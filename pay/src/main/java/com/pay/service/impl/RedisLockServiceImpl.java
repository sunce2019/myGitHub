package com.pay.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.stereotype.Repository;

import com.pay.dao.MemberDao;
import com.pay.dao.RedisGeneratorDao;
import com.pay.service.RedisLockService;
import com.pay.util.StringUtils;
import com.pay.vo.Member;

/**
*@author star
*@version 创建时间：2019年6月3日下午6:06:13
*/
@Repository(value="redisLockService")
public class RedisLockServiceImpl extends RedisGeneratorDao<String,Long> implements RedisLockService {
	 /**
     * 加锁超时时间，单位毫秒， 即：加锁时间内执行完操作，如果未完成会有并发现象
     */
    private static final long LOCK_TIMEOUT = 5 * 1000;

    private static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger("busi1");
    
    @Autowired
	private MemberDao memberDao;

    /**
     * 取到锁加锁 取不到锁一直等待直到获得锁
     */
    @Override
    public Long lock(final String lockKey) {
        //循环获取锁
        while (true) {
            //锁时间
            final Long lock_timeout = currtTimeForRedis() + LOCK_TIMEOUT + 1;
            if (redisTemplate.execute(new RedisCallback<Boolean>() {
                @Override
                public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                    JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer();
                    byte[] value = jdkSerializer.serialize(lock_timeout);
                    return connection.setNX(lockKey.getBytes(), value);
                }
            })) {
                //设置超时时间，释放内存
                redisTemplate.expire(lockKey, LOCK_TIMEOUT, TimeUnit.MILLISECONDS);
                return lock_timeout;
            } else {
                // redis里的时间
                Long currt_lock_timeout_Str = redisTemplate.opsForValue().get(lockKey);
                //String val = redisTemplate.opsForValue().get(lockKey);
               // if(!StringUtils.isBlank(val))currt_lock_timeout_Str = Long.parseLong(val);
                //锁已经失效
                if (currt_lock_timeout_Str != null && currt_lock_timeout_Str < currtTimeForRedis()) {
                    // 判断是否为空，不为空的情况下，说明已经失效，如果被其他线程设置了值，则第二个条件判断是无法执行
                    Long old_lock_timeout_Str = redisTemplate.opsForValue().getAndSet(lockKey,lock_timeout);
                    //val = redisTemplate.opsForValue().getAndSet(lockKey, Long.toString(lock_timeout));
                    //if(!StringUtils.isBlank(val))old_lock_timeout_Str = Long.parseLong(val);
                    // 获取上一个锁到期时间，并设置现在的锁到期时间
                    if (old_lock_timeout_Str != null && old_lock_timeout_Str.equals(currt_lock_timeout_Str)) {
                        // 如过这个时候，多个线程恰好都到了这里，但是只有一个线程的设置值和当前值相同，他才有权利获取锁
                        //设置超时时间，释放内存
                        redisTemplate.expire(lockKey, LOCK_TIMEOUT, TimeUnit.MILLISECONDS);
                        //返回加锁时间
                        return lock_timeout;
                    }
                }
            }
            try {
                //睡眠100毫秒
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void unlock(String lockKey, long lockvalue) {
        //正常直接删除 如果异常关闭判断加锁会判断过期时间
        // redis里的时间
        Long currt_lock_timeout_Str = redisTemplate.opsForValue().get(lockKey);
        //如果是加锁者 则删除锁 如果不是则等待自动过期 重新竞争加锁
        if (currt_lock_timeout_Str != null && currt_lock_timeout_Str == lockvalue) {
            //删除键
        	redisTemplate.delete(lockKey);
        }
    }
    
    /**
     * 多服务器集群，使用下面的方法，代替System.currentTimeMillis()，获取redis时间，避免多服务的时间不一致问题！！！
     *
     * @return
     */
    @Override
    public long currtTimeForRedis() {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.time();
            }
        });
    }
}
