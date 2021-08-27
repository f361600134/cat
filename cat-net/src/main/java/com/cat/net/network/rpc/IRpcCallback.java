package com.cat.net.network.rpc;

import com.cat.net.network.base.IProtocol;

/**
 * rpc回调
 * @author Jeremy
 * @param <T> 返回的消息
 */
public interface IRpcCallback<T extends IProtocol> {
    /**
     * 消息序号
     * 
     * @return
     */
    int getSeq();

    /**
     * 接受到回调消息
     * 
     * @param response
     */
    void receiveResponse(T response);

    /**
     * 处理错误
     * 
     * @param ex
     */
    void handleException(Exception ex);

    /**
     * 是否超时
     * 
     * @param now
     * @return
     */
    boolean isTimeout(long now);

}
