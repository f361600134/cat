package com.cat.net.network.base;

import io.netty.buffer.ByteBuf;

/**
 * 数据包
 * 在业务层, 可以选择指定的数据包设定
 */
public class Packet {
	
	/** 协议长度 */
	public static final int PROTO_LEN = 4;
	
	private final int cmd;
	private final byte[] data;

	public Packet(int cmd, byte[] data) {
		this.cmd = cmd;
		this.data = data;
	}
	
	public int cmd() {
		return cmd;
	}
	
	public byte[] data() {
		return data;
	}
	
	public static Packet decode(ByteBuf byteBuf) {
		short cmd = byteBuf.readShort();
		byte[] newdata = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(newdata);
		return new Packet(cmd, newdata);
	}
	
	public static Packet encode(IProtocol protocol) {
		int cmd = protocol.protocol();
		byte[] data = protocol.toBytes();
		return new Packet(cmd, data);
	}
	
}	
