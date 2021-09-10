package com.cat.net.network.base;

/**
 * session监听接口
 * @author Jeremy
 */
public interface ISessionListener {

	/**
	 * 当创建session
	 * @param session
	 */
	default void onCreate(ISession session) {}
	
	/**
	 * 当移除session
	 * @param session
	 */
	default void onRemove(ISession session) {}
	
}
