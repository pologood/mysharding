package com.jd.promo.sharding.mybatis.builder;

import com.jd.promo.sharding.mybatis.router.RouteCondition;
import junit.framework.Assert;
import org.apache.ibatis.io.Resources;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/24
 * @Version: 1.0.0
 * 配置解析测试
 */
public class TestShardingConfigParser {
    InputStream input=null;
    @Before
    public void init() {
        try {
            input = Resources.getResourceAsStream("mybatis/config.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testFile(){
        Assert.assertNotNull(input);
    }
    @Test
    public void testParseRoute() throws Exception {
        ShardConfigHolder shardConfigHolder = ShardConfigParser.parse(input);
        Assert.assertNotNull(shardConfigHolder.getRouter("SimpleRouter"));
    }
    @Test
    public void testParseShardCondition() throws Exception {
        ShardConfigHolder shardConfigHolder = ShardConfigParser.parse(input);
        RouteCondition routeCondition = shardConfigHolder.getRouteCondition("sys_user".toUpperCase());
        Assert.assertNotNull(routeCondition);
        Assert.assertEquals("defalt", routeCondition.getParent());
    }
    @Test
    public void testConditionInfo() throws Exception {
        ShardConfigHolder shardConfigHolder = ShardConfigParser.parse(input);
        RouteCondition routeCondition = shardConfigHolder.getRouteCondition("defalt".toUpperCase());
        Assert.assertNotNull(routeCondition);
        Assert.assertNull(routeCondition.getParent());
        Assert.assertEquals("DEFALT",routeCondition.getLogicTableName());
        Assert.assertEquals("2",routeCondition.getCondition("dbCount"));
        Assert.assertEquals("userId",routeCondition.getCondition("routeField"));
        Assert.assertEquals("0",routeCondition.getCondition("dbStartWith"));
    }
}
