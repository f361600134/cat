package com.cat.net.network.controller;

import java.lang.reflect.Method;
import java.util.List;

import com.cat.net.exception.RepeatProtoException;
import com.cat.net.network.annotation.Rpc;
import com.cat.net.network.base.ISession;
import com.cat.net.network.base.Packet;
import com.cat.net.network.base.RemoteCaller;
import com.cat.net.network.client.RpcClientStarter;
import com.cat.net.network.rpc.IResponseCallback;
import com.cat.net.network.rpc.RpcCallbackCache;
import com.cat.net.util.MessageOutput;
import com.google.protobuf.AbstractMessageLite;

/**
 * 远程调用控制类
 * @author Jeremy
 */
public class DefaultRemoteCallControllerDispatcher extends AbstractControllerDispatcher<RemoteCaller> {
	
	/**
	 * 初始化
	 * @param controllers 消息处理接口列表
	 * @throws Exception 异常
	 */
	public void initialize(List<IResponseCallback<? extends AbstractMessageLite<?, ?>>> callbacks) throws Exception {
		long startTime = System.currentTimeMillis();
		for (IResponseCallback<?> callback : callbacks) {
			Rpc rpc =  callback.getClass().getAnnotation(Rpc.class);
			if (rpc == null) {
				continue;
			}
			//检查重复协议号
			if (mapper.containsKey(rpc.value())) {
				throw new RepeatProtoException("发现重复协议号:"+rpc.value());
			}
			mapper.put(rpc.value(), RemoteCaller.create(callback, rpc.isAuth()));
		}
		log.info("The initialization [rpc] message[{}] is complete and takes [{}] milliseconds.", mapper.size(),(System.currentTimeMillis() - startTime));
	}

	/**
	 * invoke之前的判断
	 */
	@Override
	public boolean checkInvoke(ISession session, RemoteCaller commander) {
		//不需要验证, 则默认返回成功
		if (!commander.isAuth()) {
			return true;
		}
		//需要验证, 设置了userData表示验证成功
		if (session.getUserData() != null) {
			return true;
		}
		return false;
	}

	@Override
	public void invoke(ISession session, RemoteCaller commander, Packet packet) throws Exception {
		long begin = System.currentTimeMillis();
		final int seq = packet.seq();
		if (seq <= 0) {
			log.error("不合法的rpc序列号:[{}]", seq);
			return;
		}
		final int cmd = packet.cmd();
		if (cmd <= 0) {
			log.error("不合法的rpc协议号:[{}]", seq);
			return;
		}
		final byte[] bytes = packet.data();
		Method parser = commander.getProtobufParser();
		AbstractMessageLite<?, ?> params = (AbstractMessageLite<?, ?>) parser.invoke(null, (Object) bytes);

		// 处理转发请求
		RpcClientStarter client = session.getUserData();
		RpcCallbackCache callbackCache = client.getCallbackCache();
		callbackCache.receiveResponse(seq, cmd, params);

		log.debug("收到RPC协议[{}], pid={}, params={}, size={}B", cmd, session.getUserData(), MessageOutput.create(params),
				bytes.length);
		long used = System.currentTimeMillis() - begin;
		// 协议处理超过1秒
		if (used > 1000) {
			log.error("协议[{}]处理慢!!!耗时{}ms", cmd, used);
		}
	}

}