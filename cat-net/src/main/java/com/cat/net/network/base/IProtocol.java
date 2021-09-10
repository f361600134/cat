package com.cat.net.network.base;

/**
 * 下发协议接口
 * 
 * @author Jeremy
 */
public interface IProtocol {
	
	/**
	 * 协议号
	 * @return
	 */
	int protocol();
	
	/**
	 * 数据流
	 * @return
	 */
	byte[] toBytes();
	
	/**
     * 序列号
     * @return
     */
    int getSeq();

    /**
     * 设置序列号
     * @param seq
     */
    void setSeq(int seq);
	
}
