package com.cat.net.http.controller;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * IRequestController HTTP系統接口
 */
public interface IRequestController {
	
	void onConnect(FullHttpResponse response);

	void onReceive(FullHttpRequest httpRequest, FullHttpResponse response) throws Exception;

	void onClose(FullHttpResponse response);

	void onException(FullHttpResponse response, Throwable e);
	
	void serverStatus(boolean running);
	
}
