package com.cat.net.network.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CMD, 用于游戏层协议消息号映射
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cmd {
	
	int value();
	
	boolean mustLogin() default true;
}
