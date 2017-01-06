package com.jd.promo.sharding.mybatis.converter;

import com.jd.promo.sharding.mybatis.ShardCondition;
import com.jd.promo.sharding.mybatis.exception.ExceptCodeConstant;
import com.jd.promo.sharding.mybatis.exception.ShardRuntimeException;
import com.jd.promo.sharding.mybatis.parser.SqlParser;
import com.jd.promo.sharding.mybatis.parser.SqlParserFactory;
import com.jd.promo.sharding.mybatis.router.RouteFactor;
import com.jd.promo.sharding.mybatis.router.Router;
import com.jd.promo.sharding.mybatis.util.ShardPluginUtils;
import net.sf.jsqlparser.schema.Table;
import org.apache.ibatis.mapping.BoundSql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/29
 * @Version: 1.0.0
 * 分库分表 数据库路由 sql转换
 */
public class ShardConvert {
    private static final Logger logger = LoggerFactory.getLogger(ShardConvert.class);
    private BoundSql boundSql;
    private SqlParser sqlParser;


    public ShardConvert(BoundSql boundSql) {
        this.boundSql = boundSql;
    }

    /**
     * 获取当前执行SQL表名称
     *
     * @param mapperId 缓存id
     * @return
     * @throws SQLException
     */
    public List<String> parseSqlTable(String mapperId) throws SQLException {
        List<String> tableRslt = ConvertCache.getSqlTables(mapperId);
        //缓存未命中
        if (tableRslt == null || tableRslt.isEmpty()) {
            if (this.sqlParser == null) {
                String originalSql = boundSql.getSql();
                logger.debug("Shard Original SQL:{}", originalSql);
                this.sqlParser = SqlParserFactory.getInstance().createParser(originalSql);
            }
            List<Table> tables = this.sqlParser.getTables();
            tableRslt = ShardPluginUtils.convertTableName(tables);
            ConvertCache.addSqlTables(mapperId, tableRslt);
        }
        return tableRslt;
    }

    /**
     * 根据逻辑表名称，sql参数，计算物理表名称 物理数据库名称
     * @param tableNames
     * @return
     */
    public Map<String, ShardCondition> generateShardCondition(List<String> tableNames) {
        Map<String, ShardCondition> shardConditionMap = new HashMap<String, ShardCondition>();
        Map<String, Object> params = null;
        try {
            params = ShardPluginUtils.getParams(boundSql);
        } catch (Exception e) {
            logger.error("Parse Sql Params error sql is :{}", boundSql.getSql());
            throw new ShardRuntimeException("Parse Sql Params error", ExceptCodeConstant.ParseSqlParamsError);
        }
        for (String tableName : tableNames) {
            //sql中可能包含多表，相同的表配置相同，不需要多次计算
            if(!shardConditionMap.containsKey(tableName)){
                Router router = RouteFactor.getRouter(tableName);
                ShardCondition shardCondition = router.doRoute(params);
                shardConditionMap.put(tableName, shardCondition);
            }
        }
        //校验表是否属于同一个数据库
        if (!ShardPluginUtils.isSameDb(shardConditionMap)) {
            logger.error("Multiple DB Connection In One Sql,sql is :{}", boundSql.getSql());
            throw new ShardRuntimeException("Multiple DB Connection In One Sql", ExceptCodeConstant.MultipleDBinOneSql);
        }
        return shardConditionMap;
    }


    public String convertedSql(String mapperId, List<String> tableNames, Map<String, ShardCondition> shardConditionMap) throws SQLException {
        //从缓存获取
        String sql = ConvertCache.getConvertedSql(mapperId, tableNames, shardConditionMap);
        if (null == sql || "".equals(sql.trim())) {
            if (this.sqlParser == null) {
                String originalSql = this.boundSql.getSql();
                this.sqlParser = SqlParserFactory.getInstance().createParser(originalSql);
            }
            List<Table> tables = this.sqlParser.getTables();
            //根据计算结果修改逻辑表为物理表
            /**
             * 一个sql中可能包含多个逻辑表，支持不同逻辑表不同的分表配置
             */
            ShardPluginUtils.converter(tables, shardConditionMap);

            sql = this.sqlParser.toSQL();
            ConvertCache.addConvertedSql(mapperId, tableNames, shardConditionMap, sql);
        }
        return sql;
    }
}
