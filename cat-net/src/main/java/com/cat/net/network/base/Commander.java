package com.cat.net.network.base;

import java.lang.reflect.Method;

import com.cat.net.core.reflect.MethodInvoker;
import com.cat.net.network.controller.IController;

/**
 * 消息封装
 * @author Jeremy
 * @date 2020年7月4日
 */
public class Commander {
	
	private final IController controller;
	private final MethodInvoker invoker;
	private final boolean mustLogin;
	private final Method protobufParser;
	/**
	 * 参数数量
	 */
	private final int paramNum;

	public Commander(IController controller, boolean mustLogin, Method method) throws Exception {
		this.controller = controller;
		this.invoker = MethodInvoker.create(controller, method);
		this.mustLogin = mustLogin;
		Class<?> paramType = method.getParameterTypes()[1];
		this.protobufParser = paramType.getMethod("parseFrom", byte[].class);
		this.paramNum = method.getParameterCount();
	}
	
	public static Commander create(IController controller, boolean mustLogin, Method method) throws Exception {
		return new Commander(controller, mustLogin, method);
	}

	public boolean isMustLogin() {
		return mustLogin;
	}

	
	public MethodInvoker getInvoker() {
		return invoker;
	}

	public Method getProtobufParser() {
		return protobufParser;
	}

	public int getParamNum() {
		return paramNum;
	}
	
	public IController getController() {
		return controller;
	}
}