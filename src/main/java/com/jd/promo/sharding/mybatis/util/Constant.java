package com.jd.promo.sharding.mybatis.util;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/24
 * @Version: 1.0.0
 * 反射工具类
 */
public interface Constant {
    /**
     * 公共配置名称，配置文件中，名称为空的配置为公共配置
     */
    public static final String DefaltConfigName = "#DEFALT#";



    /**
     * sql解析缓存key分隔符      数据库索引与表索引分隔符
     */
    public static final String ShardConditionInfoDbTable = "###";
    /**
     * sql解析缓存key分隔符      ShardCondition与ShardCondition之间的分隔
     */
    public static final String ShardConditionInfo = "@@@";
    /**
     * sql解析缓存key分隔符      MapperId与ShardConditionInfo之间分隔符
     */
    public static final String MapperIdAndShardConditionInfo = "&&&";
}
