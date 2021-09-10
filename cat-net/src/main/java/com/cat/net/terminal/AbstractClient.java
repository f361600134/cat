package com.cat.net.terminal;


public abstract class AbstractClient implements IClient{
	
	/**
	 * 当前节点id
	 */
	protected int nodeId;
	/**
	 * 连接服务节点的id
	 */
	protected int connectId;
	/**
	 * 当前节点类型<br>
	 */
	protected String nodeType;
	
	/** 连接服务器的IP */
	protected String ip;
	
	/**端口*/
	protected int port;
    
   	/** 运行状态 */
   	protected boolean runState;
	
	public AbstractClient() {}
	
	public AbstractClient(int nodeId, int connectId, String nodeType, String ip, int port) {
		this.nodeId = nodeId;
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
	
	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
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
