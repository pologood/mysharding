package com.jd.promo.sharding.mybatis.test.routerext;

import com.jd.promo.sharding.mybatis.router.RouteCondition;
import com.jd.promo.sharding.mybatis.router.SimpleRouter;

/**
 * Created by zhouchangjiang on 2016/9/7.
 */
public class MyRouter extends SimpleRouter {
    public MyRouter(RouteCondition routeCondition) {
        super(routeCondition);
    }

    @Override
    protected String getTableSuffix(Object shardFiled, int dbCount, int tableCount) {
        String tableSuffix = getRouteCondition().getCondition("tableSuffix") == null ? "" : getRouteCondition().getCondition("tableSuffix").trim();
        return tableSuffix;
    }
}
