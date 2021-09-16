package com.cat.net.network.controller;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cat.net.core.executor.DisruptorStrategy;
import com.cat.net.exception.RepeatProtoException;
import com.cat.net.network.annotation.Rpc;
import com.cat.net.network.base.AbstractProtocol;
import com.cat.net.network.base.ISession;
import com.cat.net.network.base.Packet;
import com.cat.net.network.base.RemoteCaller;
import com.cat.net.network.base.RemoteServerCaller;
import com.cat.net.network.rpc.IRpcStarter;
import com.cat.net.network.rpc.RpcCallbackCache;
import com.cat.net.util.SerializationUtil;

import io.netty.buffer.ByteBuf;

public class DefaultRpcDispatcher implements IControllerDispatcher {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * 回调消息映射
	 */
	protected Map<Integer, RemoteCaller> callbackMapper = new HashMap<>();
	/**
	 * 主动监听的消息
	 */
	protected Map<Integer, RemoteServerCaller> requesterMapper = new HashMap<>();
	
	protected boolean serverRunning; // 服务器状态, true-运行中

	@Override
	public void onReceive(ISession session, ByteBuf message) {
		if (!serverRunning) {
			log.error("默认分发处理器, 服务不在运行状态, 舍弃消息");
			return;
		}
		
		Packet packet = Packet.decode(message);
		final int cmd = packet.cmd();
		final byte[] bytes = packet.data();
		
		//获取消息
		RemoteCaller caller = null;
		if ((caller = callbackMapper.get(cmd))!= null) {//回调消息不为null, 则处理
			if (!checkInvoke(session, caller)) {
				log.info("回调协议验证不通过, 拒绝处理, cmd:[{}]", cmd);
				return;
			}
			Class<?> clazz = caller.getParamType();
			AbstractProtocol params = (AbstractProtocol) SerializationUtil.deserialize(bytes, clazz);
			// 处理转发请求
			IRpcStarter rpcStarter = session.getUserData();
			RpcCallbackCache callbackCache = rpcStarter.getCallbackCache();
			callbackCache.receiveResponse(packet.seq(), cmd, params);
		}else if((caller = requesterMapper.get(cmd)) != null){
			if (!checkInvoke(session, caller)) {
				log.info("监听协议验证不通过, 拒绝处理, cmd:[{}]", cmd);
				return;
			}
			RemoteServerCaller serverCaller =  (RemoteServerCaller)caller;
			Class<?> clazz = caller.getParamType();
			AbstractProtocol params = (AbstractProtocol) SerializationUtil.deserialize(bytes, clazz);
			//添加到任务队列
			DisruptorStrategy.get(DisruptorStrategy.SINGLE).execute(session.getSessionId(), ()->{
				try {
					serverCaller.getInvoker().invoke(session, params, packet.seq());
					//log.info("====> DisruptorDispatchTask run, threadName:{}", Thread.currentThread().getName());
				} catch (Exception e) {
					log.error("DisruptorDispatchTask error", e);
				}
			});
		}
	}

	@Override
	public void serverStatus(boolean running) {
		this.serverRunning = running;
	}
	
	public void onConnect(ISession session) {
		log.info("默认分发处理器, 客户端连接服务:{}", session.getChannel().remoteAddress());
	}

	@Override
	public void onClose(ISession session) {
		log.info("默认分发处理器, 客户端连接断开:{}", session.getChannel().remoteAddress());
	}

	@Override
	public void onException(ISession session, Throwable e) {
		log.error("默认分发处理器, 协议通信过程出错", e);
	}
	
	/**
	 * 初始化
	 * @param controllers 消息处理接口列表
	 * @throws Exception 异常
	 */
	public void initialize(List<IRpcController> controllers) throws Exception {
		long startTime = System.currentTimeMillis();
		for (IRpcController controller : controllers) {
			boolean bool = initCallbackMapper(controller);
			if (!bool) {
				initRequesterMapper(controller);
			}
		}
		log.info("The initialization [rpc] callback's message[{}] and server's message[{}] are complete and takes [{}] milliseconds."
				, callbackMapper.size(), requesterMapper.size(), (System.currentTimeMillis() - startTime));
	}
	
	private boolean initCallbackMapper(IRpcController controller) throws Exception{
		Rpc rpc =  controller.getClass().getAnnotation(Rpc.class);
		if (rpc == null) {
			return false;
		}
		if (rpc.type() != Rpc.REQUEST) {
			return false;
		}
		//检查重复协议号
		if (callbackMapper.containsKey(rpc.value())) {
			throw new RepeatProtoException("发现回调重复协议号:"+rpc.value());
		}
		callbackMapper.put(rpc.value(), RemoteCaller.create(rpc.isAuth(), controller));
		return true;
	}
	
	private void initRequesterMapper(IRpcController controller) throws Exception{
		Method[] methods = controller.getClass().getDeclaredMethods();
		for (Method method : methods) {
			Rpc cmd = method.getAnnotation(Rpc.class);
			if (cmd == null) {
				continue;
			}
			if (cmd.type() != Rpc.RESPONSE) {
				continue;
			}
			//检查重复协议号
			if (requesterMapper.containsKey(cmd.value())) {
				throw new RepeatProtoException("发现监听重复协议号:"+cmd.value());
			}
			requesterMapper.put(cmd.value(), RemoteServerCaller.create(controller, cmd.isAuth(), method));
		}
	}
	
	public boolean checkInvoke(ISession session, RemoteCaller commander) {
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
	
}
