package com.zhbit.miaosha.security.bo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhbit.miaosha.common.Constant;
import com.zhbit.miaosha.model.entity.UmsMember;
import com.zhbit.miaosha.model.entity.UmsRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * SpringSecurity需要的用户详情
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    private static final long serialVersionUID = 7560963438800089204L;
    /**
     * 会员登录实体
     */
    private UmsMember umsMember;
    /**
     * 用户角色列表
     */
    private List<String> roles;
    /**
     * 用户权限列表
     */
    private Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal create(UmsMember umsMember, List<UmsRole> roles) {
        List<String> roleNames = roles.stream()
                .map(UmsRole::getName)
                .collect(Collectors.toList());
        //TODO  permissions
//        List<GrantedAuthority> authorities = permissions.stream()
//                .filter(permission -> StrUtil.isNotBlank(permission.getPermission()))
//                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
//                .collect(Collectors.toList());

        return new UserPrincipal(umsMember, roleNames,null);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return umsMember.getPassword();
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        //TODO 暂时只能邮箱登录
        return umsMember.getEmail();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return Objects.equals(umsMember.getStatus(), Constant.ENABLE);
    }
}
