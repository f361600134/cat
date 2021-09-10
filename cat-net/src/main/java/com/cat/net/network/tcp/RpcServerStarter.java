package com.cat.net.network.tcp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cat.net.network.base.ISession;
import com.cat.net.network.base.ISessionListener;
import com.cat.net.network.controller.IControllerDispatcher;

/**
 * TCP服务启动器
 * 
 * @author Jeremy
 * @date 2020年7月9日
 */
public class RpcServerStarter extends TcpServerStarter implements ISessionListener{
	
	/**
	 * session会话缓存<br>
	 */
	private Map<String, Map<Integer, ISession>> sessionMap = new ConcurrentHashMap<>();

	public RpcServerStarter(IControllerDispatcher serverHandler, String ip, int port) {
		super(serverHandler, ip, port);
	}

	@Override
	protected String getServerType() {
		return "Rpc";
	}

}
