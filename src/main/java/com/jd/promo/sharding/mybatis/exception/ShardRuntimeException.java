package com.jd.promo.sharding.mybatis.exception;

/**
 * @Author: zhouchangjiang
 * @Date: 2016/08/20
 * @Version: 1.0.0
 * SQL解析异常
 */
public class ShardRuntimeException extends RuntimeException {

    /**
     * 异常编号
     */
    private String code = null;

    public ShardRuntimeException(String code) {
        super();
        this.code=code;
    }

    public ShardRuntimeException(String msg, String code) {
        super(msg);
        this.code=code;
    }

    public ShardRuntimeException(String msg, Throwable t, String code) {
        super(msg, t);
        this.code=code;
    }

    public ShardRuntimeException(Throwable t, String code) {
        super(t);
        this.code=code;
    }

    public String getCode() {
        return code;
    }
}
