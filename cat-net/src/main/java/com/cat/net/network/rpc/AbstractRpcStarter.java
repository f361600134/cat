package com.cat.net.network.rpc;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cat.net.exception.RpcInvalidConnectException;
import com.cat.net.network.base.IProtocol;

public abstract class AbstractRpcStarter implements IRpcStarter{
	
	protected final Logger log = LoggerFactory.getLogger(getClass());

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
		//log.info("发送RPC请求, 节点类型: {}, 客户端:{}", getNodeType(), getConnectId());
		sendMessage(request);
		//发送消息成功后, 去检测是否有需要清掉的回调函数
		callbackCache.checkExpired(now);
	}
	
	/**
	 * 获取所有的回调缓存
	 * @return
	 */
	public RpcCallbackCache getCallbackCache() {
		return callbackCache;
	}
	
	/**
	 * 获得当前真是的回调缓存
	 * @return
	 */
	public RpcCallbackCache getRealCallbackCache() {
		callbackCache.checkExpired(System.currentTimeMillis());
		return callbackCache;
	}
   
	
}
