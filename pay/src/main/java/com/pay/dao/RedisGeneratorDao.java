
package com.pay.dao;


import java.io.Serializable;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
 
public abstract class RedisGeneratorDao<K extends Serializable, V extends Serializable>  {
   
  @Resource
  public RedisTemplate<K,V> redisTemplate;
 
  /**
   * 设置redisTemplate
   * @param redisTemplate the redisTemplate to set
   */ 
  public void setRedisTemplate(RedisTemplate<K, V> redisTemplate) { 
	RedisSerializer stringSerializer = new StringRedisSerializer();
    redisTemplate.setKeySerializer(stringSerializer);
    redisTemplate.setHashKeySerializer(stringSerializer);
    this.redisTemplate = redisTemplate;
  } 
     
  /**
   * 获取 RedisSerializer
   * <br>------------------------------<br>
   */ 
  protected RedisSerializer<String> getRedisSerializer() { 
    return redisTemplate.getStringSerializer(); 
  }
}

