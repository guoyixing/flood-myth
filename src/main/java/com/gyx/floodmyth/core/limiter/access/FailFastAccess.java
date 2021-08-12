package com.gyx.floodmyth.core.limiter.access;

import com.gyx.floodmyth.entity.LimiterRuleWrapper;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 快速失败的访问策略
 *
 * @author gyx
 * @date 2021/8/12 11:22
 */
public class FailFastAccess implements AccessStrategy {

    /**
     * 尝试访问
     * @param bucket 令牌桶
     * @param rule   限流器规则
     */
    @Override
    public boolean tryAccess(AtomicLong bucket,LimiterRuleWrapper rule) {
        //CAS获取令牌,没有令牌立即失败
        long l = bucket.longValue();
        while (l > 0) {
            if (bucket.compareAndSet(l, l - 1)) {
                return true;
            }
            l = bucket.longValue();
        }
        return false;
    }
}
