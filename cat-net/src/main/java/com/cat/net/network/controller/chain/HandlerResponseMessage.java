package com.cat.net.network.controller.chain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cat.net.core.executor.DisruptorStrategy;
import com.cat.net.exception.RepeatProtoException;
import com.cat.net.network.annotation.Rpc;
import com.cat.net.network.base.AbstractProtocol;
import com.cat.net.network.base.ISession;
import com.cat.net.network.base.Packet;
import com.cat.net.network.base.RemoteCaller;
import com.cat.net.network.controller.IRpcController;
import com.cat.net.network.rpc.IRpcStarter;
import com.cat.net.network.rpc.RpcCallbackCache;

public class HandlerResponseMessage extends AbstractHandlerWork<RemoteCaller>{
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	public void doHandler(ISession session, RemoteCaller caller, Packet packet, AbstractProtocol params) {
		//丢到session线程池去处理
		DisruptorStrategy.get(DisruptorStrategy.SINGLE).execute(session.getSessionId(), ()->{
			try {
				IRpcStarter rpcStarter = session.getUserData();
				RpcCallbackCache callbackCache = rpcStarter.getCallbackCache();
				callbackCache.receiveResponse(packet.seq(), packet.cmd(), params);
			} catch (Exception e) {
				log.error("DisruptorDispatchTask error", e);
			}
		});
	}
	

	@Override
	public boolean addController(IRpcController controller) {
		Rpc rpc =  controller.getClass().getAnnotation(Rpc.class);
		if (rpc == null) {
			return false;
		}
		if (rpc.listen() != Rpc.RESPONSE) {
			return false;
		}
		//检查重复协议号
		if (mapper.containsKey(rpc.value())) {
			throw new RepeatProtoException("发现回调重复协议号:"+rpc.value());
		}
		mapper.put(rpc.value(), RemoteCaller.create(rpc.isAuth(), controller));
		return true;
	}
	
}
