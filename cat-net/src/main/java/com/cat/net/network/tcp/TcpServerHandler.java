package com.cat.net.network.tcp;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cat.net.network.base.DefaultSession;
import com.cat.net.network.base.ISession;
import com.cat.net.network.base.ISessionListener;
import com.cat.net.network.controller.IControllerDispatcher;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 游戏服务器消息处理器 注意: 
 * 1. 一条连接对应一个TcpServerHandler 
 * 2. 一条连接对应一个GameSession
 * 3. 所有TcpServerHandler持有的IServerHandler都是同一个引用
 */
public class TcpServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

	private static final Logger log = LoggerFactory.getLogger(TcpServerHandler.class);

	private ISession session;
	private IControllerDispatcher serverHandler;
	private ISessionListener listen;
	
	
	public TcpServerHandler(IControllerDispatcher serverHandler, ISessionListener listen) {
		log.info("===============TcpServerHandler====================");
		this.serverHandler = serverHandler;
		this.listen = listen;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		session = DefaultSession.create(ctx.channel()); // 新建session
		listen.onCreate(session);
		serverHandler.onConnect(session);
		log.info("===============channelActive===================={}, {}, {}", listen, session, serverHandler);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.info("===============channelInactive====================");
		listen.onRemove(session);
		serverHandler.onClose(session);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (cause instanceof IOException) {
			return;
		} else {
			cause.printStackTrace();
			serverHandler.onException(session, cause);
		}
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		log.info("===============channelRead0====================:{}", session);
		serverHandler.onReceive(session, msg);
	}

}
