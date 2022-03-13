package com.cat.net.network.controller;

import com.cat.net.network.base.ISession;

/**
 * 业务逻辑处理器入口
 */
public interface IController {
	
	/**
	 * 用于发消息前的验证
	 * @param session
	 * @return boolean 为true则表示可以发消息, 否则跳过发消息
	 */
	default boolean verify(ISession session) {
		return true;
	}
	
	/**
	 * 用于消息处理后的处理
	 * @param session
	 * @param beginTime 协议处理开始时间
	 */
	default void afterProcess(ISession session, long beginTime) {
	}

}
