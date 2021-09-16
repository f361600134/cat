package com.cat.net.network.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.cat.net.exception.RepeatProtoException;
import com.cat.net.network.annotation.RpcRequest;
import com.cat.net.network.base.ISession;
import com.cat.net.network.base.Packet;
import com.cat.net.network.base.RemoteServerCaller;
import com.cat.net.util.SerializationUtil;

@Deprecated
public class DefaultRemoteCallServerDispatcher extends AbstractControllerDispatcher<RemoteServerCaller> {
	
	/**
	 * 初始化
	 * @param controllers 消息处理接口列表
	 * @throws Exception 异常
	 */
	public void initialize(List<IRpcController> controllers) throws Exception {
		long startTime = System.currentTimeMillis();
		for (IRpcController controller : controllers) {
			Method[] methods = controller.getClass().getDeclaredMethods();
			for (Method method : methods) {
				RpcRequest cmd = method.getAnnotation(RpcRequest.class);
				if (cmd == null) {
					continue;
				}
				//检查重复协议号
				if (mapper.containsKey(cmd.value())) {
					throw new RepeatProtoException("发现重复协议号:"+cmd.value());
				}
				mapper.put(cmd.value(), RemoteServerCaller.create(controller, cmd.isAuth(), method));
			}
		}
		log.info("The initialization [server rpc] message[{}] is complete and takes [{}] milliseconds.", mapper.size(),(System.currentTimeMillis() - startTime));
	}
	
	@Override
	public boolean checkInvoke(ISession session, RemoteServerCaller commander) {
		//不需要登录
		if (!commander.isMustLogin()) {
			return true;
		}
		//需要登录, 且已经登录设置值, 则返回true
		if (session.getUserData() != null) {
			return true;
		}
		return false;
	}
	

	/**
	 * 协议分发
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	@Override
	public void invoke(ISession session, RemoteServerCaller commander, Packet packet) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		int cmd = packet.cmd();
		long begin = System.currentTimeMillis();
		byte[] bytes = packet.data();
		
		Class<?> struct = commander.getParamType();
		Object obj = SerializationUtil.deserialize(bytes, struct);
		
		log.debug("收到协议[{}], pid={}, params={}, size={}B",
				cmd, session.getUserData(), obj, bytes.length);
		
		commander.getInvoker().invoke(session, obj, packet.seq());
		
		long used = System.currentTimeMillis() - begin;
		// 协议处理超过1秒
		if (used > 1000) {
			log.error("协议[{}]处理慢!!!耗时{}ms", cmd, used);
		}
	}
	
}
