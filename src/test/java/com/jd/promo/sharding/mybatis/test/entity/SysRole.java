package com.jd.promo.sharding.mybatis.test.entity;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/30
 * @Version: 1.0.0
 * 用户实体
 */
public class SysRole {
    private Long roleId;
    private Long userId;
    private String roleName;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
