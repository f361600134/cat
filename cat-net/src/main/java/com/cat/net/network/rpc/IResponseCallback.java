package com.cat.net.network.rpc;

import com.cat.net.network.base.AbstractProtocol;
import com.cat.net.network.controller.IRpcController;

/**
 * rpc返回消息的回调
 * 
 * @author hdh
 *
 * @param <T>
 */
public interface IResponseCallback<T extends AbstractProtocol> extends IRpcController{

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

}
