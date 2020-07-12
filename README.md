## 1、功能模块

- 用户中心
- 商品-库存中心
- 订单中心
- 活动中心（秒杀）

## 2、架构设计

### 2.1 RabbitMQ

#### MQ的作用

**1）解耦**：在项目启动之初是很难预测未来会遇到什么困难的，消息中间件在处理过程中插入了一个隐含的，基于数据的接口层，两边都实现这个接口，这样就允许独立的修改或者扩展两边的处理过程，只要两边遵守相同的接口约束即可。
 **2）冗余（存储）**：在某些情况下处理数据的过程中会失败，消息中间件允许把数据持久化直到他们完全被处理
 **3）扩展性**：消息中间件解耦了应用的过程，所以提供消息入队和处理的效率是很容易的，只需要增加处理流程就可以了。
 **4）削峰**：在访问量剧增的情况下，但是应用仍然需要发挥作用，但是这样的突发流量并不常见。而使用消息中间件采用队列的形式可以减少突发访问压力，不会因为突发的超时负荷要求而崩溃
 **5）可恢复性**：当系统一部分组件失效时，不会影响到整个系统。消息中间件降低了进程间的耦合性，当一个处理消息的进程挂掉后，加入消息中间件的消息仍然可以在系统恢复后重新处理
 **6）顺序保证**：在大多数场景下，处理数据的顺序也很重要，大部分消息中间件支持一定的顺序性
 **7）缓冲**：消息中间件通过一个缓冲层来帮助任务最高效率的执行
 **8）异步通信**：通过把消息发送给消息中间件，消息中间件异步消费。



#### 削峰限流

设计一：

<img src="C:\Users\acer\Desktop\秒杀系统\images\image-20200706172144164.png" alt="image-20200706172144164" style="zoom:80%;" />

设计二：

<img src="C:\Users\acer\Desktop\秒杀系统\images\image-20200706172510601.png" alt="image-20200706172510601" style="zoom:80%;" />

设置消息队列容量大小，当队列满载时，此时再发送的消息就会被丢弃。

应用于限制秒杀请求，当秒杀请求达到一定数量时，其余请求将会被抛弃不进行处理，一定程度上减少了服务器处理秒杀请求的压力，从而达到削峰限流的效果。（从根源上限制了高并发流量）

MQ有界消息队列：

```java
/**
 * 秒杀请求队列
 */
@Bean
public Queue orderSeckillQueue() {
    return QueueBuilder
            .durable(QUEUE_ORDER_SECKILL)
            .maxLength(ACCESS_LIMIT)    // 削峰限流 100->限制100个请求
            .overflow(QueueBuilder.Overflow.rejectPublish)  // 拒绝策略：超过队列容量大小将丢弃消息
            .build();
}
```

#### 异步处理

采用消息的异步消费，减少服务器对客户端的响应时间，从而提高用户的体验，一定程度上解决了高并发秒杀带来的QPS低下的问题。

```java
/**
 * 消费秒杀请求
 */
@RabbitListener(queues = RabbitConfig.QUEUE_ORDER_SECKILL)
public void receiveSeckillMsg(SeckillMsg seckillMsg, Message msg, Channel channel) {
    log.info("TAG:{}",String.valueOf(msg.getMessageProperties().getDeliveryTag()));
    // 消费者消费  执行秒杀请求
    seckillService.seckill(seckillMsg.getSeckillId(),seckillMsg.getMemberId());
}
```

#### 自动取消超时订单

秒杀成功时，后台创建秒杀订单返回给客户端。订单有效时间为30分钟，超过30分钟未支付则自动取消订单。

解决方案：**RabbitMQ**延迟消息实现超时订单的取消处理

> 流程步骤：

- 根据秒杀用户生成订单
- 将订单id消息发送至**死信队列**，（设置30分钟不支付取消订单）
- 按订单超时时间发送一个延迟消息给RabbitMQ，让它在订单超时后触发取消订单的操作；
- 如果用户没有支付，进行取消订单操作（设置订单状态为交易取消，释放锁定商品库存）。

**延迟消息队列（死信队列）**

```java
public Queue orderTtlQueue() {
    return QueueBuilder
            .durable(QUEUE_TTL_ORDER)
            .deadLetterExchange(EXCHANGE_ORDER_DIRECT)//到期后转发的交换机
            .deadLetterRoutingKey(QUEUE_ORDER_CANCEL)//到期后转发的路由键
            .build();
}
```

**发送延迟消息**

