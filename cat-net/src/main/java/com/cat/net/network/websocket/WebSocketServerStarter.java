package com.cat.net.network.websocket;

import com.cat.net.network.bootstrap.IdleDetectionHandler;
import com.cat.net.network.controller.IControllerDispatcher;
import com.cat.net.terminal.AbstractSocketServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * TCP服务启动器
 * 
 * @author Jeremy
 * @date 2020年7月9日
 */
public class WebSocketServerStarter extends AbstractSocketServer {

	public WebSocketServerStarter(IControllerDispatcher serverHandler, String ip, int port) {
		super(ip, port, serverHandler);
	}

	@Override
	protected ServerBootstrap getBootstrap() {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup)
		.channel(NioServerSocketChannel.class)
		.option(ChannelOption.SO_REUSEADDR, true)
		.childOption(ChannelOption.SO_RCVBUF, 128 * 1024)
		.childOption(ChannelOption.SO_SNDBUF, 128 * 1024)
		.childOption(ChannelOption.SO_KEEPALIVE, true)
		.childOption(ChannelOption.TCP_NODELAY, true)
		// heap buf 's
        // better
        .childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(false))
        .handler(new LoggingHandler(LogLevel.INFO))
		.childHandler(getInitializer());
		return bootstrap;
	}
	
	private ChannelInitializer<Channel> getInitializer() {
		return new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast("encoder", new HttpResponseEncoder());
				pipeline.addLast("decoder", new HttpRequestDecoder());
				//聚合器，使用websocket会用到
				pipeline.addLast(new HttpObjectAggregator(65536));
				    
				// idle connection detection
				pipeline.addLast("idleState", new IdleStateHandler(30, 0, 0)); // 默认30秒
				pipeline.addLast("idleDetection", new IdleDetectionHandler());
				/*
				 * inbound
				 * websocket
				 * */
				//pipeline.addLast("lengthDecoder", new LengthFieldBasedFrameDecoder(8 * 1024, 0, 4, 0, 4));
				pipeline.addLast("serverHandler", new WebsocketServerHandler(serverHandler));
				// outbound
				//pipeline.addLast("lengthEncoder", new LengthFieldPrepender(4));
				pipeline.addLast("protocolEncoder", new WebsocketProtocolEncoder());
				
			}
		};
	}
	
	@Override
	protected String getServerType() {
		return "WebScoket";
	}

}
