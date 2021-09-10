package com.cat.net.network.tcp;

import com.cat.net.network.base.ISessionListener;
import com.cat.net.network.controller.IControllerDispatcher;
import com.cat.net.terminal.AbstractSocketServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * TCP服务启动器
 * 
 * @author Jeremy
 * @date 2020年7月9日
 */
public class TcpServerStarter extends AbstractSocketServer implements ISessionListener{
	
	public TcpServerStarter(IControllerDispatcher serverHandler, String ip, int port) {
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
				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(getInitializer());
		return bootstrap;
	}
	
	private ChannelInitializer<Channel> getInitializer() {
		final TcpServerHandler handler = new TcpServerHandler(serverHandler, this);
		return new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				final ChannelPipeline pipeline = ch.pipeline();
				// idle connection detection
				pipeline.addLast("idleState", new IdleStateHandler(30, 30, 60)); // 默认30秒
				// pipeline.addLast("idleDetection", new CustomeHeartbeatHandler());
				
				// inbound
				pipeline.addLast("lengthDecoder", new LengthFieldBasedFrameDecoder(8 * 1024, 0, 4, 0, 4));
				pipeline.addLast("serverHandler", handler);
				
				// outbound
				pipeline.addLast("lengthEncoder", new LengthFieldPrepender(4));
				pipeline.addLast("protocolEncoder", new TcpProtocolEncoder());
			}
		};
	}

	@Override
	protected String getServerType() {
		return "Tcp";
	}

}
