package com.cat.net.network.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cat.net.network.base.ISession;
import com.cat.net.network.base.Packet;
import com.cat.net.network.controller.chain.HandlerChain;
import com.cat.net.network.controller.chain.HandlerRequestMessage;
import com.cat.net.network.controller.chain.HandlerResponseMessage;

import io.netty.buffer.ByteBuf;

public class DefaultRpcDispatcher implements IControllerDispatcher {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected HandlerChain handlerChain = new HandlerChain();
	
	protected boolean serverRunning; // 服务器状态, true-运行中

	@Override
	public void onReceive(ISession session, ByteBuf message) {
		if (!serverRunning) {
			log.error("默认分发处理器, 服务不在运行状态, 舍弃消息");
			return;
		}
		Packet packet = Packet.decode(message);
		handlerChain.handler(session, packet);
	}

	/**
	 * 初始化
	 * @param controllers 消息处理接口列表
	 * @throws Exception 异常
	 */
	public void initialize(List<IRpcController> controllers) throws Exception {
		long startTime = System.currentTimeMillis();
		
		HandlerResponseMessage response = new HandlerResponseMessage();
		HandlerRequestMessage request = new HandlerRequestMessage();
		handlerChain.addWork(response);
		handlerChain.addWork(request);
		
		for (IRpcController controller : controllers) {
			handlerChain.addController(controller);
		}
		log.info("The initialization [rpc] callback's message[{}] and server's message[{}] are complete and takes [{}] milliseconds."
				, request.size(), response.size(), (System.currentTimeMillis() - startTime));
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
	
	
}
