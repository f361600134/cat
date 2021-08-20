package com.cat.net.network.client;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cat.net.network.bootstrap.IdleDetectionHandler;
import com.cat.net.network.controller.IConnectController;
import com.cat.net.network.tcp.TcpProtocolEncoder;
import com.cat.net.terminal.AbstractClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * TCP连接客户端启动器
 * @author Jeremy
 *
 */
public class TcpClientStarter extends AbstractClient{
	
	private static final Logger log = LoggerFactory.getLogger(TcpClientHandler.class);
	
	private IConnectController handler;
	
	private EventLoopGroup group;
	
    private Channel channel;
    
    public TcpClientStarter(IConnectController handler, String ip, int port) {
		super(ip, port);
		this.handler = handler;
	}

	@Override
	public boolean connect() throws Exception {
		 group = new NioEventLoopGroup(1, new DefaultThreadFactory("TCP_CLIENT_BOSS"));
		 try {
			Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group) // 注册线程池
                    .channel(NioSocketChannel.class) // 使用NioSocketChannel来作为连接用的channel类
                    .remoteAddress(new InetSocketAddress(this.getIp(), this.getPort())) // 绑定连接端口和host信息
                    .handler(new ChannelInitializer<SocketChannel>() { // 绑定连接初始化器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                        	ch.pipeline().addLast(new TcpProtocolEncoder());
                            //ch.pipeline().addLast(new EchoClientHandler());
                        	ch.pipeline().addLast("idleDetection", new IdleDetectionHandler());
							// inbound
                        	ch.pipeline().addLast("lengthDecoder", new LengthFieldBasedFrameDecoder(8 * 1024, 0, 4, 0, 4));
//        							pipeline.addLast(new FixedLengthFrameDecoder(frameLength));
                        	ch.pipeline().addLast("serverHandler", new TcpClientHandler(handler));
							// outbound
                        	ch.pipeline().addLast("lengthEncoder", new LengthFieldPrepender(4));
                        	ch.pipeline().addLast("protocolEncoder", new TcpProtocolEncoder());
                        }
                    });

            ChannelFuture cf = bootstrap.connect().sync(); // 异步连接服务器
            cf.channel().closeFuture().sync(); // 异步等待关闭连接channel
        } finally {
            group.shutdownGracefully().sync(); // 释放线程池资源
        }
		return false;
	}

	@Override
	public void disConnect() throws Exception {
		if (isRunning()) {
			handler.serverStatus(false);// 关掉连接
			if (group != null) {
				group.shutdownGracefully();
			}
			if (channel!=null) {
				channel.close();
			}
		}else {
			log.info("TCP网络服务未在运行状态");
		}
	}

	@Override
	public void tryConnect() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkConnect() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disConnectNow() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
