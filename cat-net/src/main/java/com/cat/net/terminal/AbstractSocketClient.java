package com.cat.net.terminal;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cat.net.network.base.IProtocol;
import com.cat.net.network.base.ISession;
import com.cat.net.network.base.Packet;
import com.cat.net.network.client.IClientState;
import com.cat.net.network.client.TcpClientHandler;
import com.cat.net.network.controller.IControllerDispatcher;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * @Description  服务socket连接启动抽象层
 */
public abstract class AbstractSocketClient extends AbstractClient implements IClientState{
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * NioEventLoop并不是一个纯粹的I/O线程，它除了负责I/O的读写之外 创建了两个NioEventLoopGroup，
	 * 它们实际是两个独立的Reactor线程池。 一个用于接收客户端的TCP连接，
	 * 另一个用于处理I/O相关的读写操作，或者执行系统Task、定时任务Task等。
	 */
	protected EventLoopGroup group;
	/**
	 * clientHandler, 会话session, IConnectController 
	 */
    protected TcpClientHandler clientHandler;
	
	/**
	 * 连接状态控制
	 */
	protected AtomicInteger state = new AtomicInteger(STATE_NOT_CONNECT);
	
	public AbstractSocketClient(int connectId, String nodeType, String ip, int port, IControllerDispatcher handler) {
		super(connectId, nodeType, ip, port);
		this.clientHandler = new TcpClientHandler(handler);
		group = new NioEventLoopGroup(1, new DefaultThreadFactory("TCP_CLIENT_BOSS"));
	}
	
	@Override
	public void connect() {
		Thread thread = new Thread(()->{
			try {
				//客户端连接服务端, 建立channel后会被阻塞,直到服务端断开连接.这里开一个线程去处理
				doConnect();
			} catch (Exception e) {
				e.printStackTrace();
				log.error("connectAsyn to server error, host:{}, port:{}", this.getIp(), this.getPort());
			}
		});
		thread.start();
	}
	
	@Override
	public void tryConnect() throws Exception {
		// TODO Auto-generated method stub
	}
	
	protected void doConnect() throws Exception{
		try {
			Bootstrap bootstrap = getBootstrap();
			
			//FIXME 这里的状态太多了, 统一用一个状态控制
			this.clientHandler.getHandler().serverStatus(true);
            state.set(STATE_CONNECTED);
            setRunState(true);
            
            log.info("connect to server successful, host:{}, port:{}", this.getIp(), this.getPort());
            
            // 线程同步阻塞等连接到指定地址
            ChannelFuture future = bootstrap.connect().sync();
            // 成功连接到端口之后,给channel增加一个 管道关闭的监听器并同步阻塞,直到channel关闭,线程才会往下执行,结束线程
            future.channel().closeFuture().sync();
		} catch (Exception e) {
			log.info("Netty [{}] Clinet 启动出现异常, 服务器关闭, 请检查", getServerType());
			log.error("", e);
		} finally {
			//FIXME 如果服务器断掉了, 客户端直接设置为保留状态? 不尝试连接了吗
			state.set(STATE_RESERVED);
            group.shutdownGracefully().sync(); // 释放线程池资源
            log.info("服务器主动断开连接, 客户端状态设置为保留");
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
	public void disConnect() {
		state.set(STATE_RESERVED);
		if (isRunning()) {
			this.clientHandler.getHandler().serverStatus(false);// 关掉连接
			if (group != null) {
				group.shutdownGracefully();
			}
			ISession session = this.clientHandler.getSession();
			if (session!=null) {
				session.disConnect();
			}
			log.info("TCP网络服务连接断开");
		}else {
			log.info("TCP网络服务未在运行状态");
		}
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
	
	@Override
	public boolean compareAndSet(int expectState, int newState) {
		return state.compareAndSet(expectState, newState);
	}

	@Override
	public void setState(int newState) {
		state.set(newState);
	}

	@Override
	public int getState() {
		return state.get();
	}
	
	/**
	 * 获取启动器
	 * @return
	 */
	protected abstract Bootstrap getBootstrap();
	
	/**
	 * 获取服务类型
	 * @return
	 */
	protected abstract String getServerType();
	
}
