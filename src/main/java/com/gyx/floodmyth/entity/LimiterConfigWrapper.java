package com.gyx.floodmyth.entity;


import com.gyx.floodmyth.core.server.AllotServer;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author gyx
 * @date 2021/8/12 11:07
 */
public class LimiterConfigWrapper {

    /**
     * 单例
     */
    private static volatile LimiterConfigWrapper limiterConfigWrapper;

    /**
     * 发票服务器
     */
    private AllotServer allotServer;
    /**
     * 调度线程池
     */
    private ScheduledExecutorService scheduledThreadExecutor;

    //Ticket server interface
    public static String http_monitor = "monitor";
    public static String http_heart = "heart";
    public static String http_token = "token";

    private LimiterConfigWrapper() {
        //禁止new实例
    }

    public static LimiterConfigWrapper getInstance() {
        if (limiterConfigWrapper == null) {
            synchronized (LimiterConfigWrapper.class) {
                if (limiterConfigWrapper == null) {
                    limiterConfigWrapper = new LimiterConfigWrapper();
                }
            }
        }
        return limiterConfigWrapper;
    }

    public ScheduledExecutorService getScheduledThreadExecutor() {
        if (this.scheduledThreadExecutor == null) {
            synchronized (this) {
                if (this.scheduledThreadExecutor == null) {
                    setScheduledThreadExecutor(new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2, new ThreadPoolExecutor.DiscardOldestPolicy()));
                }
            }
        }
        return this.scheduledThreadExecutor;
    }

    public void setScheduledThreadExecutor(ScheduledExecutorService scheduledThreadExecutor) {
        this.scheduledThreadExecutor = scheduledThreadExecutor;
    }

    public AllotServer getAllotServer() {
        if (allotServer == null) {
            throw new RuntimeException("无法获取到令牌分发服务");
        }
        return allotServer;
    }

    public void setAllotServer(Map<String, Integer> ip) {
        if (ip.size() < 1) {
            throw new RuntimeException("至少需要一个令牌分发服务地址");
        }
        if (this.allotServer == null) {
            synchronized (this) {
                if (this.allotServer == null) {
                    this.allotServer = new AllotServer();
                }
            }
        }
        this.allotServer.setServer(ip);
    }

}
