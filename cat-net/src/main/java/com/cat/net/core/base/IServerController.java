package com.cat.net.core.base;

import io.netty.buffer.ByteBuf;

/**
 * IServerController
 */
public interface IServerController {
	
	void onConnect(GameSession session);

	void onReceive(GameSession session, ByteBuf message);

	void onClose(GameSession session);

	void onException(GameSession session, Throwable e);
	
	void serverStatus(boolean running);
	
}
