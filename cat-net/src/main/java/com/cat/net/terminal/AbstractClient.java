package com.cat.net.terminal;

public abstract class AbstractClient implements IClient{
	
	/** 连接服务器的IP */
	protected String ip;
	
	/**端口*/
    private int port;
    
	/** 运行状态 */
	protected boolean runState;
	
	public AbstractClient() {}
	
	public AbstractClient(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setRunState(boolean runState) {
		this.runState = runState;
	}

	@Override
	public String getClientId() {
		return ip.concat(":").concat(String.valueOf(port));
	}

	@Override
	public boolean isRunning() {
		return runState;
	}
	
	
}
