package com.cat.net.network.rpc;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cat.net.exception.RpcShutdownException;
import com.cat.net.exception.RpcTimeoutException;
import com.cat.net.network.base.AbstractProtocol;

/**
 * rpc回调缓存
 * @author hdh
 * @param <T>
 */
public class RpcCallbackCache {

    private final static Logger logger = LoggerFactory.getLogger(RpcCallbackCache.class);
    
	/*** 协议序号生成器*/
    protected final AtomicInteger seqGenerator = new AtomicInteger();
    
    /*** cas锁,用于删除过期的回调函数*/
    private final AtomicBoolean expiredLock = new AtomicBoolean();

    /*** 回调缓存*/
    private final ConcurrentMap<Integer, IRpcCallback<? extends AbstractProtocol>> callbackMap = new ConcurrentHashMap<>();

    public void addCallback(IRpcCallback<? extends AbstractProtocol> callback) {
    	final int seq = generateSeq();
    	callback.setSeq(seq);
        callbackMap.put(callback.getSeq(), callback);
    }
    
    public int generateSeq() {
        int seq = seqGenerator.incrementAndGet();
        if (seq >= 0) {
            return seq;
        }
        // 重置从1开始
        seqGenerator.compareAndSet(seq, 1);
        return seqGenerator.incrementAndGet();
    }

    /**
     * 收到响应消息
     * @param seq
     * @param protoId
     * @param response
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void receiveResponse(int seq, int protoId, AbstractProtocol response) {
        IRpcCallback callback = callbackMap.remove(seq);
        if (callback == null) {
            logger.warn("receive response[{}],but callback is expired.", protoId);
            return;
        }
        //FIXME, 如果收到回调要不要检测是否已超时!?
        try {
            callback.receiveResponse(response);
        } catch (Exception e) {
            logger.error("callback receiveResponse[" + protoId + "] error.", e);
        }
    }

    /**
     * 处理异常
     * @param seq
     * @param ex
     */
    public void handleException(int seq, Exception ex) {
        IRpcCallback<?> callback = callbackMap.remove(seq);
        if (callback == null) {
            return;
        }
        try {
            callback.handleException(ex);
        } catch (Exception e) {
            logger.error("callback handleException error.", e);
        }

    }

    /**
     * 检查并清理过期回调
     * @param now
     */
    public void checkExpired(long now) {
    	//Do nothing if the callbackMap is empty or expiredLock is in use.
        if (callbackMap.isEmpty() || expiredLock.get()) {
        	return;
        }
        if (expiredLock.compareAndSet(false, true)) {
            try {
              Iterator<Entry<Integer, IRpcCallback<? extends AbstractProtocol>>> iterator = callbackMap.entrySet().iterator();
               while (iterator.hasNext()) {
                   Entry<Integer, IRpcCallback<? extends AbstractProtocol>> entry = iterator.next();
                   IRpcCallback<? extends AbstractProtocol> callback = entry.getValue();
                   if (!callback.isTimeout(now)) {
                       continue;
                   }
                   callback.handleException(new RpcTimeoutException());
                   iterator.remove();
               }
            } catch (Exception e) {
            	logger.error("callback checkExpired error.", e);
			}finally {
            	expiredLock.set(false);
            }
        }
    }

    public void shutdown() {
        if (callbackMap.isEmpty()) {
            return;
        }
        Iterator<Entry<Integer, IRpcCallback<? extends AbstractProtocol>>> iterator = callbackMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Integer, IRpcCallback<? extends AbstractProtocol>> entry = iterator.next();
            IRpcCallback<? extends AbstractProtocol> callback = entry.getValue();
            callback.handleException(new RpcShutdownException());
            iterator.remove();
        }

    }

    public IRpcCallback<? extends AbstractProtocol> getCallback(int seq) {
        return callbackMap.get(seq);
    }

    public ConcurrentMap<Integer, IRpcCallback<? extends AbstractProtocol>> getCallbackMap() {
        return callbackMap;
    }

}
