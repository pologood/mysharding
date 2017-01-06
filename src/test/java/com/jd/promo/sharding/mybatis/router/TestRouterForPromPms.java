package com.jd.promo.sharding.mybatis.router;

import com.jd.promo.sharding.mybatis.ShardCondition;
import com.jd.promo.sharding.mybatis.builder.ShardConfigHolder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/24
 * @Version: 1.0.0
 * 路由算法测试
 */
public class TestRouterForPromPms {
    ShardConfigHolder shardConfigHolder = null;

    @Before
    public void init() {
        shardConfigHolder = ShardConfigHolder.getInstance();
        shardConfigHolder.register("SimpleRouter", SimpleRouter.class);

        RouteCondition routeCondition = new RouteCondition("sys_user".toUpperCase());
        routeCondition.setRouterName("SimpleRouter");
        Map<String,String> map = new HashMap<String, String>();
        map.put("dbCount", "6");
        map.put("dbSplit", "");
        map.put("dbStartWith", "0");
        map.put("needPadToAlign","false");
        map.put("routeField","userId");
        map.put("tableCount", "1536");
        map.put("tableSplit", "_");
        map.put("tableStartWith","1");
        routeCondition.addCondition(map);
        shardConfigHolder.register(routeCondition.getLogicTableName(),routeCondition);
    }
    @Test
    public void testGetShardCondition(){
        Router router = RouteFactor.getRouter("sys_user".toUpperCase());
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("userId",3129617942L);
        ShardCondition shardCondition = router.doRoute(map);

        Assert.assertNotNull(shardCondition);
        Assert.assertEquals(shardCondition.getTableSuffix(),"_1536");
        Assert.assertEquals(shardCondition.getDatabaseSuffix(),"5");
    }

}
