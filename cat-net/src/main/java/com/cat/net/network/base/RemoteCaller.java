package com.cat.net.network.base;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.cat.net.network.controller.IRpcController;

/**
 * 远程调用对象, 客户端用命令封装
 * @author Jeremy
 */
public class RemoteCaller {
	
	private final boolean mustLogin;
	private final Class<?> paramType;
	
	public RemoteCaller(boolean mustLogin, Class<?> paramType) {
		super();
		this.mustLogin = mustLogin;
		this.paramType = paramType;
	}
	public boolean isMustLogin() {
		return mustLogin;
	}
	public Class<?> getParamType() {
		return paramType;
	}
	
	public static RemoteCaller create(boolean mustLogin, IRpcController callback) {
		Type superClass = callback.getClass().getGenericInterfaces()[0];
		Class<?> basePoClazz = (Class<?>) (((ParameterizedType) superClass).getActualTypeArguments()[0]);
		return new RemoteCaller(mustLogin, basePoClazz);
	}
	
}
