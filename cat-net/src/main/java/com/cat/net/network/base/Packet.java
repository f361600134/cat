package com.cat.net.network.base;

import io.netty.buffer.ByteBuf;

/**
 * 数据包
 * 在业务层, 可以选择指定的数据包设定
 */
public class Packet {
	
	/** 协议长度 */
	public static final int PROTO_LEN = 4;
	
	/**
	 * 消息号
	 */
	private final int cmd;
	/**
	 * 序列号
	 */
	private final int seq;
	/**
	 * 数据
	 */
	private final byte[] data;

	public Packet(int cmd, int seq, byte[] data) {
		this.cmd = cmd;
		this.seq = seq;
		this.data = data;
	}
	
	public int cmd() {
		return cmd;
	}
	
	public int seq() {
		return seq;
	}
	
	public byte[] data() {
		return data;
	}
	
	public static Packet decode(ByteBuf byteBuf) {
		int cmd = byteBuf.readInt();
		int seq = byteBuf.readInt();
		byte[] newdata = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(newdata);
		return new Packet(cmd, seq, newdata);
	}
	
	public static Packet encode(IProtocol protocol) {
		int cmd = protocol.protocol();
		int seq = protocol.getSeq();
		byte[] data = protocol.toBytes();
		return new Packet(cmd, seq, data);
	}
	
}	
