package com.cat.net.terminal;

import com.cat.net.network.base.IProtocol;

/**
 * 客户端接口, 用于客户端
 * 
 * @author Jeremy
 */
public interface IClient {

	/**
	 * 获取连接的服务节点id
	 * @return
	 */
	public int getConnectId();
	
	/**
	 * 获取节点类型
	 * @return
	 */
	public String getNodeType();

	/**
	 * 获取客户端名字
	 * 
	 * @return
	 */
	public String getClientName();

	/**
	 * 启动客户端连接
	 * 
	 * @return
	 * @throws Exception
	 */
	public void connect() throws Exception;

	/**
	 * 友好的断开连接,不可以接收请求,把当前请求完成后,断开连接.
	 */
	public void disConnect() ;

	/**
	 * 尝试重连
	 * 
	 * @throws Exception
	 */
	public void tryConnect() throws Exception;

	/**
	 * 检查连接
	 * 
	 * @throws Exception
	 */
	public boolean isActive() throws Exception;

	/**
	 * 客户端是否运行
	 * @return
	 */
	public boolean isRunning();
	
	/**
	 * 接收响应消息
	 */
	public void receiveResponse(IProtocol message);
	
	/**
	 * 发送消息
	 */
	public void sendMessage(IProtocol message);
	

}
