package com.cat.net.network.base;

import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.Channel;

/**
 * 默认会话
 * @author Jeremy
 */
public class DefaultSession implements ISession{
	
	/** session id 生产者 */
	protected static AtomicInteger generator = new AtomicInteger(1);
	
	/** session id */
	protected int sessionId;
	/** socket通道 */
	protected Channel channel;
	/** 访问IP */
	protected String ip;
	/** 访问端口 */
	protected int port;
	/**
	 * 用户自定义数据
	 */
	protected Object userData;
	
	public DefaultSession(Channel ch) {
		this.channel = ch;
		String address = ch.remoteAddress().toString();
		String[] ipAndPort = address.split(":");
		this.ip = ipAndPort[0];
		this.port = Integer.parseInt(ipAndPort[1]);
	}

	@Override
	public int getSessionId() {
		return sessionId;
	}

	@Override
	public Channel getChannel() {
		return channel;
	}

	@Override
	public String getIp() {
		return ip;
	}

	@Override
	public int getPort() {
		return port;
	}
	
	@Override
	public void setUserData(Object userData) {
		this.userData = userData;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getUserData() {
		return (T) userData;
	}

	public static DefaultSession create(Channel ch) {
		DefaultSession session = new DefaultSession(ch);
		session.sessionId = Math.abs(generator.getAndIncrement());	// 初始化session id
		return session;
	}
}
