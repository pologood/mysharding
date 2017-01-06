package com.jd.promo.sharding.mybatis.router;

import com.jd.promo.sharding.mybatis.ShardCondition;
import com.jd.promo.sharding.mybatis.builder.ShardConfigHolder;
import com.jd.promo.sharding.mybatis.builder.ShardConfigParser;
import com.jd.promo.sharding.mybatis.exception.ExceptCodeConstant;
import com.jd.promo.sharding.mybatis.exception.ShardRuntimeException;
import org.apache.ibatis.io.Resources;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/24
 * @Version: 1.0.0
 * 路由算法测试
 */
public class TestRouterByConfigFile {
    ShardConfigHolder shardConfigHolder = null;
    InputStream input = null;

    @Before
    public void init() {
        try {
            input = Resources.getResourceAsStream("mybatis/config.xml");
            shardConfigHolder = ShardConfigParser.parse(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetShardCondition() {
        Router router = RouteFactor.getRouter("sys_user".toUpperCase());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", 4L);
        ShardCondition shardCondition = router.doRoute(map);


        Assert.assertNotNull(shardCondition);
        Assert.assertEquals(shardCondition.getTableSuffix(), "_1");
        Assert.assertEquals(shardCondition.getDatabaseSuffix(), "0");
    }

    @Test
    public void testGetShardConditionExtend() {
        Router router = RouteFactor.getRouter("sys_user_t".toUpperCase());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", 4L);
        ShardCondition shardCondition = router.doRoute(map);


        Assert.assertNotNull(shardCondition);
        Assert.assertEquals(shardCondition.getTableSuffix(), "_1");
        Assert.assertEquals(shardCondition.getDatabaseSuffix(), "-0");
    }

    @Test
    public void testGetShardConditionCyclicDependency() {
        try {
            Router router = RouteFactor.getRouter("sys_user_t_d".toUpperCase());
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("userId", 4L);
            ShardCondition shardCondition = router.doRoute(map);
        }catch (Exception e){
            Assert.assertTrue(ExceptCodeConstant.CyclicDependency.equals(((ShardRuntimeException) e).getCode()));
        }
    }


    @Test
    public void testPrintShardCondition() {
        Router router = RouteFactor.getRouter("sys_user".toUpperCase());
        for (int i = 0; i < 10; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("userId", Long.valueOf(i));
            ShardCondition shardCondition = router.doRoute(map);
            System.out.println(i + ">>>" + shardCondition);
        }

        Router routerRole = RouteFactor.getRouter("sys_role".toUpperCase());
        for (int i = 0; i < 10; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("roleId", Long.valueOf(i));
            ShardCondition shardCondition = routerRole.doRoute(map);
            System.out.println(i + ">>>" + shardCondition);
        }


    }

}
