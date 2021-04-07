package com.cat.orm.core.redis;

import java.io.Serializable;

import com.cat.orm.core.base.BasePo;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisRepostory<T extends BasePo> implements IRedisRepository<T>{
		
	protected RedisTemplate<String, Serializable> redisTemplate;
	
	private final Class<?> clazz;
	private final String clientName;
	
	public RedisRepostory(T t, RedisTemplate<String, Serializable> redisTemplate){
		LettuceConnectionFactory factory = (LettuceConnectionFactory)redisTemplate.getConnectionFactory();
		String name = factory.getClientName();
		name = name == null ? "Default" : name;
		
		this.clazz = t.getClass();
		this.clientName = name.concat("_").concat(clazz.getSimpleName());
		this.redisTemplate = redisTemplate;
	}
	
	public RedisRepostory(Class<T> clazz, RedisTemplate<String, Serializable> redisTemplate){
		LettuceConnectionFactory factory = (LettuceConnectionFactory)redisTemplate.getConnectionFactory();
		String name = factory.getClientName();
		name = name == null ? "Default" : name;
		
		this.clazz = clazz.getClass();
		this.clientName = name.concat("_").concat(clazz.getSimpleName());
		this.redisTemplate = redisTemplate;
	}
	
	public void add(T t) {
		//redisTemplate.expire(clientName, 1, TimeUnit.MINUTES);
		redisTemplate.opsForHash().put(clientName, t.cacheId(), t);
	}
	
	@SuppressWarnings("unchecked")
	public T get(String key) {
		return (T)redisTemplate.opsForHash().get(clientName, key);
	}
	
	public String getClientName() {
		return clientName;
	}
	
}
