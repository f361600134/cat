package com.cat.orm.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段加了这个注解, 表示默认通过反序列化的方式注入到对象内<br>
 * 注意: final 修饰的对象, 默认不能被反序列化, 防止引用, 数据被替换掉
 * 接口类的对象,需要指定反序列化对象类型
 * @author Jeremy
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
	
	/**Column name*/
	String value();
	
	/**Column 反序列化类型, 提供的反序列化类型必须有空构造函数*/
	Class<?> clazzType() default Object.class;
	
}