```java
public void ttlOrderCancelMsg(Long orderId ,final long delayTimes) {
    // 给延迟队列发送消息
    rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_TTL_ORDER_DIRECT,
            RabbitConfig.QUEUE_TTL_ORDER,orderId,message -> {
                // 给消息设置延迟时间，延迟时间一到立马被消费
                message.getMessageProperties().setExpiration(String.valueOf(delayTimes));
                return message;
            });
}
```

**订单超时后触发取消订单的操作**

```java
@RabbitListener(queues = RabbitConfig.QUEUE_ORDER_CANCEL)
public void receiveOrderCancelMsg(Long orderId, Message msg,Channel channel) {
    log.info("超时订单取消:{}",orderId);
    // 调用超时订单取消业务
    omsOrderService.orderCancel(orderId);

}
```

### 2.2 Redis

#### 库存预热

传统的秒杀请求直接打到数据库，进行库存查询和锁定库存操作，但在巨大流量的面前，数据库将承受无比巨大的压力，最终可能导致数据库宕机的后果。

单机的数据库能承受的并发访问量是有限的，大概在200~300 QPS。

库存预热，就是在秒杀活动正式开始前，将秒杀商品的库存缓存到内存中。数据在内存中的读取是十分快速的，那么利用缓存中间件**Redis**就能提高数据的访问效率以及承受更大的并发访问量。使得用户秒杀请求不用去数据库中查询库存和减库存操作，而是直接从提前缓存好的内存中的数据进行读写，将并发压力转到了性能更高的Redis来承担，从而保障了数据库的服务质量。

单体的**Redis**最高能承受的最大并发访问量大约在10W/s

#### 内存标记

为了防止用户进行二次秒杀，将对用户秒杀成功记录进行内存标记，秒杀过的用户再次进行秒杀将会直接失败。

秒杀订单成功生成之后，将在内存中进行标记，前端访问根据用户和商品查询到订单则表示秒杀成功。

#### 缓存热点数据

将频繁访问数据库的数据缓存到内存中，以后每次访问数据就从内存中读取，而不必访问数据库，大大提高了系统的效率。

热点数据，例如登录用户信息，首页内容，用户角色权限等

### 2.3 定时任务

设置定时处理的任务

例如每天凌晨十二点进行日志分析、秒杀活动开始前定时进行库存预热

### 2.4认证＋授权

> `JWT+SpringSecurity`

#### JWT

**JSON WEB TOKEN**用户令牌、是验证访问用户的凭证，实现单点登录的实现方式之一。

**JWT的组成**

- JWT token的格式：header.payload.signature

- 头部：header中用于存放签名的生成算法

  ```json
  {"alg": "HS512"}
  ```

- 负载：payload中用于存放用户名、token的生成时间和过期时间

  ```json
  {"sub":"admin","created":1489079981393,"exp":1489684781}
  ```

- 签名：signature为以header和payload生成的签名，一旦header和payload被篡改，验证将失败

  ```java
  //secret为加密算法的密钥
  String signature = HMACSHA512(base64UrlEncode(header) + "." +base64UrlEncode(payload),secret)
  ```

可以在该网站上获得解析结果：https://jwt.io/

![img](C:\Users\acer\Desktop\秒杀系统\images\arch_screen_13.png)

#### JWT实现认证和授权的原理

- 用户调用登录接口，登录成功后获取到JWT的token；
- 之后用户每次调用接口都在http的header中添加一个叫Authorization的头，值为JWT的token；
- 后台程序通过对Authorization头中信息的解码及数字签名校验来获取其中的用户信息，从而实现认证和授权。

#### SpringSecurity

整合springboot的一套认证鉴权框架，对某些保护资源进行登录用户的认证，以及用户权限的校验。

**相关的java类说明**

- configure(HttpSecurity httpSecurity)：用于配置需要拦截的url路径、jwt过滤器及出异常后的处理器；
- configure(AuthenticationManagerBuilder auth)：用于配置UserDetailsService及PasswordEncoder；
- RestfulAccessDeniedHandler：当用户没有访问权限时的处理器，用于返回JSON格式的处理结果；
- RestAuthenticationEntryPoint：当未登录或token失效时，返回JSON格式的结果；
- UserDetailsService:SpringSecurity定义的核心接口，用于根据用户名获取用户信息，需要自行实现；
- UserDetails：SpringSecurity定义用于封装用户信息的类（主要是用户信息和权限），需要自行实现；
- PasswordEncoder：SpringSecurity定义的用于对密码进行编码及比对的接口，目前使用的是BCryptPasswordEncoder；
- JwtAuthenticationTokenFilter：在用户名和密码校验前添加的过滤器，如果有jwt的token，会自行根据token信息进行登录。

