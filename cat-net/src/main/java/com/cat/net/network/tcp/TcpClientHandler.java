package com.cat.net.network.tcp;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cat.net.network.base.DefaultSession;
import com.cat.net.network.base.ISession;
import com.cat.net.network.controller.IConnectController;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 游戏服务器消息处理器 注意: 
 * 1. 一条连接对应一个TcpServerHandler 
 * 2. 一条连接对应一个GameSession
 * 3. 所有TcpServerHandler持有的IServerHandler都是同一个引用
 */
public class TcpClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

	private static final Logger log = LoggerFactory.getLogger(TcpClientHandler.class);

	private ISession session;
	private IConnectController serverHandler;

	public TcpClientHandler(IConnectController serverHandler) {
//		log.info("===============TcpServerHandler====================");
		this.serverHandler = serverHandler;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		log.info("===============channelActive====================");
		session = DefaultSession.create(ctx.channel()); // 新建session
		serverHandler.onConnect(session);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//		log.info("===============channelInactive====================");
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
//		log.info("===============channelRead0====================:{}", msg);
		serverHandler.onReceive(session, msg);
	}

}