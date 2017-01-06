package com.jd.promo.sharding.mybatis.parser;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.sql.SQLException;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/20
 * @Version: 1.0.0
 */
public class SqlParserFactory {

    private static final Logger logger = LoggerFactory.getLogger(SqlParserFactory.class);
    private static SqlParserFactory instance = new SqlParserFactory();

    public static SqlParserFactory getInstance() {
        return instance;
    }

    private final CCJSqlParserManager manager;

    public SqlParserFactory() {
        manager = new CCJSqlParserManager();
    }

    public SqlParser createParser(String originalSql) throws SQLException {
        try {
            Statement statement = manager.parse(new StringReader(originalSql));
            if (statement instanceof Select) {
                SelectSqlParser select = new SelectSqlParser((Select) statement);
                select.init();
                return select;
            } else if (statement instanceof Update) {
                UpdateSqlParser update = new UpdateSqlParser((Update) statement);
                update.init();
                return update;
            } else if (statement instanceof Insert) {
                InsertSqlParser insert = new InsertSqlParser((Insert) statement);
                insert.init();
                return insert;
            } else if (statement instanceof Delete) {
                DeleteSqlParser delete = new DeleteSqlParser((Delete) statement);
                delete.init();
                return delete;
            } else {
                logger.error("Unsupported sql,statement is not insert,delete,update,select originalSql is {}",originalSql);
                throw new SQLException("Unsupported Parser[" + statement.getClass().getName() + "]");
            }
        } catch (JSQLParserException e) {
            logger.error("parse sql error originalSql is {}",originalSql);
            throw new SQLException("SQL Parse Failed", e);
        }

    }

}
