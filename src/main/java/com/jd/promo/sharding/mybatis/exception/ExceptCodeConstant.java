package com.jd.promo.sharding.mybatis.exception;

/**
 * Created by zhouchangjiang on 2016/8/23.
 */
public interface ExceptCodeConstant {
    public static final String Other="00000";
    /**
     * 指定逻辑表不包含路由配置
     */
    public static final String NoRouteCondition="00001";
    /**
     * 复制默认属性异常
     */
    public static final String CopyPropertiesError="00002";
    /**
     * 为配置路由算法
     */
    public static final String NoDefineRouter="00003";
    /**
     * 路由算法定义错误
     */
    public static final String RouterHasNoConstructor="00004";
    /**
     * 路由算法初始化异常
     */
    public static final String RouterInitError="00005";
    /**
     * 表数量未配置
     */
    public static final String TableCountRequery="00006";
    /**
     * 数据库数量未配置
     */
    public static final String DbCountRequery="00007";

    /**
     * 表开始索引未配置
     */
    public static final String TableStartRequery="00008";
    /**
     * 数据库开始索引未配置
     */
    public static final String DbStartRequery="00009";
    /**
     * 分表字段未配置
     */
    public static final String RouteFieldRequery="00010";
    /**
     * 同一个sql语句，涉及多个数据库链接
     */
    public static final String MultipleDBinOneSql="00011";
    /**
     * 解析SQL参数异常
     */
    public static final String ParseSqlParamsError="00012";
    /**
     * 配置信息存在循环依赖
     */
    public static final String CyclicDependency="00013";
}
