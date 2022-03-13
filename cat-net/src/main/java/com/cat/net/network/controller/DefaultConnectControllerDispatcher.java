package com.cat.net.network.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.cat.net.exception.RepeatProtoException;
import com.cat.net.network.annotation.Cmd;
import com.cat.net.network.base.Commander;
import com.cat.net.network.base.ISession;
import com.cat.net.network.base.Packet;
import com.cat.net.util.MessageOutput;
import com.google.protobuf.AbstractMessageLite;

/**
 * 默认协议分发处理器, 开发人员使用时注册.
 */
public class DefaultConnectControllerDispatcher extends AbstractControllerDispatcher<Commander> {

	/**
	 * 初始化
	 * @param controllers 消息处理接口列表
	 * @throws Exception 异常
	 */
	public void initialize(List<IController> controllers) throws Exception {
		long startTime = System.currentTimeMillis();
		for (IController controller : controllers) {
			Method[] methods = controller.getClass().getDeclaredMethods();
			for (Method method : methods) {
				Cmd cmd = method.getAnnotation(Cmd.class);
				if (cmd == null) {
					continue;
				}
				//检查重复协议号
				if (mapper.containsKey(cmd.value())) {
					//log.error("协议号[{}]重复, 请检查!!!", cmd.id());
					throw new RepeatProtoException("发现重复协议号:"+cmd.value());
				}
				mapper.put(cmd.value(), Commander.create(controller, cmd.mustLogin(), method));
			}
		}
		//log.info("====>{}",mapper.keySet());
		log.info("The initialization message[{}] is complete and takes [{}] milliseconds.", mapper.size(),(System.currentTimeMillis() - startTime));
	}
	
	@Override
	public boolean checkInvoke(ISession session, Commander commander) {
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
	public void invoke(ISession session, Commander commander, Packet packet) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		int cmd = packet.cmd();
		long beginTime = System.currentTimeMillis();
		byte[] bytes = packet.data();
		
		IController controller = commander.getController();
		if (!controller.verify(session)) {
			log.info("收到协议[{}], 业务层权限验证失败, pid={}", cmd);
			return;
		}
		
		Method parser = commander.getProtobufParser();
		AbstractMessageLite<?, ?> params = (AbstractMessageLite<?, ?>) parser.invoke(null, (Object) bytes);
		
//		log.debug("收到协议[{}], pid={}, params={}, size={}B",
//					cmd, session.getUserData(), MessageOutput.create(params), bytes.length);
		
		//FIXME 这里处理的不好
		if (commander.getParamNum() == 2) {
			commander.getInvoker().invoke(session, params);
		}else if (commander.getParamNum() == 3) {
			commander.getInvoker().invoke(session, params, packet.seq());
		}
		
		long used = System.currentTimeMillis() - beginTime;
		// 协议处理超过1秒
		if (used > 1000) {
			log.info("协议[{}]处理慢!!!耗时{}ms", cmd, used);
		}
		//协议处理完逻辑后的操作
		controller.afterProcess(session, beginTime);
	}
	
}
