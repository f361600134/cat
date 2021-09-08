package com.cat.net.network.client;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cat.net.network.base.DefaultSession;
import com.cat.net.network.base.ISession;
import com.cat.net.network.controller.IControllerDispatcher;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 游戏服务器消息处理器 注意: 
 * 1. 一条连接对应一个TcpClientHandler 
 * 2. 一条连接对应一个session
 * 3. 所有TcpClientHandler持有的IConnectController都是同一个引用
 */
public class TcpClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

	private static final Logger log = LoggerFactory.getLogger(TcpClientHandler.class);

	private ISession session;
	private IControllerDispatcher handler;

	public TcpClientHandler(IControllerDispatcher clientHandler) {
		//log.info("===============TcpClientHandler====================");
		this.handler = clientHandler;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//log.info("===============TcpClientHandler-channelActive====================");
		session = DefaultSession.create(ctx.channel()); // 新建session
		handler.onConnect(session);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		//log.info("===============TcpClientHandler-channelInactive====================");
		handler.onClose(session);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (cause instanceof IOException) {
			return;
		} else {
			cause.printStackTrace();
			handler.onException(session, cause);
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		//log.info("===============TcpClientHandler-channelRead0====================:{}", msg);
		handler.onReceive(session, msg);
	}
	
	/**
	 * 返回连接会话
	 * @return
	 */
	public ISession getSession() {
		return session;
	}
	
	/**
	 * 返回连接会话
	 * @return
	 */
	public IControllerDispatcher getHandler() {
		return handler;
	}

}
