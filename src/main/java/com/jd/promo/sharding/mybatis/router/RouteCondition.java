package com.jd.promo.sharding.mybatis.router;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/21
 * @Version: 1.0.0
 * 路由配置信息
 */
public class RouteCondition {
    /**
     * 逻辑表名称
     */
    private String logicTableName;
    /**
     * 路由算法配置
     */
    private String routerName;
    /**
     * 继承配置
     */
    private String parent;
    /**
     *路由配置明细
     */
    private Map<String, String> conditionInfo = new HashMap<String, String>();
    public RouteCondition(String logicTableName){
        this.logicTableName=logicTableName;
    }

    /**
     * 设置配置属性值
     * @param key
     * @param value
     */
    public void addCondition(String key,String value){
        this.conditionInfo.put(key,value);
    }

    /**
     * 批量设置属性值
     * @param addConditionInfo
     */
    public void addCondition(Map<String,String> addConditionInfo){
        this.conditionInfo.putAll(addConditionInfo);
    }

    /**
     * 获取具体的配置信息
     * @param key
     * @return
     */
    public String getCondition(String key){
        return this.conditionInfo.get(key);
    }

    /**
     * 返回所有的配置信息
     * @return
     */
    public Map<String, String> getAllCondition(){
        return conditionInfo;
    }

    public String getLogicTableName() {
        return logicTableName;
    }

    public void setLogicTableName(String logicTableName) {
        this.logicTableName = logicTableName;
    }

    public String getRouterName() {
        return routerName;
    }

    public void setRouterName(String routerName) {
        this.routerName = routerName;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
