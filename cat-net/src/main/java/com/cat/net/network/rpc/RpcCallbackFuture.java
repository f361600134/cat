package com.cat.net.network.rpc;

import java.util.concurrent.CompletableFuture;

import com.cat.net.network.base.IProtocol;

/**
 * rpc调用返回
 * @author Jeremy
 * @param <T> 返回消息
 */
public class RpcCallbackFuture<T extends IProtocol> extends AbstractRpcCallback<T> {

    private CompletableFuture<T> future;

    public RpcCallbackFuture() {
    }

    public RpcCallbackFuture(int seq, long expiredTime, CompletableFuture<T> future) {
        this.seq = seq;
        this.expiredTime = expiredTime;
        this.future = future;
    }

    public CompletableFuture<T> getFuture() {
        return future;
    }

    public void setFuture(CompletableFuture<T> future) {
        this.future = future;
    }

    @Override
    public void receiveResponse(T response) {
        future.complete(response);
    }

    @Override
    public void handleException(Exception ex) {
        future.completeExceptionally(ex);
    }

}
