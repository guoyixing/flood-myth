package com.gyx.floodmyth.core.limiter.access;

import com.gyx.floodmyth.common.enums.AccessModel;
import com.gyx.floodmyth.entity.LimiterRuleWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 访问策略接口
 *
 * @author gyx
 * @date 2021/8/12 11:18
 */
public interface AccessStrategy {

    /**
     * 用于访问策略
     */
    Map<AccessModel, AccessStrategy> strategy = new HashMap<AccessModel, AccessStrategy>(2) {{
        put(AccessModel.FAIL_FAST, new FailFastAccess());
        put(AccessModel.BLOCKING, new BlockingAccess());
    }};


    /**
     * 尝试访问
     *
     * @param bucket   令牌桶
     * @param rule     限流器规则
     * @param tokenNum 消耗的令牌数量
     */
    boolean tryAccess(AtomicLong bucket, LimiterRuleWrapper rule, Integer tokenNum);
}
