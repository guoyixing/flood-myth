package com.gyx.floodmyth.core.server;

import com.gyx.floodmyth.common.http.HttpUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 令牌分发服务
 *
 * @author gyx
 * @date 2021/8/12 14:31
 */
public class AllotServer {
    /**
     * 令牌分发服务器地址
     *
     * 读多写少
     */
    private List<String> serverList = new CopyOnWriteArrayList<>();
    /**
     * 地址的备份
     */
    private List<String> backupsList = new CopyOnWriteArrayList<>();
    private ReentrantLock lock = new ReentrantLock();
    private int pos = 0;


    /**
     * 设置令牌分发服务器
     * @param ip ip地址
     */
    public void setServer(Map<String, Integer> ip) {
        // 清空List
        serverList.clear();
        // 重建一个Map，避免服务器的上下线导致的并发问题
        Map<String, Integer> serverMap = new HashMap<>(ip);
        // 取得Ip地址List
        for (String server : serverMap.keySet()) {
            int weight = serverMap.get(server);
            //添加权重
            for (int i = 0; i < weight; i++) {
                serverList.add(server);
            }
        }
    }

    private String getServer() {
        String server;
        lock.lock();
        try {
            if (serverList.size()==0){
                serverList.addAll(backupsList);
                backupsList.clear();
            }
            if (pos >= serverList.size()) {
                pos = 0;
            }
            server = serverList.get(pos);
            pos++;
        } finally {
            lock.unlock();
        }
        return server;
    }

    public String connect(String path, String data) {
        String server = getServer();
        try {
            return HttpUtil.connect("http://" + server + "/" + path)
                    .setData("data", data)
                    .setMethod("POST")
                    .execute()
                    .getBody();
        } catch (IOException e) {
            serverList.remove(server);
            backupsList.add(server);
        }
        return null;
    }
}
