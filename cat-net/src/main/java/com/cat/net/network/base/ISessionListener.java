package com.cat.net.network.base;

/**
 * session监听接口
 * @author Jeremy
 */
public interface ISessionListener {

	/**
	 * 建立连接成功, 创建session
	 * @param session
	 */
	default void onCreate(ISession session) {}
	
	/**
	 * 当移除session
	 * @param session
	 */
	default void onRemove(ISession session) {}
	
}
