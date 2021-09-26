package com.cat.net.network.controller.chain;

import java.util.ArrayList;
import java.util.List;

import com.cat.net.network.base.ISession;
import com.cat.net.network.base.Packet;
import com.cat.net.network.controller.IRpcController;

public class HandlerChain implements HandlerWork {
	
	private List<HandlerWork> workList = new ArrayList<>();
	
	public List<HandlerWork> getWorkList() {
		return workList;
	}
	
	public void addWork(HandlerWork work) {
		this.workList.add(work);
	}

	@Override
	public boolean handler(ISession session, Packet packet) {
		for (HandlerWork handlerWork : workList) {
			boolean bool = handlerWork.handler(session, packet);
			if (bool) {
				return bool;
			}
		}
		return false;
	}
	
	@Override
	public boolean addController(IRpcController controller) {
		for (HandlerWork handlerWork : workList) {
			boolean bool = handlerWork.addController(controller);
			if (bool) {
				return bool;
			}
		}
		return false;
	}
	@Override
	public int size() {
		int size = 0;
		for (HandlerWork handlerWork : workList) {
			size += handlerWork.size();
		}
		return size;
	}
	
}