### 2.5 单点登录SSO

在Redis中集中管理用户的**JWT Token**,从而实现在多个子系统中登录会话的共享。



## 3、问题解决

### 3.1 如何解决超卖问题

**问题产生：**

在多线程并发场景的秒杀情况下，有多个请求同时对数据进行读写访问，就会造成并发问题。
例如：有两个线程同时查询到库存为1，那么它们都可以去尝试扣减库存，最后导致剩余的一个库存被两个线程所消费，扣减库存为-1，造成超卖的现象。

**问题的解决：**

在多线程并发异步的情况下，容易引发数据修改的并发问题。

那么采用同步的方式让原本并行的访问变成串行执行，就能解决并发问题。

- 线程同步的方式：加锁；锁机制又分为悲观锁和乐观锁。

典型的悲观锁就是给需要同步的方法或代码块加上`synchronized`关键字，实现线程间的同步策略；以及`Lock`锁这种显式的加锁解锁进行线程同步。

悲观锁虽然能解决高并发下数据修改产生的并发问题，但是由于线程从并行变成了串行访问，导致系统的性能大大降低了，这时候来应对高并发的流量时就显得有些性能不足了。

除了悲观锁，乐观锁相对来说性能会更好。乐观锁并不是传统意义上的加锁机制，不加锁意味着是并行访问的，由于没有了锁的竞争，线程间访问的速度也更快。对于在高并发情况下的数据安全问题，乐观锁典型的实现方式是底层采用了`CAS`算法机制，来保证一系列操作的原子性。

**实现方法：采用乐观锁机制**

传统的实现方式是将数据类型采用JDK并发包中的原子类（底层是CAS算法）

在高并发情况下，为了实现查库存—>减库存操作的原子性

1. 方式一：数据库乐观锁。

在数据库表中额外添加一个版本号字段，每次修改库存进行计数+1，在修改库存前进行版本号比对，若修改前获取的版本号和查询库存获取的版本号一致，则表示在这期间库存没有被其他线程所修改，则执行扣减库存操作；反之则不进行任何操作，并采用自旋的方式来重试。（这种方式由于是数据库层面，实际性能跟加锁没有相差太多）

![image-20200710154248831](C:\Users\acer\Desktop\秒杀系统\images\image-20200710154248831.png)

2. 方式二：Redis事务实现乐观锁

因为我们已经把库存预热到了内存中，完全可以进行内存操作，使得数据访问效率更佳。那么Redis也提供了类似于CAS算法的方式来实现乐观锁，那就是**Redis事务**

> **CAS实现机制Redis的事务**
> `MULTI`指令 :标记着事务的开始，让客户端从非事务状态切换到事务状态，客户端后续命令全部放进一个事务队列里;
> `EXEC`指令 :服务器根据客户端所保存的事务队列，以先进先出(FIFO) 的方式执行事务队列中的命令;
> `WATCH`指令:用于监视一个(或多个key，如果在事务执行之前这个(或这些)key被其他命令所改动，那么事务将被打断:

代码演示：

```java
/**
 * 减库存操作
 * @param seckillId 秒杀商品编号
 * @return
 */
private boolean updateSeckillStock(Long seckillId) {
    // 创建redis 显式事务redisTemplate.execute(SessionCallback<Object>())
    Object execute = redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            @SuppressWarnings("unchecked")
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                while (true) {  //轮询（CAS机制）
                    // 监视库存，若在事务执行中库存被修改，则放弃一切操作
                    operations.watch((K) String.valueOf(seckillId));
                    try {
                        // 根据秒杀商品id-> 查询秒杀剩余库存
                        Integer stock = (Integer) operations.opsForValue().get(String.valueOf(seckillId));
                        if (stock == null || stock == 0) {  //库存不足，直接返回false
                            return false;
                        }
                        // 尝试减库存
                        // 开启事务
                        operations.multi();
                        // 发起减库存命令进入事务队列
                        operations.opsForValue().decrement((K) String.valueOf(seckillId));
                        // 提交事务
                        List<Object> results = operations.exec();
                        if (results == null || results.isEmpty()) { // 事务提交失败，重试
                            continue;
                        } else {
                            return true; // 扣减库存成功
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    return (boolean) execute;
}
```

