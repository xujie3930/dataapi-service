package com.jinninghui.datasphere.icreditstudio.framework.sequence.snowflake;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jinninghui.datasphere.icreditstudio.framework.utils.NetworkUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.net.NetworkInterface;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: MacNodeGenerate <br/>
 * Description: 分布式节点唯一编号按照机器man地址获取
 * 每个应用单独部署一台机器时使用，可保证获取到的workid不重复
 * Date: 2019/03/08 11:07
 *
 * @author liyanhui
 */
public class MacNodeGenerate implements WorkNodeGenerate {

    /** redis work id hash key*/
    private static final String SNOW_FLAKE_WORK_ID_KEY = "SNOW_FLAKE_WORK::ID::KEY";

    /** redis lock time out 5 seconds*/
    private static final int LOCK_TIMEOUT = 5;

    /**
     * 机器id所占的位数
     */
    private final int workerIdBits = 5;

    /** 最大机器节点数 31 理论支持31*31台机器*/
    private final Integer MAX_NODE_ID = ~(-1 << workerIdBits);

    /** 最小节点编号 */
    private final Integer MIN_NODE_ID = 0;

    /** redis lock hash key*/
    private static String SNOW_FLAKE_KEY_LOCK;

    static {
        SNOW_FLAKE_KEY_LOCK = SNOW_FLAKE_WORK_ID_KEY + "::LOCK";
    }

    @Override
    public ClusterNode generate(RedisTemplate<Object, Object> redisTemplate) {
        return getClusterNode(getServer(), redisTemplate);
    }

    @Override
    public boolean release(RedisTemplate<Object, Object> redisTemplate, ClusterNode node) {
        return true;
    }

    private ClusterNode getClusterNode(String server, RedisTemplate<Object, Object> redisTemplate) {
        if (!StringUtils.hasText(server)) {
            throw new IllegalArgumentException("server can not be null.");
        } else if (redisTemplate.opsForHash().hasKey(SNOW_FLAKE_WORK_ID_KEY, server)) {
            Object value = redisTemplate.opsForHash().get(SNOW_FLAKE_WORK_ID_KEY, server);
            return JSONObject.parseObject(value.toString(), ClusterNode.class);
        } else {
            return getNewClusterNode(server, redisTemplate);
        }
    }

    private ClusterNode getMaxClusterNode(Map<Object, Object> entries) {
        ClusterNode maxNode = null;
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            ClusterNode its = JSONObject.parseObject(entry.getValue().toString(), ClusterNode.class);
            if (null == maxNode) {
                maxNode = its;
            } else if (maxNode.getCenterId() < its.getCenterId()) {
                maxNode = its;
            } else if (maxNode.getCenterId() == its.getCenterId() && maxNode.getWorkId() < its.getWorkId()) {
                maxNode = its;
            }
        }

        return maxNode;
    }

    private ClusterNode getNextClusterNode(Map<Object, Object> entries) {
        ClusterNode maxNode = getMaxClusterNode(entries);
        int centerId = maxNode.getCenterId();
        int workId = maxNode.getWorkId() + 1;
        if (workId > MAX_NODE_ID) {
            ++centerId;
            if (centerId > MAX_NODE_ID) {
                throw new IllegalStateException("CenterId max.");
            }

            workId = MIN_NODE_ID;
        }

        return new ClusterNode(centerId, workId);
    }

    private ClusterNode getNewClusterNode(String server, RedisTemplate<Object, Object> redisTemplate) {
        if (!lock(redisTemplate, server, LOCK_TIMEOUT)) {
            throw new IllegalStateException("lock timeout from redis server.");
        } else {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(SNOW_FLAKE_WORK_ID_KEY);
            ClusterNode nextNode;
            if (entries != null && !entries.isEmpty()) {
                nextNode = getNextClusterNode(entries);
            } else {
                nextNode = new ClusterNode(MIN_NODE_ID, MIN_NODE_ID);
            }

            redisTemplate.opsForHash().put(SNOW_FLAKE_WORK_ID_KEY, server, JSON.toJSONString(nextNode));
            return nextNode;
        }
    }

    private boolean lock(RedisTemplate<Object, Object> redisTemplate, String server, Integer maxTimeout) {
        boolean ret;
        while (true) {
            ret = redisTemplate.opsForHash().putIfAbsent(SNOW_FLAKE_KEY_LOCK, server, Long.toString(System.currentTimeMillis()));
            if (ret) {
                redisTemplate.expire(SNOW_FLAKE_KEY_LOCK, LOCK_TIMEOUT, TimeUnit.SECONDS);
                return true;
            }

            try {
                TimeUnit.SECONDS.sleep(LOCK_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Integer waitTime = maxTimeout;
            maxTimeout = maxTimeout - 1;
            if (waitTime > 0) {
                continue;
            }

            return false;
        }
    }

    private String getServer() {
        Set<NetworkInterface> networkInterfaceSet = NetworkUtils.getNICs(NetworkUtils.Filter.PHYSICAL_ONLY, NetworkUtils.Filter.UP);
        Iterator<NetworkInterface> it = networkInterfaceSet.iterator();
        String mac = "";
        while (it.hasNext()) {
            mac = NetworkUtils.getMacAddress(it.next(), "-");
            if (StringUtils.hasText(mac)) {
                break;
            }
        }
        return mac.toUpperCase();
    }
}
