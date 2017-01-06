package com.jd.promo.sharding.mybatis.builder;

import com.jd.promo.sharding.mybatis.router.RouteCondition;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/24
 * @Version: 1.0.0
 * 分库分表规则配置信息
 */
public class ShardConfigHolder {
    private static final ShardConfigHolder instance = new ShardConfigHolder();

    public static ShardConfigHolder getInstance() {
        return instance;
    }

    private ShardConfigHolder() {
    }

    /**
     * 路由算法
     */
    private Map<String, Class> routers = new HashMap<String, Class>();
    /**
     * 路由条件
     */
    private Map<String, RouteCondition> routeConditions = new HashMap<String, RouteCondition>();

    /**
     * 注册路由算法
     *
     * @param routerName
     * @param router
     */
    public void register(String routerName, Class router) {
        this.routers.put(routerName, router);
    }

    /**
     * 注册路由条件
     *
     * @param logicTableName
     * @param routeCondition
     */
    public void register(String logicTableName, RouteCondition routeCondition) {
        this.routeConditions.put(logicTableName, routeCondition);
    }

    /**
     * 获取路由算法
     *
     * @param routerName
     * @return
     */
    public Class getRouter(String routerName) {
        return routers.get(routerName);
    }

    /**
     * 获取路由条件
     *
     * @param logicTableName
     * @return
     */
    public RouteCondition getRouteCondition(String logicTableName) {
        return routeConditions.get(logicTableName);
    }

}
