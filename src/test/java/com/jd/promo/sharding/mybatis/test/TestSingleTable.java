package com.jd.promo.sharding.mybatis.test;

import com.jd.promo.sharding.mybatis.test.dao.SysUserDao;
import com.jd.promo.sharding.mybatis.test.entity.SysUser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/22
 * @Version: 1.0.0
 * 数据库操作与事物测试
 */
public class TestSingleTable {
    private ClassPathXmlApplicationContext context;
    private SysUserDao sysUserDao=null;
    private TransactionTemplate transactionTemplate;
    @Before
    public void init() {
        this.context = new ClassPathXmlApplicationContext("spring-config.xml");
        this.sysUserDao = context.getBean(SysUserDao.class);
        this.transactionTemplate = context.getBean(TransactionTemplate.class);
    }
    @Test
    public void testSysUserDao(){
        SysUser sysUser = new SysUser();
        sysUser.setUserId(1L);
        sysUser.setUsername("测试用户111");
        sysUser.setPassword("123456");
        sysUser.setSalt("QQQQQQWWWWEERR");
        sysUserDao.insert(sysUser);
        SysUser rslt = sysUserDao.selectByPrimaryKey(1L);
        Assert.assertNotNull(rslt);
        Assert.assertEquals(sysUser.getUsername(), rslt.getUsername());
    }

    /**
     * 相同数据源 同一事务
     */
    @Test
    public void testTransactionOneDb(){
        try{
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    SysUser sysUser = new SysUser();
                    sysUser.setUserId(1L);
                    sysUser.setUsername("测试用户1");
                    sysUser.setPassword("123456");
                    sysUser.setSalt("QQQQQQWWWWEERR");
                    sysUserDao.insert(sysUser);
                    sysUser = new SysUser();
                    sysUser.setUserId(2L);
                    sysUser.setUsername("测试用户0");
                    sysUser.setPassword("123456");
                    sysUser.setSalt("QQQQQQWWWWEERR");
                    sysUserDao.insert(sysUser);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        SysUser rslt = sysUserDao.selectByPrimaryKey(1L);
        Assert.assertNotNull(rslt);
        SysUser rslt1 = sysUserDao.selectByPrimaryKey(2L);
        Assert.assertNotNull(rslt1);
    }
    /**
     * 相同数据源 事务回滚
     */
    @Test
    public void testTransactionBack(){
        try{
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    SysUser sysUser = new SysUser();
                    sysUser.setUserId(1L);
                    sysUser.setUsername("测试用户1");
                    sysUser.setPassword("123456");
                    sysUser.setSalt("QQQQQQWWWWEERR");
                    sysUserDao.insert(sysUser);

                    if (true) {
                        throw new RuntimeException();
                    }

                    sysUser = new SysUser();
                    sysUser.setUserId(2L);
                    sysUser.setUsername("测试用户0");
                    sysUser.setPassword("123456");
                    sysUser.setSalt("QQQQQQWWWWEERR");
                    sysUserDao.insert(sysUser);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        SysUser rslt = sysUserDao.selectByPrimaryKey(1L);
        Assert.assertNull(rslt);
        SysUser rslt1 = sysUserDao.selectByPrimaryKey(2L);
        Assert.assertNull(rslt1);
    }
    /**
     * 同一个事务回滚，多个数据源
     */
    @Test
    public void testTransactionMoreDataSource(){
        try{
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    SysUser sysUser = new SysUser();
                    sysUser.setUserId(0L);
                    sysUser.setUsername("测试用户1");
                    sysUser.setPassword("123456");
                    sysUser.setSalt("QQQQQQWWWWEERR");
                    sysUserDao.insert(sysUser);


                    sysUser = new SysUser();
                    sysUser.setUserId(1L);
                    sysUser.setUsername("测试用户0");
                    sysUser.setPassword("123456");
                    sysUser.setSalt("QQQQQQWWWWEERR");
                    sysUserDao.insert(sysUser);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        SysUser rslt = sysUserDao.selectByPrimaryKey(1L);
        Assert.assertNull(rslt);
        SysUser rslt1 = sysUserDao.selectByPrimaryKey(0L);
        Assert.assertNull(rslt1);
    }

    /**
     * 验证sql解析缓存效果
     */
    @Test
    public void testQuerySqlCache(){
        SysUser sysUser = new SysUser();
        sysUser.setUserId(1L);
        sysUser.setUsername("测试用户111");
        sysUser.setPassword("123456");
        sysUser.setSalt("QQQQQQWWWWEERR");
        sysUserDao.insert(sysUser);
        long time1 = System.nanoTime();
        SysUser rslt = sysUserDao.selectByPrimaryKey(1L);
        long time2 =System.nanoTime();
        rslt = sysUserDao.selectByPrimaryKey(1L);
        long time3 = System.nanoTime();
        rslt = sysUserDao.selectByPrimaryKey(1L);
        long time4 = System.nanoTime();
        System.out.println(time2-time1);
        System.out.println(time3-time2);
        System.out.println(time4-time3);
        Assert.assertTrue(time2-time1>time3-time2);
        Assert.assertNotNull(rslt);
        Assert.assertEquals(sysUser.getUsername(), rslt.getUsername());
    }
}
