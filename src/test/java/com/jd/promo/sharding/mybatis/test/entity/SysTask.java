package com.jd.promo.sharding.mybatis.test.entity;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/09/07
 * @Version: 1.0.0
 * 任务实体
 */
public class SysTask {
    private Long taskId;
    private Long userId;
    private String taskName;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
