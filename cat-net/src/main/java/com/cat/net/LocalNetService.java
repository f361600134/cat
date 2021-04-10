package com.cat.net;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cat.net.common.NetConfig;
import com.cat.net.http.HttpServerStarter;
import com.cat.net.http.controller.IRequestController;
import com.cat.net.network.controller.IServerController;
import com.cat.net.network.tcp.TcpServerStarter;
import com.cat.net.network.websocket.WebSocketServerStarter;
import com.cat.net.server.IServer;

/**
 * 本地网络服务
 */
@Component
public class LocalNetService implements InitializingBean{
	
	private static final Logger log = LoggerFactory.getLogger(LocalNetService.class);
	
	@Autowired private NetConfig config;
	
	@Autowired private IServerController serverHandler;
	@Autowired private IRequestController requestHandler;
	
	private IServer tcpServer;
	private IServer websocketServer;
	private IServer httpServer;
	
	public LocalNetService() {
	}
	
	/**
	 * 启动
	 * @date 2020年7月9日
	 * @throws Exception
	 */
	public void startup() throws Exception {
		if (tcpServer != null) {
			tcpServer.startServer();
		}
		if (websocketServer != null) {
			websocketServer.startServer();
		}
		if (httpServer != null) {
			httpServer.startServer();
		}
	}
	
	/**
	 * 关闭
	 * @date 2020年7月9日
	 * @throws Exception
	 */
	public void shutdown() throws Exception {
		if (tcpServer != null) {
			tcpServer.stopServer();
		}
		if (websocketServer != null) {
			websocketServer.stopServer();
		}
		if (httpServer != null) {
			httpServer.stopServer();
		}
	}
	
	@PreDestroy
	public void preDestroy() {
		try {
			shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (config.isTcpEnable()) {
			tcpServer = new TcpServerStarter(serverHandler, config.getServerIp(), config.getTcpPort());
		}
		if (config.isWebscoketEnable()){
			websocketServer = new WebSocketServerStarter(serverHandler, config.getServerIp(), config.getWebscoketPort());
		}
		if (config.isHttpEnable()) {
			httpServer = new HttpServerStarter(requestHandler, config.getServerIp(), config.getHttpPort());
		}
		startup();
	}

}
