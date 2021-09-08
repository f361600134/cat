package com.cat.net.terminal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cat.net.network.controller.IControllerDispatcher;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * @Description  服务socket连接启动抽象层
 */
public abstract class AbstractSocketServer extends AbstractServer {
	
	private final Logger log = LoggerFactory.getLogger(AbstractSocketServer.class);
	
	/**
	 * NioEventLoop并不是一个纯粹的I/O线程，它除了负责I/O的读写之外 创建了两个NioEventLoopGroup，
	 * 它们实际是两个独立的Reactor线程池。 一个用于接收客户端的TCP连接，
	 * 另一个用于处理I/O相关的读写操作，或者执行系统Task、定时任务Task等。
	 */
	protected EventLoopGroup bossGroup;
	protected EventLoopGroup workerGroup;
	/**
	 * 协议转发控制器
	 */
	protected IControllerDispatcher serverHandler;
	
	public AbstractSocketServer(String ip, int port, IControllerDispatcher serverHandler) {
		super(ip, port);
		this.bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("TCP_SERVER_BOSS"));
		int threadCount = Runtime.getRuntime().availableProcessors(); // CPU核数 * 2
		this.workerGroup = new NioEventLoopGroup(threadCount, new DefaultThreadFactory("TCP_SERVER_WORKER"));
		this.serverHandler = serverHandler;
	}
	
	@Override
	public boolean startServer() throws Exception {
		ChannelFuture future = null;
		try {
			ServerBootstrap bootstrap = getBootstrap();
			future = bootstrap.bind(port).sync();
			serverHandler.serverStatus(true);
			return true;
		} catch (Exception e) {
			log.info("", e);
			throw new RuntimeException("Netty "+getServerType()+"启动出现异常, 服务器关闭, 请检查");
		} finally {
			if (future != null && future.isSuccess()) {
				log.info("Netty [{}] server listening {} on port {} and ready for connections...", getServerType(), ip, port);
			} else {
				log.error("Netty [{}] server start up Error!", getServerType());
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
			serverHandler.serverStatus(false);// 关服
		}else {
			log.info("[{}]网络服务未在运行状态", getServerType());
		}
	}
	
	/**
	 * 获取启动器
	 * @return
	 */
	protected abstract ServerBootstrap getBootstrap();
	
	/**
	 * 获取服务类型
	 * @return
	 */
	protected abstract String getServerType();
	
}
