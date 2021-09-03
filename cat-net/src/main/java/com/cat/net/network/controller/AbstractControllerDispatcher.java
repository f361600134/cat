package com.cat.net.network.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cat.net.core.executor.DisruptorStrategy;
import com.cat.net.network.base.ISession;
import com.cat.net.network.base.Packet;

import io.netty.buffer.ByteBuf;

public abstract class AbstractControllerDispatcher<T> implements IControllerDispatcher {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected Map<Integer, T> mapper = new HashMap<>();
	
	protected boolean serverRunning; // 服务器状态, true-运行中
	
	public AbstractControllerDispatcher(){
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
	
	@Override
	public void onReceive(ISession session, ByteBuf message) {
		if (!serverRunning) {
			log.error("默认分发处理器, 服务不在运行状态, 舍弃消息");
			return;
		}
		Packet packet = Packet.decode(message);
		int cmd = packet.cmd();
		T commander = mapper.get(cmd);
		if (commander == null) {
			log.info("收到未处理协议, cmd=[{}]", cmd);
			return;
		}
		if (!checkInvoke(session, commander)) {
			return;
		}
		//	添加到任务队列
		DisruptorStrategy.get(DisruptorStrategy.SINGLE).execute(session.getSessionId(), ()->{
			try {
				this.invoke(session, commander, packet);
				log.info("====> DisruptorDispatchTask run, threadName:{}", Thread.currentThread().getName());
			} catch (Exception e) {
				log.error("DisruptorDispatchTask error", e);
			}
		});
	}
	
	/**
	 * 校验invoke
	 * @param commander
	 * @return
	 */
	public abstract boolean checkInvoke(ISession session, T commander);
	
	/**
	 * 协议分发
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public abstract void invoke(ISession session, T commander, Packet packet) throws Exception;
	

	@Override
	public void serverStatus(boolean running) {
		this.serverRunning = running;
	}

}
