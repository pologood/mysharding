package com.jd.promo.sharding.mybatis.router;

import com.jd.promo.sharding.mybatis.ShardCondition;
import com.jd.promo.sharding.mybatis.exception.ExceptCodeConstant;
import com.jd.promo.sharding.mybatis.exception.ShardRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/09/07
 * @Version: 1.0.0
 */
public abstract class AbstractRouter implements Router {
    private static final Logger logger = LoggerFactory.getLogger(AbstractRouter.class);
    private RouteCondition routeCondition = null;

    public AbstractRouter(RouteCondition routeCondition) {
        this.routeCondition = routeCondition;
    }

    @Override
    public ShardCondition doRoute(Map<String, Object> routerKey) {
        if (this.routeCondition == null) {
            logger.error("doRoute routeCondition is null");
            throw new ShardRuntimeException("doRoute routeCondition is null", ExceptCodeConstant.NoRouteCondition);
        }
        boolean isThrowExcp = false;
        int tableCount = 0;
        try {
            tableCount = Integer.parseInt(routeCondition.getCondition(CommonConditionEnum.tableCount.name()));
        } catch (Exception e) {
            isThrowExcp = true;
        }
        if (isThrowExcp || tableCount == 0) {
            logger.error("doRoute TableCount not config,name is {}", routeCondition.getLogicTableName());
            throw new ShardRuntimeException("doRoute TableCount not config", ExceptCodeConstant.TableCountRequery);
        }

        int dbCount = 0;
        try {
            dbCount = Integer.parseInt(routeCondition.getCondition(CommonConditionEnum.dbCount.name()));
        } catch (Exception e) {
            isThrowExcp = true;
        }
        if (isThrowExcp || dbCount == 0) {
            logger.error("doRoute dbCount not config,name is {}", routeCondition.getLogicTableName());
            throw new ShardRuntimeException("doRoute dbCount not config", ExceptCodeConstant.DbCountRequery);
        }

        String routeFieldStr = routeCondition.getCondition(CommonConditionEnum.routeField.name());
        if (routeFieldStr == null || "".equals(routeFieldStr.trim())) {
            logger.error("doRoute RouteField not config,name is {}", routeCondition.getLogicTableName());
            throw new ShardRuntimeException("doRoute RouteField not config", ExceptCodeConstant.RouteFieldRequery);
        }

        Object obj = routerKey.get(routeFieldStr);

        if (obj == null) {
            logger.error("dbRequest parmet not contan routerKey name is {},routeField is {}", routeCondition.getLogicTableName(), routeFieldStr);
            throw new ShardRuntimeException("dbRequest parmet not contan routerKey", ExceptCodeConstant.RouteFieldRequery);
        }

        String tableSuffix = getTableSuffix(obj,dbCount,tableCount);

        String databaseSuffixd = getDatabaseSuffixd(obj,dbCount,tableCount);

        ShardCondition shardCondition = new ShardCondition();
        shardCondition.setDatabaseSuffix(databaseSuffixd);
        shardCondition.setTableSuffix(tableSuffix);
        return shardCondition;
    }

    /**
     * 计算表后缀
     * @param shardFiled
     * @param dbCount
     * @param tableCount
     * @return
     */
    protected abstract String getTableSuffix(Object shardFiled,int dbCount,int tableCount);

    /**
     * 计算库后缀
     * @param shardFiled
     * @param dbCount
     * @param tableCount
     * @return
     */
    protected abstract String getDatabaseSuffixd(Object shardFiled,int dbCount,int tableCount);

    /**
     * 获取路由条件信息
     * @return
     */
    public RouteCondition getRouteCondition(){
        return this.routeCondition;
    }

    protected String[] createIndices(int startWith, String prefix, int count, boolean needPadToAlign) {
        String[] indices = new String[count];
        int indexNumberSize = String.valueOf(count).length();
        int dbNum = startWith;
        for (int i = 0; i < count; i++) {
            String index = String.valueOf(dbNum++);
            if (needPadToAlign) {
                int padCount = indexNumberSize - index.length();
                while (padCount-- > 0) {
                    index = "0" + index;
                }
            }
            indices[i] = prefix + index;
        }
        return indices;
    }

}
