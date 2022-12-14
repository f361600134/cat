package com.cat.net.network.websocket;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cat.net.network.base.Packet;
import com.cat.net.network.protocol.IDefaultProtocolEncoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.ReferenceCountUtil;

public class WebsocketProtocolEncoder extends MessageToMessageEncoder<Packet> implements IDefaultProtocolEncoder {

	private static final Logger log = LoggerFactory.getLogger(WebsocketProtocolEncoder.class);

	@Override
	protected void encode(ChannelHandlerContext ctx, Packet msg, List<Object> out) throws Exception {
		if (msg != null) {
			ByteBuf byteBuf = ctx.alloc().ioBuffer();
			codec(msg, byteBuf);
			if (byteBuf != null && byteBuf.isReadable()) {
				BinaryWebSocketFrame binaryWebSocketFrame = new BinaryWebSocketFrame(byteBuf);
				out.add(binaryWebSocketFrame);
			} else {
				// 释放空间
				byteBuf.release();
				log.info("WebsocketProtocolEncoder error, byteBuf is null or not use to be Readable");
			}
		} else {
			ReferenceCountUtil.retain(msg);
			out.add(msg);
		}
	}

}
