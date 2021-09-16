package com.cat.net.network.tcp;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cat.net.network.base.IProtocol;
import com.cat.net.network.base.ISession;
import com.cat.net.network.controller.IControllerDispatcher;
import com.cat.net.network.rpc.IResponseCallback;
import com.cat.net.network.rpc.IRpcStarter;
import com.cat.net.network.rpc.RpcCallbackCache;
import com.cat.net.network.rpc.RpcCallbackHandler;

/**
 * TCP服务启动器
 * 
 * @author Jeremy
 * @date 2020年7月9日
 */
public class RpcServerStarter extends TcpServerStarter implements IRpcStarter{
	
	/**
	 * 服务端默认回调缓存超时时间
	 */
	private static final long TIMEOUT = 300L;
	
	/**
	 * 回调缓存
	 */
	protected final RpcCallbackCache callbackCache = new RpcCallbackCache();
    
	/**
	 * key: 节点类型<br>
	 * value: key: 节点id, session会话<br>
	 * session会话缓存<br>
	 * 如何清除掉session缓存? FIXME
	 * 1. cache定时清除.
	 * 2. 显示清除,重新封装ISession, nodeType, nodeId, session. 当需要清除时, 循环找到对应session清掉
	 * 2. 
	 */
	private Map<String, Map<Integer, ISession>> sessionMap = new ConcurrentHashMap<>();
	
	public RpcServerStarter(IControllerDispatcher serverHandler, String ip, int port) {
		super(serverHandler, ip, port);
	}

	@Override
	protected String getServerType() {
		return "Rpc";
	}

	public void addSession(String nodeType, int nodeId, ISession session) {
		if (session == null) {
			log.debug("session is null, nodeType:{}, nodeId:{}", nodeType, nodeId);
		}
		Map<Integer, ISession> map = sessionMap.get(nodeType);
		if (map == null) {
			map = new ConcurrentHashMap<Integer, ISession>();
			sessionMap.put(nodeType, map);
		}
		map.put(nodeId, session);
	}
	
	/**
	 * 获取指定节点类型下的节点
	 * @param nodeType
	 * @param nodeId
	 * @return
	 */
	public Collection<ISession> getSession(String nodeType) {
		Map<Integer, ISession> map = sessionMap.getOrDefault(nodeType, Collections.emptyMap());
		return map.values();
	}
	
	/**
	 * 获取指定节点类型下的节点
	 * @param nodeType
	 * @param nodeId
	 * @return
	 */
	public ISession getSession(String nodeType, int nodeId) {
		Map<Integer, ISession> map = sessionMap.getOrDefault(nodeType, Collections.emptyMap());
		return map.get(nodeId);
	}
	
	/**
	 * 服务端请求rpc调用
	 */
	public void ask(String nodeType, IProtocol request, IResponseCallback<?> callback) {
		Map<Integer, ISession> map = sessionMap.getOrDefault(nodeType, Collections.emptyMap());
		Collection<Integer> nodeIds = map.keySet();
		if (callback == null) {
			sendMessage(nodeType, nodeIds, request);
            return;
        }
		nodeIds.forEach(nodeId->{
			ask(nodeType, nodeId, request, callback);
		});
	}
	
	/**
	 * 服务端请求rpc调用
	 */
	public void ask(String nodeType, List<Integer> nodeIds, IProtocol request, IResponseCallback<?> callback) {
		if (callback == null) {
			sendMessage(nodeType, nodeIds, request);
            return;
        }
		nodeIds.forEach(nodeId->{
			ask(nodeType, nodeId, request, callback);
		});
	}
	
	/**
	 * 发送rpc到指定节点
	 * @param nodeType
	 * @param nodeId
	 * @param request
	 * @param callback
	 */
	public void ask(String nodeType, int nodeId, IProtocol request, IResponseCallback<?> callback) {
		long now = System.currentTimeMillis();
		long expiredTime = now + TIMEOUT;
		RpcCallbackHandler<?> futureCallback = new RpcCallbackHandler<>(expiredTime, callback);
		//回调方法加入缓存时,生成序列号
		callbackCache.addCallback(futureCallback);
		//设置序列号
		request.setSeq(futureCallback.getSeq());
		//发送消息
		log.info("发送RPC请求, 节点类型: {}, 客户端:{}", nodeType, nodeId);
		sendMessage(nodeType, nodeId, request);
		//发送消息成功后, 去检测是否有需要清掉的回调函数
		callbackCache.checkExpired(now);
	}
	
	/**
	 * 下发消息
	 * @param nodeType
	 * @param nodeIds
	 * @param request
	 */
	private void sendMessage(String nodeType, Collection<Integer> nodeIds, IProtocol request){
		nodeIds.forEach(nodeId->{
			sendMessage(nodeType, nodeId, request);
		});
	}
	
	/**
	 * 下发消息
	 * @param nodeType
	 * @param nodeIds
	 * @param request
	 */
	private void sendMessage(String nodeType, int nodeId, IProtocol request){
		ISession session = getSession(nodeType, nodeId);
		session.push(request);
	}

	@Override
	public void onCreate(ISession session) {
		session.setUserData(this);
	}

	@Override
	public RpcCallbackCache getCallbackCache() {
		return callbackCache;
	}

}
