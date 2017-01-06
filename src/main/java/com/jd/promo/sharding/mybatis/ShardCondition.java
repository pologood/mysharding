package com.jd.promo.sharding.mybatis;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/21
 * @Version: 1.0.0
 */
public final class ShardCondition {
    /**
     * 库后缀
     */
    private String databaseSuffix;

    /**
     * 表后缀
     */
    private String tableSuffix;

    public String getDatabaseSuffix() {
        return databaseSuffix;
    }

    /**
     * 生成特殊字符分隔的库后缀 表后缀
     *
     * @param split
     * @return
     */
    public String getShardConditionInfo(String split) {
        StringBuilder sb = new StringBuilder();
        sb.append(databaseSuffix == null ? "" : databaseSuffix);
        sb.append(split);
        sb.append(tableSuffix == null ? "" : tableSuffix);
        return sb.toString();
    }

    public void setDatabaseSuffix(String databaseSuffix) {
        this.databaseSuffix = databaseSuffix;
    }

    public String getTableSuffix() {
        return tableSuffix;
    }

    public void setTableSuffix(String tableSuffix) {
        this.tableSuffix = tableSuffix;
    }

    @Override
    public String toString() {
        return "ShardCondition[databaseSuffix=" + databaseSuffix + ",tableSuffix=" + tableSuffix + "]";
    }
}
