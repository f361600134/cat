package com.cat.net.network.client;

import com.cat.net.network.base.DefaultSession;
import com.cat.net.network.controller.IControllerDispatcher;

import io.netty.channel.ChannelHandlerContext;

/**
 * 游戏服务器消息处理器 注意: 
 * 1. 一条连接对应一个TcpClientHandler 
 * 2. 一条连接对应一个session
 * 3. 所有TcpClientHandler持有的IConnectController都是同一个引用
 */
public class RpcClientHandler extends TcpClientHandler {

	private RpcClientStarter client;
	
	public RpcClientHandler(IControllerDispatcher clientHandler, RpcClientStarter client) {
		super(clientHandler);
		this.client = client;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//log.info("===============TcpClientHandler-channelActive====================");
		session = DefaultSession.create(ctx.channel()); // 新建session
		session.setUserData(client);
		handler.onConnect(session);
	}

}
