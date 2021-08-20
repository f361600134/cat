package com.cat.net.core.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cat.net.network.base.ISession;
import com.cat.net.network.base.Packet;
import com.cat.net.network.process.ControllerDispatcher;

public class DisruptorDispatchTask implements Runnable {
	
	private static final Logger log = LoggerFactory.getLogger(DisruptorDispatchTask.class);
	
	private ControllerDispatcher processor;
	private ISession session;
	private Packet packet;

	public DisruptorDispatchTask(ControllerDispatcher processor, ISession session, Packet packet) {
		this.processor = processor;
		this.session = session;
		this.packet = packet;
	}

	@Override
	public void run() {
		try {
			processor.invoke(session, packet);
			log.info("====> DisruptorDispatchTask run, threadName:{}", Thread.currentThread().getName());
		} catch (Exception e) {
			log.error("DisruptorDispatchTask error", e);
		}
	}

}