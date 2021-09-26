package com.cat.net.network.controller.chain;

import java.lang.reflect.Method;

import com.cat.net.core.executor.DisruptorStrategy;
import com.cat.net.exception.RepeatProtoException;
import com.cat.net.network.annotation.Rpc;
import com.cat.net.network.base.AbstractProtocol;
import com.cat.net.network.base.ISession;
import com.cat.net.network.base.Packet;
import com.cat.net.network.base.RemoteServerCaller;
import com.cat.net.network.controller.IRpcController;

public class HandlerRequestMessage extends AbstractHandlerWork<RemoteServerCaller>{
	
	@Override
	public void doHandler(ISession session, RemoteServerCaller caller, Packet packet, AbstractProtocol params) {
		DisruptorStrategy.get(DisruptorStrategy.SINGLE).execute(session.getSessionId(), ()->{
			try {
				caller.getInvoker().invoke(session, params, packet.seq());
			} catch (Exception e) {
				log.error("DisruptorDispatchTask error", e);
			}
		});
	}

	@Override
	public boolean addController(IRpcController controller) {
		Method[] methods = controller.getClass().getDeclaredMethods();
		boolean bool = false;
		for (Method method : methods) {
			Rpc cmd = method.getAnnotation(Rpc.class);
			if (cmd == null) {
				continue;
			}
			if (cmd.listen() != Rpc.REQUEST) {
				continue;
			}
			//检查重复协议号
			if (mapper.containsKey(cmd.value())) {
				throw new RepeatProtoException("发现监听重复协议号:"+cmd.value());
			}
			bool = true;
			mapper.put(cmd.value(),RemoteServerCaller.create(controller, cmd.isAuth(), method));
		}
		return bool;
	}
	
	public int size() {
		return mapper.size();
	}
	

}
