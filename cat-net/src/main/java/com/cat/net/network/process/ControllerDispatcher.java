package com.cat.net.network.process;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.cat.net.network.annotation.Cmd;
import com.cat.net.network.base.Commander;
import com.cat.net.network.base.GameSession;
import com.cat.net.network.base.Packet;
import com.cat.net.network.controller.IController;
import com.cat.net.util.MessageOutput;
import com.google.protobuf.GeneratedMessageLite;

//@Component
public class ControllerDispatcher implements InitializingBean{
	
	private static final Logger log = LoggerFactory.getLogger(ControllerDispatcher.class);
	
	@Autowired
	private List<IController> handlerList;
	
	/**
	 * key：协议id
	 * value: 消息对象
	 */
	private Map<Integer, Commander> commanderMap;
	
	/**
	 * 加载完成后, 初始化handler
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		long startTime = System.currentTimeMillis();
		commanderMap = new HashMap<Integer, Commander>();
		for (IController controller : handlerList) {
			Method[] methods = controller.getClass().getDeclaredMethods();
			for (Method method : methods) {
				Cmd cmd = method.getAnnotation(Cmd.class);
				if (cmd == null) {
					continue;
				}
				//检查重复协议号
				if (commanderMap.containsKey(cmd.value())) {
					//log.error("协议号[{}]重复, 请检查!!!", cmd.id());
					throw new RuntimeException("发现重复协议号:"+cmd.value());
				}
				commanderMap.put(cmd.value(), Commander.create(controller, cmd.mustLogin(), method));
			}
		}
		log.info("The initialization message[{}] is complete and takes [{}] milliseconds.", commanderMap.size(),(System.currentTimeMillis() - startTime));
	}
	
	/**
	 * 协议调用
	 * 
	 * @param session	玩家会话信息
	 * @param packet	包体
	 */
	public void invoke(GameSession session, Packet packet) throws Exception {
		int cmd = packet.cmd();
		Commander commander = commanderMap.get(cmd);
		if (commander != null) {
			long begin = System.currentTimeMillis();

			byte[] bytes = packet.data();
			
			Method parser = commander.getProtobufParser();
			GeneratedMessageLite<?, ?> params = (GeneratedMessageLite<?, ?>) parser.invoke(null, (Object) bytes);
			
			log.debug("收到协议[{}], pid={}, params={}, size={}B",
					cmd, session.getPlayerId(), MessageOutput.create(params), bytes.length);

			//commander.getMethod().invoke(commander.getController(), session, params);
			commander.getInvoker().invoke(session, params);

			long used = System.currentTimeMillis() - begin;
			// 协议处理超过1秒
			if (used > 1000) {
				log.error("协议[{}]处理慢!!!耗时{}ms", cmd, used);
			}

		}
	}
	
	public Commander getCommander(int cmd) {
		return commanderMap.get(cmd);
	}

}
