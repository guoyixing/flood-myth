package com.gyx.floodmyth.entity;

import com.gyx.floodmyth.common.enums.AccessModel;
import com.gyx.floodmyth.common.enums.LimiterModel;
import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * 限流规则的包装器
 *
 * @author gyx
 * @date 2021/8/12 10:35
 */
@Data
public class LimiterRuleWrapper implements Comparable<LimiterRuleWrapper> {

    /**
     * app name
     */
    private String app = "Application";
    /**
     * 限流规则名称
     */
    private String id = "id";
    /**
     * 相同的限流规则，不同的实例标识(不需要手动配置)
     */
    private String name;

    /**
     * 是否关闭限流功能
     */
    private boolean enable;

    //QPS
    /**
     * 单位时间存放的令牌数
     */
    private long limit;
    /**
     * 令牌上限
     */
    private long maxLimit;
    /**
     * 单位时间大小
     */
    private long period = 1;
    /**
     * 第一次放入令牌的延迟时间
     */
    private long initialDelay = 0;
    /**
     * 时间单位
     */
    private TimeUnit unit = TimeUnit.SECONDS;

    //get bucket
    /**
     * 每次从令牌中心取多少个令牌
     */
    private long batch = 1;
    /**
     * 获取的触发因子
     * 现有令牌数/批次令牌数<=? [0,1]
     */
    private double remaining = 0.5;

    //Select
    /**
     * 控制行为：快速失败/阻塞
     */
    private AccessModel accessModel = AccessModel.FAIL_FAST;
    /**
     * 部署方式（本地/分布式）
     */
    private LimiterModel limiterModel = LimiterModel.LOCAL;

    //System
    /**
     * APP-ID实例数(不需要手动配置)
     */
    private int number;
    /**
     * 版本号(不需要手动配置)
     */
    private long version;

    @Override
    public int compareTo(LimiterRuleWrapper o) {
        if (this.version < o.getVersion()) {
            return -1;
        } else if (this.version == o.getVersion()) {
            return 0;
        }
        return 1;
    }
}
