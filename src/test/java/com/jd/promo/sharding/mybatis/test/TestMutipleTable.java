package com.jd.promo.sharding.mybatis.test;

import com.jd.promo.sharding.mybatis.exception.ExceptCodeConstant;
import com.jd.promo.sharding.mybatis.exception.ShardRuntimeException;
import com.jd.promo.sharding.mybatis.test.dao.SysRoleDao;
import com.jd.promo.sharding.mybatis.test.dao.SysUserDao;
import com.jd.promo.sharding.mybatis.test.entity.SysRole;
import com.jd.promo.sharding.mybatis.test.entity.SysUser;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/22
 * @Version: 1.0.0
 * 数据库操作与事物测试
 */
public class TestMutipleTable {
    private ClassPathXmlApplicationContext context;
    private SysRoleDao sysRoleDao = null;
    private SysUserDao sysUserDao = null;

    @Before
    public void init() {
        this.context = new ClassPathXmlApplicationContext("spring-config.xml");
        this.sysRoleDao = context.getBean(SysRoleDao.class);
        this.sysUserDao = context.getBean(SysUserDao.class);
    }

    @Test
    public void testInsertSysRoleDao() {
        SysRole sysRole = new SysRole();
        sysRole.setRoleId(1L);
        sysRole.setUserId(1L);
        sysRole.setRoleName("测试");

        this.sysRoleDao.insert(sysRole);
        SysRole sysRoleRslt = this.sysRoleDao.selectByPrimaryKey(1L);
        Assert.assertNotNull(sysRoleRslt);
        Assert.assertEquals("测试", sysRoleRslt.getRoleName());
    }

    /**
     * 测试多表关联查询，且不同表属于相同的库
     */
    @Test
    public void testJoin(){
        ///////////////////初始化测试数据//////////////////////
        SysRole sysRoleInit = new SysRole();
        sysRoleInit.setRoleId(0L);
        sysRoleInit.setUserId(0L);
        sysRoleInit.setRoleName("测试");

        this.sysRoleDao.insert(sysRoleInit);

        SysUser sysUserInit = new SysUser();
        sysUserInit.setUserId(0L);
        sysUserInit.setUsername("测试用户");
        sysUserInit.setPassword("121134");
        sysUserInit.setSalt("2321312");

        this.sysUserDao.insert(sysUserInit);
        ///////////////////初始化测试数据//////////////////////

        SysRole sysRole = new SysRole();
        sysRole.setRoleId(0L);
        sysRole.setUserId(0L);
        List<SysRole> list = this.sysRoleDao.queryUserRole(sysRole);
        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("测试",list.get(0).getRoleName());
    }
    /**
     * 测试多表关联查询，且不同表属于不同的库
     */
    @Test
    public void testJoinMutipleDb(){
        SysRole sysRole = new SysRole();
        sysRole.setRoleId(1L);
        sysRole.setUserId(0L);
        try{
            List<SysRole> list = this.sysRoleDao.queryUserRole(sysRole);
        }catch (Exception e){
            Assert.assertTrue(e.getCause().getCause() instanceof ShardRuntimeException);
            Assert.assertTrue(((ShardRuntimeException)e.getCause().getCause()).getCode()== ExceptCodeConstant.MultipleDBinOneSql);
        }
    }
    @Test
    public void testErrorShardFiledKey(){
        SysRole sysRole = new SysRole();
        sysRole.setRoleId(1L);
        try{
            List<SysRole> list = this.sysRoleDao.queryUserRole(sysRole);
        }catch (Exception e){
            Assert.assertTrue(e.getCause().getCause() instanceof ShardRuntimeException);
            Assert.assertTrue(((ShardRuntimeException)e.getCause().getCause()).getCode()== ExceptCodeConstant.RouteFieldRequery);
        }
    }

    /**
     * 验证多表关联缓存效果
     */
    @Test
    public void testJoinMutipleDbCacheEffective(){
        SysRole sysRole = new SysRole();
        sysRole.setRoleId(0L);
        sysRole.setUserId(0L);
        long time1 = System.nanoTime();
        List<SysRole> list = this.sysRoleDao.queryUserRole(sysRole);//第一次查询无sql解析缓存
        long time2 = System.nanoTime();
        List<SysRole> list1 = this.sysRoleDao.queryUserRole(sysRole);//后续查询有sql解析缓存
        long time3 = System.nanoTime();
        System.out.println(time2-time1);
        System.out.println(time3-time2);
        Assert.assertTrue(time2-time1>time3-time2);
    }


}
