package com.cat.net.terminal;

/**
 * @Description  服务抽象层
 */
public abstract class AbstractServer implements IServer {
	
	/** 服务器IP */
	protected String ip;
	/** 服务器端口 */
	protected int port;
	/** 运行状态 */
	protected boolean runState;
	
	@Override
	public boolean isRunning() {
		return runState;
	}
	
	protected void running() {
		this.runState = true;
	}
	
	@Override
	public String getServerId() {
		return ip.concat(":").concat(String.valueOf(port));
	}
	
	protected AbstractServer(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	protected AbstractServer(int port) {
		this.ip = "0.0.0.0";
		this.port = port;
	}
	
}
