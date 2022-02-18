package com.jinninghui.datasphere.icreditstudio.framework.sequence.configuration;

import com.jinninghui.datasphere.icreditstudio.framework.sequence.api.SequenceService;
import com.jinninghui.datasphere.icreditstudio.framework.sequence.snowflake.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * ClassName: SequenceAutoConfiguration <br/>
 * Description: sequence自动装配
 * Date: 2019/03/08 11:07
 *
 * @author liyanhui
 */
@EnableConfigurationProperties({SequenceProperties.class})
public class SequenceAutoConfiguration {

    /**
     * simple获取节点
     */
    private static final String GENERATE_TYPE_SIMPLE = "simple";

    /**
     * 随机获取节点
     */
    private static final String GENERATE_TYPE_RANDOM = "random";

    /**
     * mac地址获取节点
     */
    private static final String GENERATE_TYPE_MAC = "mac";

    @Autowired
    private SequenceProperties properties;

    @Autowired(required = false)
    private RedisTemplate<Object, Object> redisTemplate;

    @Bean
    @ConditionalOnProperty(
            prefix = "sequence",
            name = "type",
            havingValue = "snowflake")
    public SequenceService snowFlakeService(WorkNodeGenerate workNodeGenerate) {
        if(redisTemplate == null){
            throw  new IllegalStateException("Snowflake sequence service need RedisTemplate bean.");
        }
        SnowflakeSequenceService snowflakeSequenceService = new SnowflakeSequenceService();
        snowflakeSequenceService.setRedisTemplate(redisTemplate);
        snowflakeSequenceService.setWorkNodeGenerate(workNodeGenerate);
        return snowflakeSequenceService;
    }


    @Bean
    @ConditionalOnProperty(
            prefix = "sequence",
            name = "type",
            havingValue = "snowflake")
    public WorkNodeGenerate workNodeGenerate() {
        String generate = properties.getGenerate();
        if (GENERATE_TYPE_RANDOM.equals(generate)) {
            return new RandomNodeGenerate();
        } else if (GENERATE_TYPE_SIMPLE.equals(generate)) {
            return new SimpleNodeGenerate();
        } else if (GENERATE_TYPE_MAC.equals(generate)) {
            return new MacNodeGenerate();
        }
        return new RandomNodeGenerate();
    }


}
