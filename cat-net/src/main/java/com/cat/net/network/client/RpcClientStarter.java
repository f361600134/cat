package com.cat.net.network.client;

import java.util.concurrent.atomic.AtomicInteger;

import com.cat.net.exception.RpcInvalidConnectException;
import com.cat.net.network.base.IProtocol;
import com.cat.net.network.controller.IControllerDispatcher;
import com.cat.net.network.rpc.IResponseCallback;
import com.cat.net.network.rpc.RpcCallbackCache;
import com.cat.net.network.rpc.RpcCallbackHandler;

/**
 * 
 * client 因为使用的netty的channel, 支持异步读写, 所以支持异步非阻塞读写.
 * 问题: 
 * 1. 怎么才算client对应的连接请求频繁? 应需要一个参考值来判断
 * 2. 
 * 
 * @author Jeremy
 */
public class RpcClientStarter extends TcpClientStarter {
	
	private final Record record = new Record();
	
	/**
     * 协议序号生成器<br>
     * ask操作才使用序号
     */
    protected final AtomicInteger seqGenerator = new AtomicInteger();
    /**
     * rpc回调缓存
     */
    protected final RpcCallbackCache callbackCache = new RpcCallbackCache();
    
    public RpcClientStarter(IControllerDispatcher handler, int id, String nodeType, String ip, int port) {
		super(id, nodeType, ip, port);
		this.clientHandler = new RpcClientHandler(handler, this);
	}
    
    public Record getRecord() {
    	return record;
    }
    
	protected int generateSeq() {
        int seq = seqGenerator.incrementAndGet();
        if (seq >= 0) {
            return seq;
        }
        // 重置从1开始
        seqGenerator.compareAndSet(seq, 1);
        return seqGenerator.incrementAndGet();
    }

	/**
	 * 请求调用
	 * @param <R>
	 */
	public void ask(IProtocol request, long timeout, IResponseCallback<?> callback) {
		if (callback == null) {
			sendMessage(request);
            return;
        }
		if (!isActive()) {
            callback.handleException(new RpcInvalidConnectException());
            return;
        }
		int seq = generateSeq();
		request.setSeq(seq);
		long now = System.currentTimeMillis();
		long expiredTime = now + timeout;
		RpcCallbackHandler<?> futureCallback = new RpcCallbackHandler<>(seq, expiredTime, callback);
		//回调方法加入缓存
		callbackCache.addCallback(futureCallback);
		//发送消息
		sendMessage(request);
		//发送消息成功后, 去检测是否有需要清掉的回调函数
		callbackCache.checkExpired(now);
	}
	
	public RpcCallbackCache getCallbackCache() {
		return callbackCache;
	}
	
	 /**
     * 记录
     * @author Jeremy
     */
    public class Record{
    	/**
    	 * 记录开始时间
    	 */
    	private long startTime;
    	/**
    	 * 调用次数
    	 */
    	private int invokeCnt;
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
    }
}
