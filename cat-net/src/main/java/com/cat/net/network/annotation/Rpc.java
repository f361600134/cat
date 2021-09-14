package com.cat.net.network.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Controller;

import com.cat.net.network.base.AbstractProtocol;

/**
 * Rpc, 用于rpc远程调用注解
 */
@Controller
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Rpc {
	
	/** RPC请求*/
	public int REQUEST = 1;
	/** RPC响应 */
	public int RESPONSE = 2;
	
	/**
	 * 监听协议号????
	 * @return
	 */
	int value();
	
	/**
	 * Rpc类型
	 * @return
	 */
	int type() default REQUEST;
	
	/**
	 * 是否需要验证身份
	 * @return
	 */
	boolean isAuth() default true;
	
	/**
	 * 协议, 协议号匹配? 直接丢一个协议对象?
	 * @return
	 */
	Class<? extends AbstractProtocol> protocol() default AbstractProtocol.class;
	

}
