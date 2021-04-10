package com.cat.net.util.convert;

/**
 * 枚举取值方式
 * @author meiyc
 *
 */
public enum ValueMode {

	/** 根据enum的ordinal()值来获取枚举 */
	ORDINAL,
	
	/** 根据enum的name()值获取枚举 */
	NAME,
	
	/** 根据enum的构造函数的参数值获取枚举具体实现参见 {@link EnumUtils#getEnumByValue()}
	 *  如果有多个字段则取第一个字段值
	 */
	VALUE;
	
}
