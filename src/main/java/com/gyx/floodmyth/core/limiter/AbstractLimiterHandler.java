package com.gyx.floodmyth.core.limiter;

import com.gyx.floodmyth.entity.LimiterConfigWrapper;
import com.gyx.floodmyth.entity.LimiterRuleWrapper;
import com.gyx.floodmyth.core.limiter.access.AccessStrategy;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 限流处理器  抽象实现
 *
 * @author gyx
 * @date 2021/8/12 10:56
 */
public abstract class AbstractLimiterHandler implements LimiterHandler {
    /**
     * 令牌桶
     * 初始容量为0
     */
    protected final AtomicLong bucket = new AtomicLong(0);
    /**
     * 限流规则
     */
    protected LimiterRuleWrapper rule;
    /**
     * 限流器集群配置
     */
    protected LimiterConfigWrapper config;
    /**
     * 令牌装填器
     *
     * 用于给令牌桶补充令牌
     */
    protected ScheduledFuture<?> scheduledFuture;

    public AbstractLimiterHandler(LimiterRuleWrapper rule, LimiterConfigWrapper config) {
        this.config = config;
        init(rule);
    }

    /**
     * 尝试访问
     *
     * @param tokenNum 消耗的令牌数量
     */
    @Override
    public boolean tryAccess(Integer tokenNum) {
        if (rule.isEnable()) {
            //限流功能已关闭
            return true;
        }
        if (rule.getLimit() == 0) {
            return false;
        }
        return AccessStrategy.strategy.get(rule.getAccessModel()).tryAccess(bucket, rule,tokenNum);
    }

    /**
     * 初始化
     *
     * @param rule 限流规则的包装器
     */
    @Override
    public void init(LimiterRuleWrapper rule) {
        this.rule = rule;
        if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(true);
        }
    }

    /**
     * 获取限流规则标识
     */
    @Override
    public String getId() {
        return rule.getId();
    }

    /**
     * 获取限流规则
     */
    @Override
    public LimiterRuleWrapper getRule() {
        return rule;
    }
}
