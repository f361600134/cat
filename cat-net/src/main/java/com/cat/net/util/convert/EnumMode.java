package com.cat.net.util.convert;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
@Documented
public @interface EnumMode {

	/** 枚举类型的取值方式 */
	ValueMode value() default ValueMode.ORDINAL; 
	
}
