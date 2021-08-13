package com.gyx.floodmyth.core.server;

import com.alibaba.fastjson.JSON;
import com.gyx.floodmyth.common.enums.LimiterModel;
import com.gyx.floodmyth.core.limiter.LimiterHandler;
import com.gyx.floodmyth.entity.LimiterConfigWrapper;
import com.gyx.floodmyth.entity.LimiterRuleWrapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 限流处理器注册服务、更新服务
 *
 * @author 郭一行
 * @date 2021/8/13 9:40
 */
public class RegisterServer {

    /**
     * 限流处理器的容器
     * <p>
     * Map<LimiterHandler.id,LimiterHandler>
     */
    private static Map<String, LimiterHandler> limiterContainer = new ConcurrentHashMap<>();

    /**
     * 本地注册
     *
     * @param limiter 限流处理器
     */
    public static void registered(LimiterHandler limiter) {
        if (limiterContainer.containsKey(limiter.getId())) {
            throw new RuntimeException("不可以重复注册限流处理器，限流器id:" + limiter.getId());
        }
        limiterContainer.put(limiter.getId(), limiter);
    }

    /**
     * 分布式注册
     *
     * @param limiter 限流处理器
     * @param config  限流器配置包装类
     */
    public static void registered(LimiterHandler limiter, LimiterConfigWrapper config) {
        //注册在本地
        registered(limiter);
        //从令牌中心拉取规则，更新本地限流规则
        rulePull(limiter, config);
    }

    /**
     * 从令牌中心拉取规则，更新本地限流规则
     *
     * @param limiter 限流处理器
     * @param config  限流器配置包装类
     */
    private static void rulePull(LimiterHandler limiter, LimiterConfigWrapper config) {
        config.getScheduledThreadExecutor().scheduleWithFixedDelay(() -> {
            //连接远程获取配置
            String rules = config.getAllotServer().connect(LimiterConfigWrapper.http_heart, JSON.toJSONString(limiter.getRule()));
            if (rules == null) {
                //连接失败，转成本地模式运行
                LimiterRuleWrapper rule = limiter.getRule();
                rule.setLimiterModel(LimiterModel.LOCAL);
                limiter.init(rule);
                return;
            }
            LimiterRuleWrapper newestRule = JSON.parseObject(rules, LimiterRuleWrapper.class);
            if (newestRule.getVersion() > limiter.getRule().getVersion()) {
                //版本升级
                if (newestRule.getLimiterModel().equals(LimiterModel.LOCAL)) {
                    //禁止改成本地模式
                    newestRule.setLimiterModel(LimiterModel.CLOUD);
                }
                //更新规则
                limiterContainer.get(limiter.getId()).init(newestRule);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

}
