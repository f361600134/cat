package com.cat.net.network.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Controller;

/**
 * Rpc, 用于rpc远程调用注解
 */
@Controller
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Rpc {
	
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
	

}
