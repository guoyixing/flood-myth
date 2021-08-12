package com.gyx.floodmyth.core.limiter;

import com.alibaba.fastjson.JSON;
import com.gyx.floodmyth.entity.LimiterConfigWrapper;
import com.gyx.floodmyth.entity.LimiterRuleWrapper;

/**
 * 分布式限流处理器
 *
 * @author gyx
 * @date 2021/8/12 13:58
 */
public class CloudLimiterHandler extends AbstractLimiterHandler {

    public CloudLimiterHandler(LimiterRuleWrapper rule, LimiterConfigWrapper config) {
        super(rule, config);
    }

    /**
     * 尝试访问
     */
    @Override
    public boolean tryAccess() {
        boolean accessFlag = super.tryAccess();
        putCloudBucket();
        return accessFlag;
    }

    /**
     * 初始化
     *
     * @param rule 限流规则的包装器
     */
    @Override
    public void init(LimiterRuleWrapper rule) {
        super.init(rule);
    }

    /**
     * 从集群令牌分发中心，获取令牌，填装到令牌桶中
     */
    private void putCloudBucket() {
        //校验
        if (bucket.get() * rule.getBatch() > rule.getRemaining()) {
            return;
        }
        //异步任务
        config.getScheduledThreadExecutor().execute(() -> {
            //DCL,再次校验
            if (bucket.get() * rule.getBatch() <= rule.getRemaining()) {
                synchronized (bucket) {
                    if (bucket.get() * rule.getBatch() <= rule.getRemaining()) {
                        String result = config.getAllotServer().connect(LimiterConfigWrapper.http_token, JSON.toJSONString(rule));
                        if (result != null) {
                            bucket.getAndAdd(Long.parseLong(result));
                        }
                    }
                }
            }
        });
    }
}
