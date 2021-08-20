package com.cat.net.network.controller;

import com.cat.net.network.base.ISession;

import io.netty.buffer.ByteBuf;

/**
 * IConnectController 系統接口
 */
public interface IConnectController {
	
	void onConnect(ISession session);

	void onReceive(ISession session, ByteBuf message);

	void onClose(ISession session);

	void onException(ISession session, Throwable e);
	
	void serverStatus(boolean running);
	
}
