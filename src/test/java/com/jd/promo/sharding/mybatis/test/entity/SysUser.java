package com.jd.promo.sharding.mybatis.test.entity;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/22
 * @Version: 1.0.0
 * 用户实体
 */
public class SysUser {
    private Long userId;

    private String username;

    private String password;

    private String salt;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
