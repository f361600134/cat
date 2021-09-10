package com.cat.net.network.authenticate;

import io.netty.util.AttributeKey;

/**
 * 身份
 * @author Jeremy
 */
public interface Identity {

    AttributeKey<Identity> ATTR_KEY = AttributeKey.valueOf(Identity.class.getSimpleName());

    /**
     * 连接的身份类型
     * 
     * @return
     */
    String getNodeType();

    /**
     * 身份名<br>
     * 该身份的唯一标识
     * @return
     */
    int getNodeId();

}
