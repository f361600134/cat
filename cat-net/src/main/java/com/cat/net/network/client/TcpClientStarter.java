package com.cat.net.network.client;

import java.net.InetSocketAddress;

import com.cat.net.network.controller.IControllerDispatcher;
import com.cat.net.network.tcp.TcpProtocolEncoder;
import com.cat.net.terminal.AbstractSocketClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * TCP连接客户端启动器, 可以吧普通客户端跟rpc客户端分离
 * @author Jeremy
 *
 */
public class TcpClientStarter extends AbstractSocketClient{
	
	public TcpClientStarter(int connectId, String nodeType, String ip, int port, IControllerDispatcher handler) {
		super(connectId, nodeType, ip, port, handler);
	}

	@Override
	protected Bootstrap getBootstrap() {
		Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group) // 注册线程池
            .channel(NioSocketChannel.class) // 使用NioSocketChannel来作为连接用的channel类
            .remoteAddress(new InetSocketAddress(this.getIp(), this.getPort())) // 绑定连接端口和host信息
            .handler(getInitializer());
		return bootstrap;
	}
	
	private ChannelInitializer<SocketChannel> getInitializer() {
		return new ChannelInitializer<SocketChannel>() { // 绑定连接初始化器
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
            	ChannelPipeline pipeline = ch.pipeline();
            	pipeline.addLast("idleState", new IdleStateHandler(0, 0, 30));
            	//ch.pipeline().addLast("idleDetection", new CustomeHeartbeatHandler());
				// inbound
            	pipeline.addLast("lengthDecoder", new LengthFieldBasedFrameDecoder(8 * 1024, 0, 4, 0, 4));
            	pipeline.addLast("clientHandler", clientHandler);
				// outbound
            	pipeline.addLast("lengthEncoder", new LengthFieldPrepender(4));
            	pipeline.addLast("protocolEncoder", new TcpProtocolEncoder());
            }
        };
	}

	@Override
	protected String getServerType() {
		return "TCP";
	}
	
}
