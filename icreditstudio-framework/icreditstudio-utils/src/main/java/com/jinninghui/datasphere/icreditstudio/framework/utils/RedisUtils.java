package com.jinninghui.datasphere.icreditstudio.framework.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author xujie
 * @description 基于RedisTemplate的redis工具类
 * @create 2021-10-14 16:44
 **/
@Component
public class RedisUtils {
    private RedisTemplate redisTemplate;

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        //自定义string类型序列化器，防止乱码
        this.redisTemplate = redisTemplate;
        //System.out.println(hashValueSerializer.hashCode());
        //this.redisTemplate.setKeySerializer(new StringRedisSerializer());
        //this.redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //重载JdkSerializationRedisSerializer，解决在使用hincrby时ERR hash value is not an integer的问题
        this.redisTemplate.setHashValueSerializer(new MyJdkSerializationRedisSerializer());
    }

    /*public static void main(String[] ss){
        byte[] serialize = new JdkSerializationRedisSerializer().serialize("system.prop.app.used.count");
        try {
            String string = new java.lang.String(serialize, "utf-8");
            System.out.println(string);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }*/

    class MyJdkSerializationRedisSerializer extends JdkSerializationRedisSerializer {
        @Override
        public byte[] serialize(Object object) {
            if (object == null){
                return new byte[0];
            }
            //由于redisTemplate原生的序列化工具存在的问题，导致在对hash执行hincrby操作时报错（ERR hash value is not an integer）
            //原型是hash的value序列化后转化为非数字字符串，而increase方法没有序列化，直接相加。导致报错
            //此处做判断，单StatisticsServiceImpl类的appTopView方法调用hset时，不执行默认序列化器，其他方法继续调用默认序列化器
            //避免改动对已有其他方法的影响
            if (object instanceof Long || object instanceof Double || object instanceof Integer) {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                for(int i=2;i<stackTrace.length;i++){
                    StackTraceElement st = stackTrace[i];
                    String className = st.getClassName();
                    String methodName = st.getMethodName();
                    if(("appTopView".equals(methodName) && className.endsWith("StatisticsServiceImpl")) || ("addAppUsedCount".equals(methodName) && className.endsWith("KafkaConsumer"))){
                        return object.toString().getBytes(Charset.forName("UTF-8"));
                    }
                }
            }
            return super.serialize(object);
            /*if(object instanceof java.lang.String){
                return ((String)object).getBytes(Charset.forName("UTF-8"));
            }
            try {
                return JSON.toJSONBytes(object, SerializerFeature.WriteClassName);
            } catch (Exception exception) {
                throw new SerializationException("Could not serialize : " + exception.getMessage(), exception);
            }*/
        }
        @Override
        public Object deserialize(@Nullable byte[] bytes) {
            if (bytes == null || bytes.length == 0) {
                return null;
            }
            try{
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                for(int i=2;i<stackTrace.length||i<20;i++){
                    StackTraceElement st = stackTrace[i];
                    String className = st.getClassName();
                    String methodName = st.getMethodName();
                    if(("appTopView".equals(methodName) && className.endsWith("StatisticsServiceImpl")) || ("addAppUsedCount".equals(methodName) && className.endsWith("KafkaConsumer"))){
                        return new String(bytes, "UTF-8");
                    }
                }

            }catch (Exception ex){}

            return super.deserialize(bytes);
        }
    }

    /*public static void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisUtils.redisTemplate = redisTemplate;
    }*/

    /**
     * 实现命令：TTL key，以秒为单位，返回给定 key的剩余生存时间(TTL, time to live)。
     *
     * @param key
     * @return
     */
    public long ttl(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 实现命令：expire 设置过期时间，单位秒
     *
     * @param key
     * @return
     */
    public void expire(String key, long timeout) {
        redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 实现命令：INCR key，增加key一次
     *
     * @param key
     * @return
     */
    public long incr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 实现命令：KEYS pattern，查找所有符合给定模式 pattern的 key
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 实现命令：DEL key，删除一个key
     *
     * @param key
     */
    public void del(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 实现命令：SET key value EX seconds，设置key-value和超时时间（秒）
     *
     * @param key
     * @param value
     * @param timeout（以秒为单位）
     */
    public void set(String key, Object value, Long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 实现命令：GET key，返回 key所关联的字符串值。
     *
     * @param key
     * @return value
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 批量查询，管道pipeline
     * @param keys
     * @return
     */
    public List<Object> batchGet(List<String> keys) {

//		nginx -> keepalive
//		redis -> pipeline

        List<Object> result = redisTemplate.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection src = (StringRedisConnection)connection;

                for (String k : keys) {
                    src.get(k);
                }
                return null;
            }
        });

        return result;
    }


    // Hash（哈希表）

    /**
     * 实现命令：HSET key field value，将哈希表 key中的域 field的值设为 value
     *
     * @param key
     * @param field
     * @param value
     */
    public void hset(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }
    public void hsetnx(String key, String field, Object value) {
        redisTemplate.opsForHash().putIfAbsent(key, field, value);
    }

    public Long hincrby(String key, String field, Integer num){
        return redisTemplate.opsForHash().increment(key, field, num);
    }

    public Long hincrby(String key, String field, Long num){
        return redisTemplate.opsForHash().increment(key, field, num);
    }

    /**
     * 实现命令：HGET key field，返回哈希表 key中给定域 field的值
     *
     * @param key
     * @param field
     * @return
     */
    public Object hget(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 实现命令：HDEL key field [field ...]，删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
     *
     * @param key
     * @param fields
     */
    public void hdel(String key, Object... fields) {
        redisTemplate.opsForHash().delete(key, fields);
    }

    public List<String> hmget(String key, List<String> fields){
        return redisTemplate.opsForHash().multiGet(key, fields);
    }

    public Set<String> hkeys(String key){
        return redisTemplate.opsForHash().keys(key);
    }

    public Long hlen(String key){
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 实现命令：HGETALL key，返回哈希表 key中，所有的域和值。
     *
     * @param key
     * @return
     */
    public Map<String, Object> hgetall(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    // List（列表）

    /**
     * 实现命令：LPUSH key value，将一个值 value插入到列表 key的表头
     *
     * @param key
     * @param value
     * @return 执行 LPUSH命令后，列表的长度。
     */
    public long lpush(String key, String value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }
    public long lpush(String key, String... value) {
        return redisTemplate.opsForList().leftPushAll(key, value);
    }

    public List<Object> lrange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    public Long lrange(String key) {
        return redisTemplate.opsForList().size(key);
    }

    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 实现命令：LPOP key，移除并返回列表 key的头元素。
     *
     * @param key
     * @return 列表key的头元素。
     */
    public String lpop(String key) {
        return (String)redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 实现命令：RPUSH key value，将一个值 value插入到列表 key的表尾(最右边)。
     *
     * @param key
     * @param value
     * @return 执行 LPUSH命令后，列表的长度。
     */
    public long rpush(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * Redis Zscore 命令返回有序集中，成员的分数值。 如果成员元素不是有序集 key 的成员，或 key 不存在，返回 nil 。
     *
     * @param key
     * @param value
     * @return 执行 LPUSH命令后，列表的长度。
     */
    public Double zscore(String key, String value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * Redis Zincrby 命令对有序集合中指定成员的分数加上增量 increment
     *
     * 可以通过传递一个负数值 increment ，让分数减去相应的值，比如 ZINCRBY key -5 member ，就是让 member 的 score 值减去 5 。
     *
     * 当 key 不存在，或分数不是 key 的成员时， ZINCRBY key increment member 等同于 ZADD key increment member 。
     *
     * 当 key 不是有序集类型时，返回一个错误。
     *
     * 分数值可以是整数值或双精度浮点数。
     * @author  maoc
     * @create  2022/6/23 10:17
     * @desc
     **/
    public Double zincrby(String key, String value, double score) {
        return redisTemplate.opsForZSet().incrementScore(key, value, score);
    }

    /**
     * Redis Zrevrange 命令返回有序集中，指定区间内的成员。
     *
     * 其中成员的位置按分数值递减(从大到小)来排列。
     *
     * 具有相同分数值的成员按字典序的逆序(reverse lexicographical order)排列。
     *
     * 除了成员按分数值递减的次序排列这一点外， ZREVRANGE 命令的其他方面和 ZRANGE 命令一样。
     * @author  maoc
     * @create  2022/6/23 10:21
     * @desc
     **/
    public Set<Object> zrevrange(String key, int start, int end, boolean withscores) {
        ZSetOperations opt = redisTemplate.opsForZSet();
        return withscores?opt.reverseRangeWithScores(key, start, end):opt.reverseRange(key, start, end);
    }

    /**
     * Redis Zadd 命令用于将一个或多个成员元素及其分数值加入到有序集当中。
     *
     * 如果某个成员已经是有序集的成员，那么更新这个成员的分数值，并通过重新插入这个成员元素，来保证该成员在正确的位置上。
     *
     * 分数值可以是整数值或双精度浮点数。
     *
     * 如果有序集合 key 不存在，则创建一个空的有序集并执行 ZADD 操作。
     *
     * 当 key 存在但不是有序集类型时，返回一个错误。
     *
     * 注意： 在 Redis 2.4 版本以前， ZADD 每次只能添加一个元素。
     * @author  maoc
     * @create  2022/6/23 14:01
     * @desc
     **/
    public boolean zadd(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }
    public long zadd(String key, Set<ZSetOperations.TypedTuple<Object>> values) {
        return redisTemplate.opsForZSet().add(key, values);
    }
}
