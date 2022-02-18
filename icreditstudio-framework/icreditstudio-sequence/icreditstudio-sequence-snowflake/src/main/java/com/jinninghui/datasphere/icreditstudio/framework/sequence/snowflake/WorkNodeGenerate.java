package com.jinninghui.datasphere.icreditstudio.framework.sequence.snowflake;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * ClassName: WorkNodeGenerate <br/>
 * Description： 分布式环境获取节点
 * Date: 2019/03/08 11:07
 *
 * @author liyanhui
 */
public interface WorkNodeGenerate {

    /**
     * 生成节点
     *
     * @param redisTemplate
     * @return
     */
    ClusterNode generate(RedisTemplate<Object, Object> redisTemplate);

    /**
     * 释放节点
     * <p>
     * 根据datacenter id 取到 worklist， 并从list removce workid
     *
     * @param node
     * @return
     */
    boolean release(RedisTemplate<Object, Object> redisTemplate, ClusterNode node);
}
