package com.gyx.floodmyth.core.limiter;

import com.gyx.floodmyth.entity.LimiterRuleWrapper;

/**
 * 限流处理器接口
 *
 * @author gyx
 * @date 2021/8/12 10:35
 */
public interface LimiterHandler {

    /**
     * 初始化
     *
     * @param rule 限流规则的包装器
     */
    void init(LimiterRuleWrapper rule);

    /**
     * 尝试访问
     *
     * @param tokenNum 消耗的令牌数量
     */
    boolean tryAccess(Integer tokenNum);

    /**
     * 获取限流规则标识
     */
    String getId();

    /**
     * 获取限流规则
     */
    LimiterRuleWrapper getRule();
}
