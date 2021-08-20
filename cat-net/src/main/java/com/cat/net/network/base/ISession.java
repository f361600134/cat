package com.cat.net.network.base;

import java.net.InetSocketAddress;

import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramChannel;

/**
 * 会话信息
 */
public interface ISession {
	
	/**
     * 唯一sessionId
     * @return
     */
	int getSessionId();
    
    /**
     * 获取连接通道
     * @return
     */
    Channel getChannel();
    
    /**
     * 获取ip
     * @return
     */
    String getIp();
    
    /**
     * 获取端口
     * @return
     */
    int getPort();
    
    /**
     * 设置用户数据
     * @param obj
     */
    void setUserData(Object obj);
    
    /**
     * 获取玩家自定义数据
     * @param <T>
     * @return
     */
    public <T> T getUserData();
    
    /**
     * 是否是Tcp连接
     * @return
     */
	default public boolean isTcpSession() {
		Channel channel = getChannel();
		return  channel!= null && !(channel instanceof DatagramChannel);
	}
	/**
	 * 获取本地ip
	 * @return
	 */
	default public String getLocalIp() {
		Channel channel = getChannel();
		if (channel != null) {
			InetSocketAddress localAddr = (InetSocketAddress) channel.localAddress();
			return localAddr.getHostName();
		}
		return null;
	}
	/**
	 * 发送消息
	 * @param protocol
	 */
	default public void push(IProtocol protocol) {
		Packet data = Packet.encode(protocol);
		if (isConnect()) {
			getChannel().writeAndFlush(data);
		}
	}
	
	/**
	 * 发送消息
	 * @param protocol
	 */
	default public void send(Object message) {
		if (isConnect()) {
			getChannel().writeAndFlush(message);
		}
	}
	
	/**
	 * TCP是否处于连接中
	 * @return
	 */
	default public boolean isConnect() {
		Channel channel = getChannel();
		return channel != null && channel.isActive();
	}
	/**
	 * 断开连接
	 */
	default public void disConnect() {
		Channel channel = getChannel();
		if (channel != null) {
			channel.close();
		}
	}
	/**
	 * 释放连接
	 */
	default public void releaseBuf() {
		if (isConnect()) {
			getChannel().flush();
		}
	}
	
}
