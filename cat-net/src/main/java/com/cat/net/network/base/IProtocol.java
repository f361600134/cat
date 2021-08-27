package com.cat.net.network.base;

import com.google.protobuf.AbstractMessageLite.Builder;

/**
 * 下发协议接口
 * 
 * @author Jeremy
 */
public interface IProtocol {
	
	int protocol();
	
	default byte[] toBytes() {
		return getBuilder().build().toByteArray();
	}
	
	Builder<?, ?> getBuilder();
	
	/**
     * 序号
     * 
     * @return
     */
    default int getSeq() {
    	return 0;
    }

    /**
     * 
     * @param seq
     */
    default void setSeq(int seq) {}
	
}
