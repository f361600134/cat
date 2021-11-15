package com.cat.net.network.client;

import org.apache.commons.lang3.tuple.Pair;

import com.cat.net.exception.RpcInvalidConnectException;
import com.cat.net.network.base.AbstractProtocol;
import com.cat.net.network.base.IProtocol;
import com.cat.net.network.base.ISession;
import com.cat.net.network.controller.IControllerDispatcher;
import com.cat.net.network.rpc.IResponseCallback;
import com.cat.net.network.rpc.IRpcAuthenticationListenable;
import com.cat.net.network.rpc.IRpcStarter;
import com.cat.net.network.rpc.Record;
import com.cat.net.network.rpc.RpcCallbackCache;
import com.cat.net.network.rpc.RpcCallbackHandler;

/**
 * client 因为使用的netty的channel, 支持异步读写, 所以支持异步非阻塞读写.
 * 问题: 
 * 1. 怎么才算client对应的连接请求频繁? 应需要一个参考值来判断, 可以使用堆积的回调函数来判断,回调函数多, 表示压力大.无回调函数, 表示没有堆积任务
 * 2. callbackCache内缓存的会定时清除,假如1s达到10个请求,1分钟600个请求, 1小时能达到36000个请求. 所以判断只要缓存内堆积的回调大于10, 则表示压力大, 新开辟一个连接
 * 反之, 只要回调内的请求, 都小于3, 则表示压力小, 清除最后的那个连接 
 * 3. 使用堆积的回调函数, 可以实现需求, 但是不够精确
 * 
 * @author Jeremy
 */
public class RpcClientStarter extends TcpClientStarter implements IRpcStarter{
	
	/**
	 * 默认过期时间
	 */
	private final static long EXPIRETIME = 100L;
	
	private final Record record = new Record();
	
    /**
     * rpc回调缓存
     */
    protected final RpcCallbackCache callbackCache = new RpcCallbackCache();
    
    /**
     * rpc身份验证监听
     */
    private IRpcAuthenticationListenable listenable;
    
    public RpcClientStarter(IControllerDispatcher handler, int nodeId, int connectId, String nodeType, String ip, int port) {
		super(nodeId, connectId, nodeType, ip, port, handler);
	}
    
    public RpcClientStarter(IControllerDispatcher handler, IRpcAuthenticationListenable listenable,
    		int nodeId, int connectId, String nodeType, String ip, int port) {
		super(nodeId, connectId, nodeType, ip, port, handler);
		this.listenable = listenable;
	}
    
    public Record getRecord() {
    	return record;
    }
    
	/**
	 * 请求调用, 有回调
	 * @param <R>
	 */
	public void ask(IProtocol request, long timeout, IResponseCallback<?> callback) {
		if (callback == null) {
			sendMessage(request);
            return;
        }
		if (!isActive()) {
            callback.handleException(new RpcInvalidConnectException());
            return;
        }
		doAsk(request, timeout, callback);
	}
	
	/**
	 * 请求调用, 有回调
	 * @param <R>
	 */
	public void ask(IProtocol request, IResponseCallback<?> callback) {
		if (callback == null) {
			sendMessage(request);
            return;
        }
		if (!isActive()) {
            callback.handleException(new RpcInvalidConnectException());
            return;
        }
		doAsk(request, EXPIRETIME, callback);
	}
	
	/**
	 * 具体调用
	 * @param request
	 * @param timeout
	 * @param callback
	 */
	private void doAsk(IProtocol request, long timeout, IResponseCallback<?> callback) {
		long now = System.currentTimeMillis();
		long expiredTime = now + timeout;
		RpcCallbackHandler<?> futureCallback = new RpcCallbackHandler<>(expiredTime, callback);
		//回调方法加入缓存
		callbackCache.addCallback(futureCallback);
		final int seq = futureCallback.getSeq();
		//设置序列号
		request.setSeq(seq);
		//发送消息
		log.info("发送RPC请求, 节点类型: {}, 客户端:{}", getNodeType(), getConnectId());
		sendMessage(request);
		//发送消息成功后, 去检测是否有需要清掉的回调函数
		callbackCache.checkExpired(now);
	}
	
	/**
	 * 获取所有的回调缓存
	 * @return
	 */
	public RpcCallbackCache getCallbackCache() {
		return callbackCache;
	}
	
	/**
	 * 获得当前真是的回调缓存
	 * @return
	 */
	public RpcCallbackCache getRealCallbackCache() {
		callbackCache.checkExpired(System.currentTimeMillis());
		return callbackCache;
	}
	
	/**
	 * 当连接成功, 创建session<br>
	 * rpc客户端执行一次身份认证
	 */
	@Override
	public void onCreate(ISession session) {
		super.onCreate(session);
		if (listenable != null) {
			Pair<IProtocol, IResponseCallback<? extends AbstractProtocol>> pair = listenable.authentication();
			IProtocol req = pair.getLeft();
			IResponseCallback<? extends AbstractProtocol> resp = pair.getRight();
			this.ask(req, resp);
		}
	}
	
}
