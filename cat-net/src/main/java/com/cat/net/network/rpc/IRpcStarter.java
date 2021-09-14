package com.cat.net.network.rpc;

import com.cat.net.network.base.IProtocol;

public interface IRpcStarter {
	
//	/**
//	 * 检查连接
//	 * 
//	 * @throws Exception
//	 */
//	public boolean isActive();
//
//	/**
//	 * 发送消息
//	 */
//	public void sendMessage(IProtocol message);
//	
//	/**
//	 * rpc请求
//	 * @param request
//	 * @param timeout
//	 * @param callback
//	 */
//	public void ask(IProtocol request, long timeout, IResponseCallback<?> callback);
	
	/**
	 * 获取rpc回调缓存
	 * @return
	 */
	public RpcCallbackCache getCallbackCache();
	
	/**
	 * 获取Rpc最新的回调缓存缓存
	 * @return
	 */
	public RpcCallbackCache getRealCallbackCache();

}
