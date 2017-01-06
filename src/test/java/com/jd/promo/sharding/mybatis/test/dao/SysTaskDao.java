package com.jd.promo.sharding.mybatis.test.dao;

import com.jd.promo.sharding.mybatis.test.entity.SysTask;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/09/07
 * @Version: 1.0.0
 * 用户dao
 */
public interface SysTaskDao {

    int insert(SysTask record);

    SysTask selectByUserId(Long userId);

}
