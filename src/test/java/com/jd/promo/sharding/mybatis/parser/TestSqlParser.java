package com.jd.promo.sharding.mybatis.parser;

import junit.framework.Assert;
import net.sf.jsqlparser.schema.Table;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/22
 * @Version: 1.0.0
 * 验证SQL解析
 */
public class TestSqlParser {
    @Before
    public void init() {

    }
    @Test
    public void testInsert(){
        String insertSql = "insert into SYS_USER (USER_ID, USERNAME, PASSWORD,SALT)values (1, 2, 1, 6)";
        SqlParser sqlParser = null;
        try {
            sqlParser = SqlParserFactory.getInstance().createParser(insertSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<Table> tables = sqlParser.getTables();
        Assert.assertNotNull(tables);
        Assert.assertEquals("SYS_USER",tables.get(0).getName());
    }

    @Test
    public void testUpdate(){
        String insertSql = "update SYS_USER set PASSWORD=1 where USER_ID =2";
        SqlParser sqlParser = null;
        try {
            sqlParser = SqlParserFactory.getInstance().createParser(insertSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<Table> tables = sqlParser.getTables();
        Assert.assertNotNull(tables);
        Assert.assertEquals("SYS_USER",tables.get(0).getName());
    }

    @Test
    public void testDelete(){
        String insertSql = "delete SYS_USER  where USER_ID =2";
        SqlParser sqlParser = null;
        try {
            sqlParser = SqlParserFactory.getInstance().createParser(insertSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<Table> tables = sqlParser.getTables();
        Assert.assertNotNull(tables);
        Assert.assertEquals("SYS_USER",tables.get(0).getName());
    }

    @Test
    public void testQuery(){
        String insertSql = "select * from  SYS_USER  where USER_ID =2";
        SqlParser sqlParser = null;
        try {
            sqlParser = SqlParserFactory.getInstance().createParser(insertSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<Table> tables = sqlParser.getTables();
        Assert.assertNotNull(tables);
        Assert.assertEquals("SYS_USER",tables.get(0).getName());
    }

    @Test
    public void testQueryJoin(){
        String insertSql = "select * from  SYS_USER as A left Join SYS_USER_B as B on a.id=b.id   where USER_ID =2";
        SqlParser sqlParser = null;
        try {
            sqlParser = SqlParserFactory.getInstance().createParser(insertSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<Table> tables = sqlParser.getTables();
        Assert.assertNotNull(tables);
        Assert.assertEquals(2, tables.size());
        Assert.assertEquals("SYS_USER",tables.get(0).getName());
        Assert.assertEquals("SYS_USER_B",tables.get(1).getName());
    }

}
