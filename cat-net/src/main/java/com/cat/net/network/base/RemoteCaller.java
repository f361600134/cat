package com.cat.net.network.base;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.cat.net.network.controller.IRpcController;
import com.cat.net.network.rpc.IResponseCallback;

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
//	public static RemoteCaller create(boolean mustLogin, IResponseCallback<? extends AbstractProtocol> callback) throws Exception {
//		//Class<?> paramType = method.getParameterTypes()[1];
//		Type superClass = callback.getClass().getGenericInterfaces()[0];
//		Class<?> basePoClazz = (Class<?>) (((ParameterizedType) superClass).getActualTypeArguments()[0]);
//		return new RemoteCaller(mustLogin, basePoClazz);
//	}
	
	public static RemoteCaller create(boolean mustLogin, IRpcController callback) throws Exception {
		Type superClass = callback.getClass().getGenericInterfaces()[0];
		Class<?> basePoClazz = (Class<?>) (((ParameterizedType) superClass).getActualTypeArguments()[0]);
		return new RemoteCaller(mustLogin, basePoClazz);
	}
	
//	private final boolean isAuth;
//	private final Method protobufParser;
//	
//	public RemoteCaller(IResponseCallback<? extends AbstractProtocol> callback, boolean isAuth) throws Exception {
////		Type superClass = callback.getClass().getGenericSuperclass();
//		Type superClass = callback.getClass().getGenericInterfaces()[0];
//		Class<?> basePoClazz = (Class<?>) (((ParameterizedType) superClass).getActualTypeArguments()[0]);
//		this.isAuth = isAuth;
//		this.protobufParser = basePoClazz.getMethod("parseFrom", byte[].class);
//	}
//	
//	/**
//	 * 静态工厂
//	 * @param callback
//	 * @param isAuth
//	 * @return
//	 */
//	public static RemoteCaller create(IResponseCallback<? extends AbstractProtocol> callback, boolean isAuth) throws Exception {
//		return new RemoteCaller(callback, isAuth);
//	}
//
//	public boolean isAuth() {
//		return isAuth;
//	}
//
//	public Method getProtobufParser() {
//		return protobufParser;
//	}
//	
}
