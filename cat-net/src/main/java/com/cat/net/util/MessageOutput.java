package com.cat.net.util;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.AbstractMessageLite;

public class MessageOutput {
	
	private AbstractMessageLite<?, ?> messageLite;
	
	public static MessageOutput create(AbstractMessageLite<?, ?> messageLite) {
		MessageOutput output = new MessageOutput();
		output.messageLite = messageLite;
		return output;
	}
	
	public static String toString(AbstractMessageLite<?, ?> messageLite) {
		return JSON.toJSONString(messageLite, ProtobufPropertyFilter.INST);
	}
	
	@Override
	public String toString() {
		return toString(messageLite);
	}
}
