package com.cat.net.http.base;

/**
 * 参数信息, 对外API接口的形参详细信息.
 * @author Jeremy
 */
public class MethodParam {
	
	/**
	 * 形参名
	 */
	private String name;
	
	/**
	 * 形参类型
	 */
	private Class<?> paramType;
	
//	/**
//	 * 形参类型
//	 */
//	private Type type;
	
	private MethodParam(String name, Class<?> paramType) {
		this.name = name;
		this.paramType = paramType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getParamType() {
		return paramType;
	}

	public void setParamType(Class<?> paramType) {
		this.paramType = paramType;
	}
	
	public static MethodParam create(String name, Class<?> paramType) {
		return new MethodParam(name, paramType);
	}

	@Override
	public String toString() {
		return "MethodParam [name=" + name + ", paramType=" + paramType + "]";
	}


}
