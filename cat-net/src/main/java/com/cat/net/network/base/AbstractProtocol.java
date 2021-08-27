package com.cat.net.network.base;

public abstract class AbstractProtocol implements IProtocol{

	 /**
     * 消息序号<br>
     * 客户端的序号自增<br>
     * 服务端返回的序号与请求序号相同<br>
     * 服务端主动发送的协议 序号为0<br>
     * 只在最外层的编译时进行读写
     */
    protected int seq;

	@Override
	public int getSeq() {
		return seq;
	}

	@Override
	public void setSeq(int seq) {
		this.seq = seq;
	}

}
