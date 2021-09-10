package com.cat.net.network.rpc;

/**
 * 记录
 * 
 * @author Jeremy
 */
public class Record {
	/**
	 * 记录开始时间
	 */
	private long startTime;
	/**
	 * 调用次数
	 */
	private int invokeCnt;
	/**
	 * 回调成功次数<br>
	 * 回调成功后次数+1, 只用于有回调的rpc请求, 若以后要实现无回调的rpc请求, 新增成员变量另计.
	 */
	private int successCnt;
	/**
	 * 最后调用时间
	 */
	private long lastTime;

	public Record() {
		this.startTime = System.currentTimeMillis();
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public int getInvokeCnt() {
		return invokeCnt;
	}

	public void setInvokeCnt(int invokeCnt) {
		this.invokeCnt = invokeCnt;
	}

	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public int getSuccessCnt() {
		return successCnt;
	}

	public void setSuccessCnt(int successCnt) {
		this.successCnt = successCnt;
	}
}