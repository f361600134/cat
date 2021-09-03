package com.cat.net.terminal;

public abstract class AbstractClient implements IClient{
	/**
	 * 连接服务节点的id
	 */
	protected int connectId;
	/**
	 * 节点类型<br>
	 * 这里做的有点复杂了,但是没有想到好的方法
	 */
	protected String nodeType;
	
	/** 连接服务器的IP */
	protected String ip;
	
	/**端口*/
	protected int port;
    
   	/** 运行状态 */
   	protected boolean runState;
	
	public AbstractClient() {}
	
	public AbstractClient(int connectId, String nodeType, String ip, int port) {
		this.connectId = connectId;
		this.nodeType = nodeType;
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
	public int getConnectId() {
		return connectId;
	}

	@Override
	public String getClientName() {
		return ip.concat(":").concat(String.valueOf(port));
	}

	@Override
	public String getNodeType() {
		return nodeType;
	}
	
	@Override
	public boolean isRunning() {
		return runState;
	}
	
}
