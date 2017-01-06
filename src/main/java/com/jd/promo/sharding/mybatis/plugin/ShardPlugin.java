package com.jd.promo.sharding.mybatis.plugin;

import com.jd.promo.sharding.mybatis.ShardCondition;
import com.jd.promo.sharding.mybatis.ShardConnection;
import com.jd.promo.sharding.mybatis.builder.ShardConfigParser;
import com.jd.promo.sharding.mybatis.converter.ShardConvert;
import com.jd.promo.sharding.mybatis.util.ReflectionUtils;
import com.jd.promo.sharding.mybatis.util.ShardPluginUtils;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/21
 * @Version: 1.0.0
 * mybatis插件实现分库 分表
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class ShardPlugin implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(ShardPlugin.class);
    /**
     * 插件配置文件名称
     */
    public static final String SHARDING_CONFIG = "shardingConfig";

    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        //获取分库分表连接
        ShardConnection conn = ShardPluginUtils.getShardConnection(args[0]);
        if (conn == null) {
            //不使用分库
            logger.warn("No Use ShardConnection");
            return invocation.proceed();
        }
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();

        MappedStatement mappedStatement = null;
        if (statementHandler instanceof RoutingStatementHandler) {
            StatementHandler delegate = (StatementHandler) ReflectionUtils.getFieldValue(statementHandler, "delegate");
            mappedStatement = (MappedStatement) ReflectionUtils.getFieldValue(delegate, "mappedStatement");
        } else {
            mappedStatement = (MappedStatement) ReflectionUtils.getFieldValue(statementHandler, "mappedStatement");
        }
        String mapperId = mappedStatement.getId();
        logger.debug("Shard mapperId:{}", mapperId);

        ShardConvert shardConvert = new ShardConvert(boundSql);
        List<String> tableNames = shardConvert.parseSqlTable(mapperId);
        if (tableNames == null || tableNames.isEmpty()) {
            return invocation.proceed();
        }
        Map<String, ShardCondition> shardConditionMap = shardConvert.generateShardCondition(tableNames);
        //选择数据源
        conn.prepare(shardConditionMap.get(tableNames.get(0)));
        //修改逻辑表为物理表
        String targetSQL = shardConvert.convertedSql(mapperId, tableNames, shardConditionMap);
        logger.debug("Shard Convert SQL:{}", targetSQL);
        ReflectionUtils.setFieldValue(boundSql, "sql", targetSQL);
        return invocation.proceed();
    }

    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    public void setProperties(Properties properties) {
        String config = properties.getProperty(SHARDING_CONFIG, null);
        if (config == null || config.trim().length() == 0) {
            logger.error("property 'shardingConfig' is requested.");
            throw new IllegalArgumentException("property 'shardingConfig' is requested.");
        }
        InputStream input = null;
        try {
            input = Resources.getResourceAsStream(config);
            ShardConfigParser.parse(input);
        } catch (IOException e) {
            logger.error("Get sharding config file failed.", e);
            throw new IllegalArgumentException(e);
        } catch (Exception e) {
            logger.error("Parse sharding config file failed.", e);
            throw new IllegalArgumentException(e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }

        }
    }
}
