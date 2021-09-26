package com.cat.net.network.controller.chain;

import com.cat.net.network.base.ISession;
import com.cat.net.network.base.Packet;
import com.cat.net.network.controller.IRpcController;

public interface HandlerWork {

	/**
	 * 处理handler, 一个节点处理成功, 就不继续往下处理了
	 * @param caller
	 * @return
	 */
	public boolean handler(ISession session, Packet packet);
	
	/**
	 * 添加controller, 一个几点处理成功, 不继续往下添加了
	 * @param controllers
	 */
	public boolean addController(IRpcController controller);
	
	/**
	 * controller数量
	 * @return
	 */
	public int size();
	
}
