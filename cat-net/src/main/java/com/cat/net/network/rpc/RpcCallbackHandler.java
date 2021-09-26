package com.cat.net.network.rpc;

import com.cat.net.network.base.AbstractProtocol;

/**
 * 
 * @author hdh
 * @param <T>
 */
public class RpcCallbackHandler<T extends AbstractProtocol> extends AbstractRpcCallback<T> {

    private IResponseCallback<T> callback;

    private volatile boolean complete;

    public RpcCallbackHandler() {
    }

    public RpcCallbackHandler(long expiredTime, IResponseCallback<T> callback) {
        this.expiredTime = expiredTime;
        this.callback = callback;
    }

    @Override
    public synchronized void receiveResponse(T response) {
        if (complete) {
            return;
        }
        complete = true;
        callback.receiveResponse(response);
    }

    @Override
    public synchronized void handleException(Exception ex) {
        if (complete) {
            return;
        }
        complete = true;
        callback.handleException(ex);
    }

    public IResponseCallback<T> getCallback() {
        return callback;
    }

    public void setCallback(IResponseCallback<T> callback) {
        this.callback = callback;
    }

}
