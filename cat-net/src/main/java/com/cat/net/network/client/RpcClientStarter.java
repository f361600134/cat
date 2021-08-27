package com.cat.net.network.client;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cat.net.exception.RpcInvalidConnectException;
import com.cat.net.network.base.IProtocol;
import com.cat.net.network.controller.IConnectController;
import com.cat.net.network.rpc.IResponseCallback;
import com.cat.net.network.rpc.RpcCallbackCache;
import com.cat.net.network.rpc.RpcCallbackHandler;

/**
 * @author Jeremy
 */
public class RpcClientStarter extends TcpClientStarter{
	
	private static final Logger log = LoggerFactory.getLogger(TcpClientHandler.class);
	
	/**
     * 协议序号生成器<br>
     * ask操作才使用序号
     */
    protected final AtomicInteger seqGenerator = new AtomicInteger();
    /**
     * rpc回调缓存
     */
    protected final RpcCallbackCache callbackCache = new RpcCallbackCache();
	
    public RpcClientStarter(IConnectController handler, int id, String nodeType, String ip, int port) {
		super(handler, id, nodeType, ip, port);
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
	public <R extends IProtocol> void ask(IProtocol request, long timeout, IResponseCallback<R> callback) {
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
		RpcCallbackHandler<R> futureCallback = new RpcCallbackHandler<>(seq, expiredTime, callback);
		//回调方法加入缓存
		callbackCache.addCallback(futureCallback);
		//发送消息
		sendMessage(request);
	}
	
}
