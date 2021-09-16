package com.cat.net.network.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Controller;

import com.cat.net.network.base.AbstractProtocol;

/**
 * Rpc请求注解, 表示监听请求信息, 用于rpc远程调用注解
 */
@Controller
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RpcRequest {
	
	/**
	 * 监听协议号????
	 * @return
	 */
	int value();
	
	/**
	 * 是否需要验证
	 * @return
	 */
	boolean isAuth() default true;
	
	/**
	 * 协议, 协议号匹配? 直接丢一个协议对象?
	 * @return
	 */
	Class<? extends AbstractProtocol> protocol() default AbstractProtocol.class;
	

}
