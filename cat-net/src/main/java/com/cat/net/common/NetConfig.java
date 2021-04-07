package com.cat.net.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

//import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 网络配置
 * @author Jeremy
 * @date 2020年6月29日
 *
 */
@Configuration
public class NetConfig {
	
	//服务器ip
	@Value("${cat.network.connection.serverIp}")
	private String serverIp;
	//tcp端口
	@Value("${cat.network.connection.tcpPort}")
	private int tcpPort;
	//tcp服务启用
	@Value("${cat.network.connection.tcpEnable}")
	private boolean tcpEnable;
	//websocket端口
	@Value("${cat.network.connection.webscoketPort}")
	private int webscoketPort;
	//websocket服务启用
	@Value("${cat.network.connection.webscoketEnable}")
	private boolean webscoketEnable;
	
	public String getServerIp() {
		return serverIp;
	}
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	public int getTcpPort() {
		return tcpPort;
	}
	public void setTcpPort(int tcpPort) {
		this.tcpPort = tcpPort;
	}
	public int getWebscoketPort() {
		return webscoketPort;
	}
	public void setWebscoketPort(int webscoketPort) {
		this.webscoketPort = webscoketPort;
	}
	public boolean isTcpEnable() {
		return tcpEnable;
	}
	public void setTcpEnable(boolean tcpEnable) {
		this.tcpEnable = tcpEnable;
	}
	public boolean isWebscoketEnable() {
		return webscoketEnable;
	}
	public void setWebscoketEnable(boolean webscoketEnable) {
		this.webscoketEnable = webscoketEnable;
	}
	
}
