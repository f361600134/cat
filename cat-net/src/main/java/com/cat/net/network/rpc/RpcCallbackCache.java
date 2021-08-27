package com.cat.net.network.rpc;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cat.net.exception.RpcShutdownException;
import com.cat.net.exception.RpcTimeoutException;
import com.cat.net.network.base.IProtocol;

/**
 * rpc回调缓存
 * 
 * @author hdh
 *
 * @param <T>
 */
public class RpcCallbackCache {

    private final static Logger logger = LoggerFactory.getLogger(RpcCallbackCache.class);
    /**
     * 回调过期检查间隔<br>
     * 毫秒
     */
    public final static long CALLBACK_EXPIRED_CHECK_INTERVAL = 300;

    private final ConcurrentMap<Integer, IRpcCallback<? extends IProtocol>> callbackMap = new ConcurrentHashMap<>();

    public void addCallback(IRpcCallback<? extends IProtocol> callback) {
        callbackMap.put(callback.getSeq(), callback);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void receiveResponse(int seq, IProtocol response) {
        IRpcCallback callback = callbackMap.remove(seq);
        if (callback == null) {
            logger.warn("receive response[{}],but callback is expired.", response.protocol());
            return;
        }
        try {
            callback.receiveResponse(response);
        } catch (Exception e) {
            logger.error("callback receiveResponse[" + response.protocol() + "] error.", e);
        }
    }

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
     * 
     * @param now
     */
    public void checkExpired(long now) {
        if (callbackMap.isEmpty()) {
            return;
        }
        Iterator<Entry<Integer, IRpcCallback<? extends IProtocol>>> iterator = callbackMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Integer, IRpcCallback<? extends IProtocol>> entry = iterator.next();
            IRpcCallback<? extends IProtocol> callback = entry.getValue();
            if (!callback.isTimeout(now)) {
                continue;
            }
            try {
                callback.handleException(new RpcTimeoutException());
            } catch (Exception e) {
                logger.error("callback handle timeout error.", e);
            }
            iterator.remove();
        }
    }

    public void shutdown() {
        if (callbackMap.isEmpty()) {
            return;
        }
        Iterator<Entry<Integer, IRpcCallback<? extends IProtocol>>> iterator = callbackMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Integer, IRpcCallback<? extends IProtocol>> entry = iterator.next();
            IRpcCallback<? extends IProtocol> callback = entry.getValue();
            try {
                callback.handleException(new RpcShutdownException());
            } catch (Exception e) {
                logger.error("callback handle shutdown error.", e);
            }
            iterator.remove();
        }

    }

    public IRpcCallback<?> getCallback(int seq) {
        return callbackMap.get(seq);
    }

    public ConcurrentMap<Integer, IRpcCallback<? extends IProtocol>> getCallbackMap() {
        return callbackMap;
    }

}
