package com.cat.net.exception;

/**
 * rpc调用超时
 * 
 * @author hdh
 *
 */
public class RpcTimeoutException extends RpcException {

    /**
     * 
     */
    private static final long serialVersionUID = 5636010777813073642L;

    public RpcTimeoutException() {
    }

    public RpcTimeoutException(String msg) {
        super(msg);
    }

    public RpcTimeoutException(Throwable cause) {
        super(cause);
    }

    public RpcTimeoutException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
