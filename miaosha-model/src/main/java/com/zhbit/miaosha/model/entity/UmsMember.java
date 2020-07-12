package com.zhbit.miaosha.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员实体类
 */
@Data
public class UmsMember implements Serializable {
    private static final long serialVersionUID = -8225433383184163688L;
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态，启用-1，禁用-0
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
