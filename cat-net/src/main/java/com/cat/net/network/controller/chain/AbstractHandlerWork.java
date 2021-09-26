package com.cat.net.network.controller.chain;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cat.net.network.base.AbstractProtocol;
import com.cat.net.network.base.ISession;
import com.cat.net.network.base.Packet;
import com.cat.net.network.base.RemoteCaller;
import com.cat.net.util.SerializationUtil;

public abstract class AbstractHandlerWork<T extends RemoteCaller> implements HandlerWork{
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	protected Map<Integer, T> mapper = new HashMap<>();
	
	public boolean checkInvoke(ISession session, T commander) {
		//不需要登录
		if (!commander.isMustLogin()) {
			return true;
		}
		//需要登录, 且已经登录设置值, 则返回true
		if (session.getUserData() != null) {
			return true;
		}
		return false;
	}

	@Override
	public boolean handler(ISession session, Packet packet) {
		T caller = mapper.get(packet.cmd());
		if(caller == null){
			log.info("监听协议验证为null, 拒绝处理, cmd:[{}]", packet.cmd());
			return false;
		}
		if (!checkInvoke(session, caller)) {
			log.info("监听协议验证不通过, 拒绝处理, cmd:[{}]", packet.cmd());
			return false;
		}
		Class<?> clazz = caller.getParamType();
		AbstractProtocol params = (AbstractProtocol) SerializationUtil.deserialize(packet.data(), clazz);
		
		doHandler(session, caller, packet, params);
		return true;
	}
	
	public int size() {
		return mapper.size();
	}
	
	public abstract void doHandler(ISession session, T caller, Packet packet, AbstractProtocol params);
	
}
