package com.jd.promo.sharding.mybatis.test.dao;

import com.jd.promo.sharding.mybatis.test.entity.SysRole;

import java.util.List;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/30
 * @Version: 1.0.0
 * 用户dao
 */
public interface SysRoleDao {

    int insert(SysRole record);

    SysRole selectByPrimaryKey(Long roleId);

    List<SysRole> queryUserRole(SysRole sysRole);

}
