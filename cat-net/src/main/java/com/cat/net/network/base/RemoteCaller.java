package com.cat.net.network.base;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.cat.net.network.rpc.IResponseCallback;
import com.google.protobuf.AbstractMessageLite;

/**
 * 远程调用对象
 * @author Jeremy
 */
public class RemoteCaller {
	
	private final boolean isAuth;
	private final Method protobufParser;
	
	public RemoteCaller(IResponseCallback<? extends AbstractMessageLite<?,?>> callback, boolean isAuth) throws Exception {
//		Type superClass = callback.getClass().getGenericSuperclass();
		Type superClass = callback.getClass().getGenericInterfaces()[0];
		Class<?> basePoClazz = (Class<?>) (((ParameterizedType) superClass).getActualTypeArguments()[0]);
		this.isAuth = isAuth;
		this.protobufParser = basePoClazz.getMethod("parseFrom", byte[].class);
	}
	
	/**
	 * 静态工厂
	 * @param callback
	 * @param isAuth
	 * @return
	 */
	public static RemoteCaller create(IResponseCallback<? extends AbstractMessageLite<?,?>> callback, boolean isAuth) throws Exception {
		return new RemoteCaller(callback, isAuth);
	}

	public boolean isAuth() {
		return isAuth;
	}

	public Method getProtobufParser() {
		return protobufParser;
	}
	
}
