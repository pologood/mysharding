package com.jd.promo.sharding.mybatis;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/21
 * @Version: 1.0.0
 */
public interface ShardConnection extends Connection {
    void prepare(ShardCondition condition) throws SQLException;
}
