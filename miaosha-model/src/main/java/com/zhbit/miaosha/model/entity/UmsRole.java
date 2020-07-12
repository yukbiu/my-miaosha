package com.zhbit.miaosha.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户角色
 */
@Data
public class UmsRole implements Serializable {
    private static final long serialVersionUID = -8997316749468211834L;
    /**
     * 主键
     */
    private Long id;

    /**
     * 角色名
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
