package com.cat.net.network.base;

import java.lang.reflect.Method;

import com.cat.net.core.reflect.MethodInvoker;
import com.cat.net.network.controller.IRpcController;

/**
 * 服务端用命令封装
 * @author Jeremy
 */
public class RemoteServerCaller extends RemoteCaller{
	
	private final MethodInvoker invoker;

	public RemoteServerCaller(boolean mustLogin, Class<?> paramType, MethodInvoker invoker) {
		super(mustLogin, paramType);
		this.invoker = invoker;
	}

	public MethodInvoker getInvoker() {
		return invoker;
	}
	
	public static RemoteServerCaller create(IRpcController controller, boolean mustLogin, Method method) throws Exception {
		MethodInvoker invoker = MethodInvoker.create(controller, method);
		Class<?> paramType = method.getParameterTypes()[1];
		return new RemoteServerCaller(mustLogin, paramType, invoker);
	}
	
}
