package com.gyx.floodmyth.core.limiter;


import com.gyx.floodmyth.entity.LimiterConfigWrapper;
import com.gyx.floodmyth.entity.LimiterRuleWrapper;

/**
 * 限流处理器工厂类
 * <p>
 * 简单工厂模式
 */
public class LimiterFactory {

    public static LimiterHandler of(LimiterRuleWrapper rule) {
        return of(rule, LimiterConfigWrapper.getInstance());
    }

    public static LimiterHandler of(LimiterRuleWrapper rule, LimiterConfigWrapper config) {
        switch (rule.getLimiterModel()) {
            case LOCAL:
                //本地限流
                LimiterHandler limiter = new LocalLimiterHandler(rule, config);
//                RateLimiterObserver.registered(limiter, config);
                return limiter;
            case CLOUD:
                //集群限流
                limiter = new CloudLimiterHandler(rule, config);
                rule.setName(rule.getName() == null ? String.valueOf(limiter.hashCode()) : rule.getName());
//                RateLimiterObserver.registered(limiter, config);
                return limiter;
            default:
                throw new RuntimeException("CurrentModel Parameter not set");
        }
    }

}
