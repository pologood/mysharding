package com.jd.promo.sharding.mybatis.router;

import com.jd.promo.sharding.mybatis.builder.ShardConfigHolder;
import com.jd.promo.sharding.mybatis.exception.ExceptCodeConstant;
import com.jd.promo.sharding.mybatis.exception.ShardRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/21
 * @Version: 1.0.0
 * 路由条件的参数设置
 */
public class RouteFactor {
    private static final Logger logger = LoggerFactory.getLogger(RouteFactor.class);
    private static ConcurrentHashMap<String, Router> routerCache = new ConcurrentHashMap<String, Router>();

    public static Router getRouter(String name) {
        if (!routerCache.containsKey(name)) {
            ShardConfigHolder shardConfigHolder = ShardConfigHolder.getInstance();
            RouteCondition routeCondition = generateConditionPropertiesByParent(name);
            if (routeCondition == null) {
                logger.error("No routeCondition,name is {}", name);
                throw new ShardRuntimeException("No routeCondition", ExceptCodeConstant.NoRouteCondition);
            }
            Class router = shardConfigHolder.getRouter(routeCondition.getRouterName());
            if (router == null) {
                logger.error("No define router,name is {}", name);
                throw new ShardRuntimeException("No define router", ExceptCodeConstant.NoDefineRouter);
            }
            Constructor constructor = null;
            try {
                constructor = router.getDeclaredConstructor(RouteCondition.class);
            } catch (NoSuchMethodException e) {
                logger.error("Router has no  Constructor,name is {}", name, e);
                throw new ShardRuntimeException("Router has no  Constructor", ExceptCodeConstant.RouterHasNoConstructor);
            }
            Router routerImpl = null;
            try {
                routerImpl = (Router) constructor.newInstance(routeCondition);
            } catch (Exception e) {
                logger.error("Router init error,name is {}", name, e);
                throw new ShardRuntimeException("Router init error", ExceptCodeConstant.RouterInitError);
            }
            routerCache.put(name, routerImpl);
        }
        return routerCache.get(name);
    }

    /**
     * 复制路由条件
     *
     * @param from
     * @param to
     */
    private static void copyRouteConditionProperties(RouteCondition from, RouteCondition to) {
        if (from != null && to != null) {
            if (from.getRouterName() != null && !"".equals(from.getRouterName().trim())) {
                to.setRouterName(from.getRouterName());
            }
            to.addCondition(from.getAllCondition());
        }
    }

    /**
     * 根据继承关系生成配置信息
     */
    private static RouteCondition generateConditionPropertiesByParent(String name) {
        ShardConfigHolder shardConfigHolder = ShardConfigHolder.getInstance();
        RouteCondition routeConditionName = shardConfigHolder.getRouteCondition(name);//加载路由规则
        RouteCondition routeCondition = null;
        if (routeConditionName != null) {
            routeCondition = new RouteCondition(name);
            List<RouteCondition> parentList = new ArrayList<RouteCondition>();
            RouteCondition tmp = routeConditionName;
            do {
                if (isCyclicDependency(tmp,parentList)) {
                    logger.error("RouteCondition Cyclic dependency by parent,name is {}", name);
                    throw new ShardRuntimeException("RouteCondition Cyclic dependency by parent", ExceptCodeConstant.CyclicDependency);
                }
                parentList.add(tmp);
                tmp = shardConfigHolder.getRouteCondition(tmp.getParent());
            } while (tmp != null);
            for (int i = parentList.size() - 1; i >= 0; i--) {
                copyRouteConditionProperties(parentList.get(i), routeCondition);
            }
        }
        return routeCondition;
    }

    /**
     * 校验是否存在循环依赖
     *
     * @param tmp
     * @param parentList
     * @return
     */
    private static boolean isCyclicDependency(RouteCondition tmp, List<RouteCondition> parentList) {
        for (RouteCondition routeCondition : parentList) {
            if(tmp.getParent()!=null&&!"".equals(tmp.getParent().trim())&& tmp.getParent().equals(routeCondition.getLogicTableName())){
                return true;
            }
        }
        return false;
    }
}
