package com.jd.promo.sharding.mybatis.router;

import com.jd.promo.sharding.mybatis.exception.ExceptCodeConstant;
import com.jd.promo.sharding.mybatis.exception.ShardRuntimeException;
import com.jd.promo.sharding.mybatis.util.MurmurHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/22
 * @Version: 1.0.0
 */
public class SimpleRouter extends AbstractRouter {
    private static final Logger logger = LoggerFactory.getLogger(SimpleRouter.class);

    public SimpleRouter(RouteCondition routeCondition) {
        super(routeCondition);
    }

    @Override
    protected String getTableSuffix(Object shardFiled, int dbCount, int tableCount) {
        int tableStartWith;
        try {
            tableStartWith = Integer.parseInt(getRouteCondition().getCondition(SimpleRouterConditionEnum.tableStartWith.name()));
        } catch (Exception e) {
            logger.error("doSimpleRouter tableStartWith not config,name is {}", getRouteCondition().getLogicTableName());
            throw new ShardRuntimeException("doSimpleRouter tableStartWith not config", ExceptCodeConstant.TableStartRequery);
        }
        String tableSplit = getRouteCondition().getCondition(SimpleRouterConditionEnum.tableSplit.name()) == null ? "" : getRouteCondition().getCondition(SimpleRouterConditionEnum.tableSplit.name()).trim();

        String needPadToAlignStr = getRouteCondition().getCondition(SimpleRouterConditionEnum.needPadToAlign.name()) == null ? "" : getRouteCondition().getCondition(SimpleRouterConditionEnum.needPadToAlign.name());
        boolean needPadToAlign = "true".equals(needPadToAlignStr.trim()) ? true : false;

        int tableIndex = (int) (Math.abs(MurmurHash.hash(String.valueOf(shardFiled))) % tableCount);
        String tableSuffix = createIndices(tableStartWith, tableSplit, tableCount, needPadToAlign)[tableIndex];
        return tableSuffix;
    }

    @Override
    protected String getDatabaseSuffixd(Object shardFiled, int dbCount, int tableCount) {
        int dbStartWith;
        try {
            dbStartWith = Integer.parseInt(getRouteCondition().getCondition(SimpleRouterConditionEnum.dbStartWith.name()));
        } catch (Exception e) {
            logger.error("doSimpleRouter dbStartWith not config,name is {}", getRouteCondition().getLogicTableName());
            throw new ShardRuntimeException("doSimpleRouter dbStartWith not config", ExceptCodeConstant.DbStartRequery);
        }
        String dbSplit = getRouteCondition().getCondition(SimpleRouterConditionEnum.dbSplit.name()) == null ? "" : getRouteCondition().getCondition(SimpleRouterConditionEnum.dbSplit.name()).trim();
        int tablesCountPerDb = (tableCount / dbCount);
        int dbIndex = (int) (Math.abs(MurmurHash.hash(String.valueOf(shardFiled))) % tableCount / tablesCountPerDb);
        String needPadToAlignStr = getRouteCondition().getCondition(SimpleRouterConditionEnum.needPadToAlign.name()) == null ? "" : getRouteCondition().getCondition(SimpleRouterConditionEnum.needPadToAlign.name());
        boolean needPadToAlign = "true".equals(needPadToAlignStr.trim()) ? true : false;
        String databaseSuffixd = createIndices(dbStartWith, dbSplit, dbCount, needPadToAlign)[dbIndex];
        return databaseSuffixd;
    }
}
