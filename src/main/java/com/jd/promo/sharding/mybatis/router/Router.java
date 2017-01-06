package com.jd.promo.sharding.mybatis.router;

import com.jd.promo.sharding.mybatis.ShardCondition;

import java.util.Map;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/21
 * @Version: 1.0.0
 * 数据源的路由器接口，负责根据条件选出
 */
public interface Router {
    /**
     * 执行具体的数据源的路由
     *
     * @param routerKey
     * @return
     */
    ShardCondition doRoute(Map<String, Object> routerKey);

}
