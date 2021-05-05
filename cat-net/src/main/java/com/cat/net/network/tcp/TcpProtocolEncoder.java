package com.cat.net.network.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cat.net.network.base.Packet;
import com.cat.net.network.protocol.IDefaultProtocolEncoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ReferenceCountUtil;

/**
 * 游戏协议编码器
 */
public class TcpProtocolEncoder extends MessageToByteEncoder<Packet> implements IDefaultProtocolEncoder {

	private static final Logger log = LoggerFactory.getLogger(TcpProtocolEncoder.class);
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
		if (msg!=null) {
			codec(msg, out);
		}else {
			ReferenceCountUtil.retain(msg);
		}	
		//log.info("TcpProtocolEncoder out:{}", out);
	}

}
