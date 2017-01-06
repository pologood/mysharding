package com.jd.promo.sharding.mybatis.test.dao;

import com.jd.promo.sharding.mybatis.test.entity.SysUser;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/22
 * @Version: 1.0.0
 * 用户dao
 */
public interface SysUserDao {
    int deleteByPrimaryKey(Long userId);

    int insert(SysUser record);

    SysUser selectByPrimaryKey(Long userId);

    SysUser selectByUsername(String username);

    int updateByPrimaryKeySelective(SysUser record);
}
