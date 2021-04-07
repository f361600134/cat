package com.cat.orm.core.redis;

import com.cat.orm.core.base.BasePo;

public interface IRedisRepository<T extends BasePo> {
	
	public void add(T t);
	
	public T get(String key);
	
}
