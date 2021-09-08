package com.cat.net.network.client;

/**
 * 客户端状态
 * @author Jeremy
 */
public interface IClientState {
	
	/**
	 * 已初始化,未连接的<br>
	 */
	int STATE_NOT_CONNECT = 0;
	
	/**
	 * 已连接状态<br>
	 * 连接成功后, 设置为此状态
	 */
	int STATE_CONNECTED = 1;
	
	/**
	 * 保留状态<br>
	 * 当一段时间请求量达不到最低要求, 且当前连接超过核心连接数, 则切换为保留状态<br>
	 * 当状态为保留状态是, 不可以处理新的请求, 消费完所有的任务后, 进入移除状态<br>
	 */
	int STATE_RESERVED = 2;
	
	/**
	 * 移除状态<br>
	 * 设置为移除状态后, 等到处理线程处理时, 销毁此连接<br>
	 */
	int STATE_REMOVED = 3;
	
	boolean compareAndSet(int expectState, int newState);

	void setState(int newState);

	int getState();
}
