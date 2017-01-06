package com.jd.promo.sharding.mybatis.test;

import com.jd.promo.sharding.mybatis.test.dao.SysTaskDao;
import com.jd.promo.sharding.mybatis.test.dao.SysUserDao;
import com.jd.promo.sharding.mybatis.test.entity.SysTask;
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
 * @Date: 2016/09/07
 * @Version: 1.0.0
 * 数据库操作与事物测试
 */
public class TestExtRouter {
    private ClassPathXmlApplicationContext context;
    private SysUserDao sysUserDao=null;
    private SysTaskDao sysTaskDao =null;
    private TransactionTemplate transactionTemplate;
    @Before
    public void init() {
        this.context = new ClassPathXmlApplicationContext("spring-config.xml");
        this.sysUserDao = context.getBean(SysUserDao.class);
        this.sysTaskDao = context.getBean(SysTaskDao.class);
        this.transactionTemplate = context.getBean(TransactionTemplate.class);
    }
    @Test
    public void testSysTaskDao(){
        SysTask sysTask = new SysTask();
        sysTask.setTaskId(2L);
        sysTask.setUserId(4L);
        sysTask.setTaskName("测试任务");
        sysTaskDao.insert(sysTask);
        SysTask newSysTask = sysTaskDao.selectByUserId(4L);
        Assert.assertNotNull(newSysTask);
        Assert.assertEquals(sysTask.getTaskName(), newSysTask.getTaskName());
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

                    SysTask sysTask = new SysTask();
                    sysTask.setTaskId(1L);
                    sysTask.setUserId(1L);
                    sysTask.setTaskName("测试任务");
                    sysTaskDao.insert(sysTask);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        SysUser rslt = sysUserDao.selectByPrimaryKey(1L);
        Assert.assertNotNull(rslt);
        SysTask rslt1 = sysTaskDao.selectByUserId(1L);
        Assert.assertNotNull(rslt1);
    }
}
