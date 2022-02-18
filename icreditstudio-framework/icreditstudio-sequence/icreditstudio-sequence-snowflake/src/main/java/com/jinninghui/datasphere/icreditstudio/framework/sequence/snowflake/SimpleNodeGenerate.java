package com.jinninghui.datasphere.icreditstudio.framework.sequence.snowflake;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * ClassName: SimpleNodeGenerate <br/>
 * Description：非分布式环境使用
 * Date: 2019/03/08 11:07
 *
 * @author liyanhui
 */
public class SimpleNodeGenerate implements WorkNodeGenerate {

    @Override
    public ClusterNode generate(RedisTemplate<Object, Object> redisTemplate) {
        return new ClusterNode(0, 0);
    }

    @Override
    public boolean release(RedisTemplate<Object, Object> redisTemplate, ClusterNode node) {
        return true;
    }
}
