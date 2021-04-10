package com.cat.net.network.controller;

import com.cat.net.network.base.GameSession;

import io.netty.buffer.ByteBuf;

/**
 * IServerController 系統接口
 */
public interface IServerController {
	
	void onConnect(GameSession session);

	void onReceive(GameSession session, ByteBuf message);

	void onClose(GameSession session);

	void onException(GameSession session, Throwable e);
	
	void serverStatus(boolean running);
	
}
