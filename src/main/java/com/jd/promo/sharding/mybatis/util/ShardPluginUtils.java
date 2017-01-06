package com.jd.promo.sharding.mybatis.util;

import com.jd.promo.sharding.mybatis.ShardCondition;
import com.jd.promo.sharding.mybatis.ShardConnection;
import net.sf.jsqlparser.schema.Table;
import org.apache.ibatis.logging.jdbc.ConnectionLogger;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.*;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/24
 * @Version: 1.0.0
 * mybatis插件工具类
 */
public class ShardPluginUtils {
    private final static Set<Class<?>> SINGLE_PARAM_CLASSES = new HashSet<Class<?>>();

    static {
        SINGLE_PARAM_CLASSES.add(int.class);
        SINGLE_PARAM_CLASSES.add(Integer.class);

        SINGLE_PARAM_CLASSES.add(long.class);
        SINGLE_PARAM_CLASSES.add(Long.class);

        SINGLE_PARAM_CLASSES.add(short.class);
        SINGLE_PARAM_CLASSES.add(Short.class);

        SINGLE_PARAM_CLASSES.add(byte.class);
        SINGLE_PARAM_CLASSES.add(Byte.class);

        SINGLE_PARAM_CLASSES.add(float.class);
        SINGLE_PARAM_CLASSES.add(Float.class);

        SINGLE_PARAM_CLASSES.add(double.class);
        SINGLE_PARAM_CLASSES.add(Double.class);

        SINGLE_PARAM_CLASSES.add(boolean.class);
        SINGLE_PARAM_CLASSES.add(Boolean.class);

        SINGLE_PARAM_CLASSES.add(char.class);
        SINGLE_PARAM_CLASSES.add(Character.class);

        SINGLE_PARAM_CLASSES.add(String.class);

    }

    public static Map<String, Object> getParams(BoundSql boundSql) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        Object parameterObject = boundSql.getParameterObject();
        Map<String, Object> params = null;
        if (SINGLE_PARAM_CLASSES.contains(parameterObject.getClass())) {
            // 单一参数
            List<ParameterMapping> mapping = boundSql.getParameterMappings();
            if (mapping != null && !mapping.isEmpty()) {
                ParameterMapping m = mapping.get(0);
                params = new HashMap<String, Object>();
                params.put(m.getProperty(), parameterObject);
            } else {
                params = Collections.emptyMap();
            }
        } else {
            // 对象参数
            if (parameterObject instanceof Map) {
                params = (Map<String, Object>) parameterObject;
            } else {
                params = new HashMap<String, Object>();
                BeanInfo beanInfo = Introspector.getBeanInfo(parameterObject.getClass());
                PropertyDescriptor[] proDescrtptors = beanInfo.getPropertyDescriptors();
                if (proDescrtptors != null && proDescrtptors.length > 0) {
                    for (PropertyDescriptor propDesc : proDescrtptors) {
                        params.put(propDesc.getName(), propDesc.getReadMethod().invoke(parameterObject));
                    }
                }
            }
        }
        return params;
    }

    /**
     * 转换sql中的逻辑表名称
     *
     * @param tables
     * @return
     */
    public static List<String> convertTableName(List<Table> tables) {
        List<String> tableNames = new ArrayList<String>();
        if (tables != null && !tables.isEmpty()) {
            for (Table table : tables) {
                String n = table.getName();
                String logicTableName = null;
                if (n.startsWith("`") && n.endsWith("`")) {
                    logicTableName = n.substring(1, n.length() - 1);
                } else {
                    logicTableName = n;
                }
                tableNames.add(logicTableName.trim().toUpperCase());
            }
        }
        return tableNames;
    }

    /**
     * 获取分库分别，数据连接对象
     *
     * @param objConn
     * @return
     */
    public static ShardConnection getShardConnection(Object objConn) {
        ShardConnection conn = null;
        if (objConn instanceof ShardConnection) {
            conn = (ShardConnection) objConn;
        } else {
            if (!Proxy.isProxyClass(objConn.getClass())) {
                return conn;
            }

            InvocationHandler handler = Proxy.getInvocationHandler(objConn);
            if (!(handler instanceof ConnectionLogger)) {
                return conn;
            }

            ConnectionLogger connLogger = (ConnectionLogger) handler;
            Connection c = connLogger.getConnection();
            if (c instanceof ShardConnection) {
                conn = (ShardConnection) c;
            } else {
                return conn;
            }
        }
        return conn;
    }

    /**
     * 校验ShardCondition 是否属于同一个数据库
     *
     * @param shardConditionMap
     * @return
     */
    public static boolean isSameDb(Map<String, ShardCondition> shardConditionMap) {
        boolean rslt = true;
        Set<String> databaseSuffixSet = new HashSet<String>();
        for (Map.Entry<String, ShardCondition> entry : shardConditionMap.entrySet()) {
            databaseSuffixSet.add(entry.getValue().getDatabaseSuffix());
            if (databaseSuffixSet.size() > 1) {
                rslt = false;
                break;
            }
        }
        return rslt;
    }

    public static String generateSqlCacheKey(String mapperId, List<String> tableNames, Map<String, ShardCondition> shardConditionMap) {
        StringBuilder sb = new StringBuilder(mapperId);
        if (tableNames != null && !tableNames.isEmpty()) {
            sb.append(Constant.MapperIdAndShardConditionInfo);
            for (int i = 0; i < tableNames.size(); i++) {
                ShardCondition shardCondition = shardConditionMap.get(tableNames.get(i));
                sb.append(shardCondition.getShardConditionInfo(Constant.ShardConditionInfoDbTable));
                if (i != tableNames.size() - 1) {
                    sb.append(Constant.ShardConditionInfo);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 根据计算结果修改逻辑表名为物理表
     *
     * @param tables
     * @param shardConditionMap
     */
    public static void converter(List<Table> tables, Map<String, ShardCondition> shardConditionMap) {
        for (Table t : tables) {
            boolean strict = false;
            String n = t.getName();
            String logicTableName = null;
            if (n.startsWith("`") && n.endsWith("`")) {
                strict = true;
                logicTableName = n.substring(1, n.length() - 1);
            } else {
                strict = false;
                logicTableName = n;
            }
            ShardCondition shardCondition = shardConditionMap.get(logicTableName);
            if (strict) {
                String targetTableName = "`" + logicTableName + shardCondition.getTableSuffix() + "`";
                t.setName(targetTableName);
            } else {
                t.setName(logicTableName + shardCondition.getTableSuffix());
            }

        }
    }
}
