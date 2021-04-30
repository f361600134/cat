package com.cat.net.network.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cat.net.core.executor.DisruptorStrategy;
import com.cat.net.network.base.Commander;
import com.cat.net.network.base.GameSession;
import com.cat.net.network.base.Packet;
import com.cat.net.network.process.ControllerDispatcher;

import io.netty.buffer.ByteBuf;

/**
 * 默认游戏服分发处理器
 */
//@Component
public class DefaultServerController implements IServerController {

	private static final Logger log = LoggerFactory.getLogger(DefaultServerController.class);

	@Autowired
	protected ControllerDispatcher processor;

	protected boolean serverRunning; // 服务器状态, true-运行中
	
	public DefaultServerController(){
	}

	public void onConnect(GameSession session) {
		log.info("默认分发处理器, 客户端连接游戏服:{}", session.getChannel().remoteAddress());
	}

	public void onReceive(GameSession session, ByteBuf message) {
		int cmd = 0;
		try {
			if (!serverRunning) {
				log.error("默认分发处理器, 服务器不在运行状态, 舍弃消息");
				return;
			}
			final Packet packet = Packet.decode(message);
			cmd = packet.cmd();
			Commander commander = processor.getCommander(cmd);
			if (commander == null) {
				log.info("收到未处理协议, cmd=[{}]", cmd);
				return;
			}
			if (commander.isMustLogin()) {
				// TODO 未登录请求协议, 通知断开连接
				log.info("协议[{}]需要登录成功后才能请求", cmd);
				return;
			}
			//	添加到任务队列
//			DisruptorDispatchTask task = new DisruptorDispatchTask(processor, session, packet);
//			DisruptorStrategy.get(DisruptorStrategy.SINGLE).execute(session.getId(), task);
			DisruptorStrategy.get(DisruptorStrategy.SINGLE).execute(session.getId(), ()->{
				try {
					processor.invoke(session, packet);
					log.info("====> DisruptorDispatchTask run, threadName:{}", Thread.currentThread().getName());
				} catch (Exception e) {
					log.error("DisruptorDispatchTask error", e);
				}
			});
			
		} catch (Exception e) {
			log.error("Packet调用过程出错, cmd={}", cmd, e);
		}

	}

	@Override
	public void onClose(GameSession session) {
		log.info("默认分发处理器, 客户端连接断开:{}", session.getChannel().remoteAddress());
	}

	@Override
	public void onException(GameSession session, Throwable e) {
		log.error("默认分发处理器, 游戏协议通信过程出错", e);
	}

	@Override
	public void serverStatus(boolean running) {
		this.serverRunning = running;
	}

}
