package com.jd.promo.sharding.mybatis.parser;

import net.sf.jsqlparser.schema.Table;

import java.util.List;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/20
 * @Version: 1.0.0
 */
public interface SqlParser {

    public List<Table> getTables();

    public String toSQL();

}
