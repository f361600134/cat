package com.cat.orm.core.redis;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.cat.orm.core.base.BasePo;
import org.springframework.data.redis.core.RedisTemplate;


public class RedisProcessor {
	
	private Map<String, IRedisRepository<BasePo>> commonDaoMap;
	
	/**
	 * @param basePoMap
	 * @param jdbcTemplate
	 */
	public RedisProcessor(Map<String, BasePo> basePoMap, RedisTemplate<String, Serializable> redisTemplate) {
		this.commonDaoMap = new HashMap<>();
		for (String key : basePoMap.keySet()) {
			BasePo po = basePoMap.get(key);
			IRedisRepository<BasePo> dao = new RedisRepostory<>(po, redisTemplate);
			commonDaoMap.put(key, dao);//注意,通过spring注入进来的Map, key默认全部小写
		}
	}
	
	/**
	 * @param basePoMap
	 * @param jdbcTemplate
	 */
	public RedisProcessor(Collection<Class<BasePo>> clazzs, RedisTemplate<String, Serializable> redisTemplate) {
		this.commonDaoMap = new HashMap<>();
		for (Class<BasePo> clazz : clazzs) {
			IRedisRepository<BasePo> dao = new RedisRepostory<>(clazz, redisTemplate);
			commonDaoMap.put(clazz.getSimpleName(), dao);//注意,通过spring注入进来的Map, key默认全部小写
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends BasePo> IRedisRepository<T> getRepository(String name) {
		if (name == null || name.equals("")) {
			throw new NullPointerException("name is can not be null:"+name);
		}
		return (IRedisRepository<T>)commonDaoMap.get(name);
	}
	
	public <T extends BasePo> IRedisRepository<T> getRepository(Class<T> clazz) {
		if (clazz == null) {
			throw new NullPointerException("clazz is can not be null:"+clazz);
		}
		String name = clazz.getSimpleName().toLowerCase();
		return getRepository(name);
	}
	
	public <T extends BasePo> IRedisRepository<T> getRepository(T clazz) {
		if (clazz == null) {
			throw new NullPointerException("clazz is can not be null:"+clazz);
		}
		return getRepository(clazz.poName().toLowerCase());
	}
	
	/**
	 * 添加一個緩存對象
	 * @param <T>
	 * @param po
	 */
	public <T extends BasePo> void add(T po) {
		IRedisRepository<T> rep = getRepository(po);
		if (rep == null) {
			throw new NullPointerException("Can not find Repository by the Pojo:"+po);
		}
		rep.add(po);
	}
	
	/**
	 * 添加一個緩存對象
	 * @param <T>
	 * @param po
	 */
	public <T extends BasePo> T get(Class<T> clazz, String key) {
		IRedisRepository<T> rep = getRepository(clazz);
		if (rep == null) {
			throw new NullPointerException("Can not find Repository by the key:"+key);
		}
		return rep.get(key);
	}
	
}
