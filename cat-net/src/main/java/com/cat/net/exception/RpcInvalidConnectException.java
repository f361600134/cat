package com.cat.net.exception;

/**
 * rpc链接失效
 * 
 * @author Jeremy
 *
 */
public class RpcInvalidConnectException extends RpcException {
    /**
     * 
     */
    private static final long serialVersionUID = -4238489289360297371L;

    public RpcInvalidConnectException() {
    }

    public RpcInvalidConnectException(String msg) {
        super(msg);
    }

    public RpcInvalidConnectException(Throwable cause) {
        super(cause);
    }

    public RpcInvalidConnectException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
