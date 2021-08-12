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
     * 尝试访问
     */
    @Override
    public boolean tryAccess() {
        if (rule.isEnable()) {
            //限流功能已关闭
            return true;
        }
        return super.tryAccess();
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
                .scheduleAtFixedRate(() -> bucket.set(rule.getLimit()), rule.getInitialDelay(), rule.getPeriod(), rule.getUnit());
    }
}
