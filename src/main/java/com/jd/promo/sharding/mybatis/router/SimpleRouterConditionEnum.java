package com.jd.promo.sharding.mybatis.router;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/30
 * @Version: 1.0.0
 */
public enum SimpleRouterConditionEnum {
    /**
     * 数据库开始索引
     */
    dbStartWith,
    /**
     * 表开始索引
     */
    tableStartWith,
    /**
     * 数据库前缀分隔符
     */
    dbSplit,
    /**
     * 表前缀分隔符
     */
    tableSplit,
    /**
     * 库名和表名是否需要通过填充达到名称长度的对齐
     */
    needPadToAlign
}
