package com.gyx.floodmyth.core.limiter;

import com.gyx.floodmyth.entity.LimiterConfigWrapper;
import com.gyx.floodmyth.entity.LimiterRuleWrapper;

/**
 * 单体限流处理器
 *
 * @author gyx
 * @date 2021/8/12 13:17
 */
public class LocalLimiterHandler extends AbstractLimiterHandler {

    public LocalLimiterHandler(LimiterRuleWrapper rule, LimiterConfigWrapper config) {
        super(rule, config);
    }

    /**
     * 初始化
     *
     * @param rule 限流规则的包装器
     */
    @Override
    public void init(LimiterRuleWrapper rule) {
        super.init(rule);
        if (rule.getLimit() == 0) {
            return;
        }
        this.scheduledFuture = config.getScheduledThreadExecutor()
                .scheduleAtFixedRate(() -> {
                    if (bucket.get() + rule.getLimit() < rule.getMaxLimit()) {
                        bucket.set(rule.getLimit());
                    }
                }, rule.getInitialDelay(), rule.getPeriod(), rule.getUnit());
    }
}
