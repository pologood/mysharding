/**
 *
 */
package com.jd.promo.sharding.mybatis.parser;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.util.deparser.StatementDeParser;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/20
 * @Version: 1.0.0
 */
public class DeleteSqlParser implements SqlParser {

    private boolean inited = false;

    private Delete statement;

    private List<Table> tables = new ArrayList<Table>();

    public DeleteSqlParser(Delete statement) {
        this.statement = statement;
    }

    @Override
    public List<Table> getTables() {
        return tables;
    }

    public void init() {
        if (inited) {
            return;
        }
        inited = true;
        tables.add(statement.getTable());
    }

    @Override
    public String toSQL() {
        StatementDeParser deParser = new StatementDeParser(new StringBuilder());
        statement.accept(deParser);
        return deParser.getBuffer().toString();
    }

}