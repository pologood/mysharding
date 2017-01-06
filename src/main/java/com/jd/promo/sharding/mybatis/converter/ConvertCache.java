package com.jd.promo.sharding.mybatis.converter;

import com.jd.promo.sharding.mybatis.ShardCondition;
import com.jd.promo.sharding.mybatis.util.ShardPluginUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/29
 * @Version: 1.0.0
 * sql解析缓存
 */
public class ConvertCache {
    /**
     * 缓存 sql mapperid 对应的表名称
     */
    private static final ConcurrentMap<String,List<String>> SqlTables = new ConcurrentHashMap<String, List<String>>();
    /**
     * 解析 sql mapperid 对应库表的sql语句
     */
    private static final ConcurrentMap<String,String> ConvertedSqls = new ConcurrentHashMap<String, String>();

    /**
     * 获取 mapperId 对应的表名称
     * @param mapperId mybatis sql 编号
     * @return
     */
    public static List<String> getSqlTables(String mapperId){
        return SqlTables.get(mapperId);
    }

    /**
     * 添加表到缓存
     * @param mapperId mybatis sql 编号
     * @param tables mybatis sql 包含的表
     */
    public static void addSqlTables(String mapperId,List<String> tables){
        if(tables!=null&&tables.size()>0){
            SqlTables.put(mapperId,tables);
        }
    }

    /**
     * 从缓存中获取SQL语句
     * @param mapperId       mybatis sql 编号
     * @param tableNames     mybatis sql 包含的表
     * @param shardConditionMap sql中各表对应的后缀
     * @return
     */
    public static String getConvertedSql(String mapperId,List<String> tableNames,Map<String,ShardCondition> shardConditionMap){
        String key = ShardPluginUtils.generateSqlCacheKey(mapperId,tableNames,shardConditionMap);
        return ConvertedSqls.get(key);
    }

    /**
     * 添加sql缓存
     * @param mapperId             mybatis sql 编号
     * @param tableNames           mybatis sql 包含的表
     * @param shardConditionMap    sql中各表对应的后缀 用于产生物理表名称
     * @param sql                  生成的具体sql
     */
    public static void addConvertedSql(String mapperId,List<String> tableNames,Map<String,ShardCondition> shardConditionMap,String sql){
        String key = ShardPluginUtils.generateSqlCacheKey(mapperId, tableNames,shardConditionMap);
        ConvertedSqls.put(key,sql);
    }
}
