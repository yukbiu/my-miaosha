package com.zhbit.miaosha.dao;

import com.zhbit.miaosha.model.entity.UmsMember;
import com.zhbit.miaosha.model.entity.UmsRole;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UmsMemberDao {
    /**
     * 根据用户名查找用户
     * @param username
     * @return 会员UmsMember
     */
    UmsMember findUserByUsername(String username);

    /**
     * 根据会员id查找用户角色
     * @param userId
     * @return 会员拥有的角色集合
     */
    List<UmsRole> selectByUserId(long userId);

    void addUser(UmsMember umsMember);
}
