package com.cat.net.network.client;

/**
 * 客户端状态
 * @author Jeremy
 */
public interface IClientState {
	
	/**
	 * 未使用状态
	 */
	int STATE_NOT_IN_USE = 0;
	/**
	 * 正使用状态
	 */
	int STATE_IN_USE = 1;
	/**
	 * 移除状态
	 */
	int STATE_REMOVED = -1;
	/**
	 * 预约的, 保留的?
	 */
	int STATE_RESERVED = -2;

	boolean compareAndSet(int expectState, int newState);

	void setState(int newState);

	int getState();
}
