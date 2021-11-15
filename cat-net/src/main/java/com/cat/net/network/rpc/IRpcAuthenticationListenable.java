package com.cat.net.network.rpc;

import org.apache.commons.lang3.tuple.Pair;

import com.cat.net.network.base.AbstractProtocol;
import com.cat.net.network.base.IProtocol;

/**
 * 身份认证的监听器<br>
 * 用于rpc客户端连接成功的监听项, rpc连接成功后, 根据注入的请求与回调消息, 进行一次身份验证请求<br>
 * 目前的处理方式不太友好 FIXME 未来想到合适的方法后, 再修改此处
 * @author Jeremy
 */
@FunctionalInterface
public interface IRpcAuthenticationListenable {
	
	 /**
	  * 用于身份认证监听, 当rpc客户端连接服务器成功后, 回调执行此方法.用于身份验证
	  */
    public Pair<IProtocol, IResponseCallback<? extends AbstractProtocol>> authentication();

}
