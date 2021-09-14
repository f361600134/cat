package com.cat.net.network.tcp;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
     * 协议序号生成器<br>
     * ask操作才使用序号
     */
    protected final AtomicInteger seqGenerator = new AtomicInteger();
    /**
     * rpc回调缓存
     */
    protected final RpcCallbackCache callbackCache = new RpcCallbackCache();
    
	/**
	 * key: 节点类型
	 * value: key: 节点id, session会话
	 * session会话缓存<br>
	 */
	private Map<String, Map<Integer, ISession>> sessionMap = new ConcurrentHashMap<>();
	
	private static final long TIMEOUT = 300L;

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
		int seq = generateSeq();
		request.setSeq(seq);
		long now = System.currentTimeMillis();
		long expiredTime = now + TIMEOUT;
		RpcCallbackHandler<?> futureCallback = new RpcCallbackHandler<>(seq, expiredTime, callback);
		//回调方法加入缓存
		callbackCache.addCallback(futureCallback);
		//发送消息
		log.info("发送RPC请求, 节点类型: {}, 客户端:{}", nodeType, nodeId);
		sendMessage(nodeType, nodeId, request);
		//发送消息成功后, 去检测是否有需要清掉的回调函数
		callbackCache.checkExpired(now);
	}
	
	protected int generateSeq() {
        int seq = seqGenerator.incrementAndGet();
        if (seq >= 0) {
            return seq;
        }
        // 重置从1开始
        seqGenerator.compareAndSet(seq, 1);
        return seqGenerator.incrementAndGet();
    }
	
	/**
	 * 下发消息
	 * @param nodeType
	 * @param nodeIds
	 * @param request
	 */
	private void sendMessage(String nodeType, Collection<Integer> nodeIds, IProtocol request){
		nodeIds.forEach(nodeId->{
			sendMessage(nodeType, port, request);
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
	public RpcCallbackCache getCallbackCache() {
		return callbackCache;
	}

	@Override
	public RpcCallbackCache getRealCallbackCache() {
		callbackCache.checkExpired(System.currentTimeMillis());
		return callbackCache;
	}
	
	@Override
	public void onCreate(ISession session) {
		session.setUserData(this);
	}
}
