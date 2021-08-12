package com.gyx.floodmyth.core.limiter.access;

import com.gyx.floodmyth.entity.LimiterRuleWrapper;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 阻塞的访问策略
 *
 * @author gyx
 * @date 2021/8/12 11:25
 */
public class BlockingAccess implements AccessStrategy {
    /**
     * 尝试访问
     *
     * @param bucket 令牌桶
     * @param rule   限流器规则
     */
    @Override
    public boolean tryAccess(AtomicLong bucket, LimiterRuleWrapper rule) {
        //CAS获取令牌,阻塞直到成功
        long l = bucket.longValue();
        while (!(l > 0 && bucket.compareAndSet(l, l - 1))) {
            sleep(rule);
            l = bucket.longValue();
        }
        return true;
    }

    /**
     * 线程休眠
     */
    private void sleep(LimiterRuleWrapper rule) {
        //大于1ms强制休眠
        if (rule.getUnit().toMillis(rule.getPeriod()) < 1) {
            return;
        }
        try {
            Thread.sleep(rule.getUnit().toMillis(rule.getPeriod()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
