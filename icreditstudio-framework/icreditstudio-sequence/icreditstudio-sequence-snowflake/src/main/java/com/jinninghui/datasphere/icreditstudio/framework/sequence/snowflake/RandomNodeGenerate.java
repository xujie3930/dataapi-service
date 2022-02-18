package com.jinninghui.datasphere.icreditstudio.framework.sequence.snowflake;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: RandomNodeGenerate <br/>
 * Description：使用一个set维护datacenter， 使用set维护每一个datacenter的work list
 * <p>
 * 每个机器多个应用时使用，可用于容器，不能根据id反查workid，因为workid会变
 * Date: 2019/03/08 11:07
 *
 * @author liyanhui
 */
public class RandomNodeGenerate implements WorkNodeGenerate {

    /**
     * 机器id所占的位数
     */
    private final int workerIdBits = 5;

    /**
     * 最大机器节点数 31 理论支持31*31台机器
     */
    private final Integer MAX_NODE_ID = ~(-1 << workerIdBits);

    /**
     * 最小节点编号
     */
    private final Integer MIN_NODE_ID = 0;

    private static String DATA_CENTER_KEY = "DATA_CENTER_KEY";

    private static String WORK_ID_KEY_PREFIXX = "WORK_ID_KEY::";

    private static String SNOW_FLAKE_KEY_LOCK;

    private static String SNOW_FLAKE_KEY_LOCK_VALUE;

    /** redis lock time out 5 seconds*/
    private static final int LOCK_TIMEOUT = 5;

    private Integer dataCenterId;

    static {
        SNOW_FLAKE_KEY_LOCK = DATA_CENTER_KEY + "::LOCK";
        SNOW_FLAKE_KEY_LOCK_VALUE = SNOW_FLAKE_KEY_LOCK + "::VALUE";
    }

    /**
     * 1.先找到可以使用的datacenter（满足work list大小不大于最大节点数即可）
     * 2.取到已经找到的datacenter的 work list ，并占位
     * 3.datacenter id ， workid
     *
     * @param redisTemplate
     * @return
     */
    @Override
    public ClusterNode generate(RedisTemplate<Object, Object> redisTemplate) {
        if (!lock(redisTemplate, LOCK_TIMEOUT)) {
            throw new IllegalStateException("lock timeout from redis server.");
        }else {
            this.dataCenterId = getDatacenterId(redisTemplate);
            int workId = getWorkId(redisTemplate, dataCenterId);
            return new ClusterNode(dataCenterId, workId);
        }
    }

    @Override
    public boolean release(RedisTemplate<Object, Object> redisTemplate, ClusterNode node) {
        int datacenterId = node.getCenterId();
        int workId = node.getWorkId();
        return redisTemplate.opsForSet().remove(WORK_ID_KEY_PREFIXX + datacenterId, workId) == workId;
    }

    private int getDatacenterId(RedisTemplate<Object, Object> redisTemplate) {
        Set<Object> set = redisTemplate.opsForSet().members(DATA_CENTER_KEY);
        if (set == null || set.isEmpty()) {
            redisTemplate.opsForSet().add(DATA_CENTER_KEY, MIN_NODE_ID);
            return MIN_NODE_ID;
        }

        for (int i = 0; i <= MAX_NODE_ID; i++) {
            if (!set.contains(i)) {
                redisTemplate.opsForSet().add(DATA_CENTER_KEY, i);
                return i;
            } else {
                Set<Object> workSet = redisTemplate.opsForSet().members(WORK_ID_KEY_PREFIXX + i);
                if (workSet == null || workSet.isEmpty() || workSet.size() <= MAX_NODE_ID) {
                    return i;
                }
            }
        }

        throw new IllegalStateException("have no left datacenter.");
    }

    private int getWorkId(RedisTemplate<Object, Object> redisTemplate, int datacenterId) {
        Set<Object> workSet = redisTemplate.opsForSet().members(WORK_ID_KEY_PREFIXX + datacenterId);

        if (workSet == null || workSet.isEmpty()) {
            redisTemplate.opsForSet().add(WORK_ID_KEY_PREFIXX + datacenterId, MIN_NODE_ID);
            return MIN_NODE_ID;
        }

        for (int i = 0; i <= MAX_NODE_ID; i++) {
            if (!workSet.contains(i)) {
                redisTemplate.opsForSet().add(WORK_ID_KEY_PREFIXX + datacenterId, i);
                return i;
            }
        }
        this.dataCenterId = getDatacenterId(redisTemplate);
        return getWorkId(redisTemplate, dataCenterId);
    }

    private boolean lock(RedisTemplate<Object, Object> redisTemplate , Integer maxTimeout) {
        boolean ret;
        while (true) {
            ret = redisTemplate.opsForHash().putIfAbsent(SNOW_FLAKE_KEY_LOCK, SNOW_FLAKE_KEY_LOCK_VALUE, Long.toString(System.currentTimeMillis()));
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
}
