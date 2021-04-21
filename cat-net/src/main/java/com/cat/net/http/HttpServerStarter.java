package com.cat.net.http;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cat.net.http.controller.IRequestController;
import com.cat.net.http.handler.HttpServerHandler;
import com.cat.net.server.AbstractServer;
import com.google.common.collect.Maps;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpServerStarter extends AbstractServer{
	
	private static final Logger log = LoggerFactory.getLogger(HttpServerStarter.class);
	
	private IRequestController controller;
	
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	
	public HttpServerStarter(IRequestController controller) {
		this.controller = controller;
	}
	
	public HttpServerStarter(IRequestController controller, int port) {
		super(port);
		this.controller = controller;
	}
	
	public HttpServerStarter(IRequestController controller, String ip,int port) {
		super(ip, port);
		this.controller = controller;
	}

	@Override
	public boolean startServer() throws Exception {
		ChannelFuture future = null;
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bossGroup = new NioEventLoopGroup(1);
			workerGroup = new NioEventLoopGroup(2);
			bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							//HttpServerCodec 封装了 HttpResponseEncoder+HttpRequestDecoder
							ch.pipeline().addLast("codec", new HttpServerCodec());
//							ch.pipeline().addLast("encoder", new HttpResponseEncoder());
//							ch.pipeline().addLast("decoder", new HttpRequestDecoder());
							ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
							ch.pipeline().addLast(new HttpServerHandler(controller));
						}
					});

			bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
			bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
			bootstrap.option(ChannelOption.SO_BACKLOG, 128);
			bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			future = bootstrap.bind(port).sync();
			future.channel().closeFuture().addListener(ChannelFutureListener.CLOSE);
			return true;
		} finally {
			if (future != null && future.isSuccess()) {
				controller.serverStatus(true);//开服
				log.info("Netty [HTTP] server listening {} on port {} and ready for connections...", ip, port);
			} else {
				log.error("Netty [HTTP] server start up Error!");
			}
		}
	}

	@Override
	public void stopServer() throws Exception {
		if (isRunning()) {
			if (bossGroup != null) {
				bossGroup.shutdownGracefully();
			}
			if (workerGroup != null) {
				workerGroup.shutdownGracefully();
			}
			controller.serverStatus(false);// 关服
		}else {
			log.info("HTTP网络服务未在运行状态");
		}
	}
	
}
