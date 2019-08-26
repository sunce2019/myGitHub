
package com.pay.dao;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.pay.vo.Member;

import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;


/**
*@author star
*@version 创建时间：2019年2月11日上午10:44:13
*/
@Repository(value="memberDao")
public class MemberDaoImpl extends RedisGeneratorDao<String,Member> implements MemberDao {

	/**
	   * 添加对象
	   */
	  @Override
	  public boolean add(final Member member) { 
	    boolean result = redisTemplate.execute(new RedisCallback<Boolean>() { 
	      public Boolean doInRedis(RedisConnection connection) 
	          throws DataAccessException { 
	        RedisSerializer<String> serializer = getRedisSerializer(); 
	        byte[] key  = serializer.serialize(member.getKey()); 
	        byte[] name = serializer.serialize(member.getValues()); 
	        return connection.setNX(key, name); 
	      } 
	    }); 
	    return result; 
	  } 
	 
	  /**
	   * 添加集合
	   */
	  @Override
	  public boolean add(final List<Member> list) {
	    Assert.notEmpty(list); 
	    boolean result = redisTemplate.execute(new RedisCallback<Boolean>() { 
	      public Boolean doInRedis(RedisConnection connection) 
	          throws DataAccessException { 
	        RedisSerializer<String> serializer = getRedisSerializer(); 
	        for (Member member : list) { 
	          byte[] key  = serializer.serialize(member.getKey()); 
	          byte[] name = serializer.serialize(member.getValues()); 
	          connection.setNX(key, name); 
	        } 
	        return true; 
	      } 
	    }, false, true); 
	    return result;
	  } 
	   
	  /**
	   * 删除对象 ,依赖key
	   */
	  @Override
	  public void delete(String key) { 
	    List<String> list = new ArrayList<String>(); 
	    list.add(key); 
	    delete(list); 
	  } 
	   
	  /**
	   * 删除集合 ,依赖key集合
	   */
	  public void delete(List<String> keys) { 
	    redisTemplate.delete(keys); 
	  } 
	   
	  /**
	   * 修改对象
	   */
	  public boolean update(final Member member) { 
	    String key = member.getKey(); 
	    if (get(key) == null) { 
	      throw new NullPointerException("数据行不存在, key = " + key); 
	    } 
	    boolean result = redisTemplate.execute(new RedisCallback<Boolean>() { 
	      public Boolean doInRedis(RedisConnection connection) 
	          throws DataAccessException { 
	        RedisSerializer<String> serializer = getRedisSerializer(); 
	        byte[] key  = serializer.serialize(member.getKey()); 
	        byte[] name = serializer.serialize(member.getValues()); 
	        connection.set(key, name); 
	        return true; 
	      } 
	    }); 
	    return result; 
	  } 
	   
	  /**
	   * 根据key获取对象
	   */
	  @Override
	  public Member get(final String keyId) { 
	    Member result = redisTemplate.execute(new RedisCallback<Member>() { 
	      public Member doInRedis(RedisConnection connection) 
	          throws DataAccessException { 
	        RedisSerializer<String> serializer = getRedisSerializer(); 
	        byte[] key = serializer.serialize(keyId); 
	        byte[] value = connection.get(key); 
	        if (value == null) { 
	          return null; 
	        } 
	        String nickname = serializer.deserialize(value); 
	        return new Member(keyId, nickname); 
	      } 
	    }); 
	    return result; 
	  }
	  @Override
	  public String gets(final String keyId) { 
		    String result = redisTemplate.execute(new RedisCallback<String>() { 
		      public String doInRedis(RedisConnection connection) 
		          throws DataAccessException { 
		        RedisSerializer<String> serializer = getRedisSerializer(); 
		        byte[] key = serializer.serialize(keyId); 
		        byte[] value = connection.get(key); 
		        return serializer.deserialize(value); 
		      } 
		    }); 
		    return result; 
		}

	public String getOrderNumber() {
	    SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
	    Date date=new Date();
	    String formatDate=sdf.format(date);
	    String key="key"+formatDate;
	    Long incr = getIncr(key, getCurrent2TodayEndMillisTime());
	    if(incr==0) {
	    	incr = getIncr(key, getCurrent2TodayEndMillisTime());//从001开始
	    }
	    DecimalFormat df=new DecimalFormat("10000");//三位序列号
	    return formatDate+df.format(incr);
	}
	
	public Long getIncr(String key, long liveTime) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        Long increment = entityIdCounter.getAndIncrement();

        if ((null == increment || increment.longValue() == 0) && liveTime > 0) {//初始设置过期时间
            entityIdCounter.expire(liveTime, TimeUnit.MILLISECONDS);//单位毫秒
        }
        return increment;
    }
 
	//现在到今天结束的毫秒数
	public Long getCurrent2TodayEndMillisTime() {
	    Calendar todayEnd = Calendar.getInstance();
	    // Calendar.HOUR 12小时制
	    // HOUR_OF_DAY 24小时制
	    todayEnd.set(Calendar.HOUR_OF_DAY, 23); 
	    todayEnd.set(Calendar.MINUTE, 59);
	    todayEnd.set(Calendar.SECOND, 59);
	    todayEnd.set(Calendar.MILLISECOND, 999);
	    return todayEnd.getTimeInMillis()-new Date().getTime();
	}
	
}
