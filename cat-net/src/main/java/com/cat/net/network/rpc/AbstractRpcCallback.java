package com.cat.net.network.rpc;

import com.cat.net.network.base.AbstractProtocol;

/**
 * 抽象rpc回调方法
 * @author hdh
 *
 * @param <T>
 */
public abstract class AbstractRpcCallback<T extends AbstractProtocol> implements IRpcCallback<T> {

	/**
	 * 序列号
	 */
    protected int seq;

    /**
     * 过期时间
     */
    protected long expiredTime;

    @Override
    public int getSeq() {
        return seq;
    }

    @Override
    public boolean isTimeout(long now) {
        return now > expiredTime;
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(long expiredTime) {
        this.expiredTime = expiredTime;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

}
