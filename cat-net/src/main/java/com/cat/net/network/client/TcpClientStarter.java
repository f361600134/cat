package com.cat.net.network.client;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cat.net.network.base.IProtocol;
import com.cat.net.network.base.ISession;
import com.cat.net.network.base.Packet;
import com.cat.net.network.bootstrap.IdleDetectionHandler;
import com.cat.net.network.controller.IControllerDispatcher;
import com.cat.net.network.tcp.TcpProtocolEncoder;
import com.cat.net.terminal.AbstractClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * TCP连接客户端启动器, 可以吧普通客户端跟rpc客户端分离
 * @author Jeremy
 *
 */
public class TcpClientStarter extends AbstractClient{
	
	private static final Logger log = LoggerFactory.getLogger(TcpClientHandler.class);
	
	private EventLoopGroup group;
	
	/**
	 * clientHandler, 会话session, IConnectController 
	 */
    protected TcpClientHandler clientHandler;
    
    public TcpClientStarter(IControllerDispatcher handler, int id, String nodeType, String ip, int port) {
		super(id, nodeType, ip, port);
		this.clientHandler = new TcpClientHandler(handler);
	}

	@Override
	public boolean connect() {
		Thread thread = new Thread(()->{
			try {
				//客户端连接服务端, 建立channel后会被阻塞,直到服务端断开连接.这里开一个线程去处理
				doConnect();
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Connect to server error, host:{}, port:{}", this.getIp(), this.getPort());
			}
		});
		thread.start();
		return true;
//		try {
//			return doConnect();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
	}

	@Override
	public void disConnect() {
		if (isRunning()) {
			this.clientHandler.getHandler().serverStatus(false);// 关掉连接
			if (group != null) {
				group.shutdownGracefully();
			}
			ISession session = this.clientHandler.getSession();
			if (session!=null) {
				session.disConnect();
			}
		}else {
			log.info("TCP网络服务未在运行状态");
		}
	}

	@Override
	public void tryConnect() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 实际连接操作
	 */
	private boolean doConnect() throws Exception {
		 group = new NioEventLoopGroup(1, new DefaultThreadFactory("TCP_CLIENT_BOSS"));
		 try {
			Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group) // 注册线程池
                    .channel(NioSocketChannel.class) // 使用NioSocketChannel来作为连接用的channel类
                    .remoteAddress(new InetSocketAddress(this.getIp(), this.getPort())) // 绑定连接端口和host信息
                    .handler(new ChannelInitializer<SocketChannel>() { // 绑定连接初始化器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
//                        	ch.pipeline().addLast(new TcpProtocolEncoder());
                            //ch.pipeline().addLast(new EchoClientHandler());
                        	ch.pipeline().addLast("idleDetection", new IdleDetectionHandler());
							// inbound
                        	ch.pipeline().addLast("lengthDecoder", new LengthFieldBasedFrameDecoder(8 * 1024, 0, 4, 0, 4));
//        							pipeline.addLast(new FixedLengthFrameDecoder(frameLength));
                        	ch.pipeline().addLast("clientHandler", clientHandler);
							// outbound
                        	ch.pipeline().addLast("lengthEncoder", new LengthFieldPrepender(4));
                        	ch.pipeline().addLast("protocolEncoder", new TcpProtocolEncoder());
                        }
                    });
            this.clientHandler.getHandler().serverStatus(true);
//            ChannelFuture cf = bootstrap.connect().sync(); // 异步连接服务器
//            // 成功连接到端口之后,给channel增加一个 管道关闭的监听器并同步阻塞,直到channel关闭,线程才会往下执行,结束线程
//            cf.channel().closeFuture().sync(); // 异步等待关闭连接channel
            bootstrap.connect(this.getIp(), this.getPort()).sync().channel();
            return true;
        } catch (Exception e) {
        	log.info("error", e);
        	return false;
		}finally {
           //group.shutdownGracefully().sync(); // 释放线程池资源
        }
	}
	
	@Override
	public boolean isActive() {
		if (isRunning()) {
			return false;
		}
		ISession session = this.clientHandler.getSession();
		if (session==null) {
			log.info("没有创建会话, 不能发送消息");
			return false;
		}
		return session.isConnect();
	}

	@Override
	public void disConnectNow() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void sendMessage(IProtocol protocol) {
		if (isRunning()) {
			return;
		}
		ISession session = this.clientHandler.getSession();
		if (session==null) {
			log.info("没有创建会话, 不能发送消息");
			return;
		}
		//session.send(message);
		session.send(Packet.encode(protocol));
	}
	
	@Override
	public void receiveResponse(IProtocol message) {
		
	}
	
}
