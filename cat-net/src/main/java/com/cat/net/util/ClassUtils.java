package com.cat.net.util;

public class ClassUtils {
	
	/**
	 * 判断类型是否是基本类型以及String类型
	 * @date 2019年9月3日下午1:17:19
	 */
	public static boolean isPrimitiveOrStr(Object obj) {
		try {
			//先判断引用类型, 再判断基础类型!!!不能变
			return  (obj instanceof String) || ((Class<?>)obj.getClass().getField("TYPE").get(null)).isPrimitive();
		} catch (Exception e) {
			return false;
		}
	}
	
}
