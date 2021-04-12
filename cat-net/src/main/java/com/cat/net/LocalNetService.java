package com.cat.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
//@Component
public class LocalNetService {
	
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
		String ip = config.getServerIp();
		if (config.getTcpPort() > 0) {
			tcpServer = new TcpServerStarter(serverHandler, ip, config.getTcpPort());
			tcpServer.startServer();
		}
		if (config.getWebscoketPort() > 0){
			websocketServer = new WebSocketServerStarter(serverHandler, ip, config.getWebscoketPort());
			websocketServer.startServer();
		}
		if (config.getHttpPort() > 0) {
			httpServer = new HttpServerStarter(requestHandler, ip, config.getHttpPort());
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
}
