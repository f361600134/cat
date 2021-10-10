package com.cat.net.http.base;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cat.net.core.reflect.MethodInvoker;
import com.cat.net.http.HttpConstant;
import com.cat.net.http.annatation.Param;
import com.cat.net.util.ConvertUtils;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * Http对外接口封装, restful风格, 封装所有方法以及参数信息
 * @author Jeremy
 */
public class Requester {
	
	public static Logger logger = LoggerFactory.getLogger(Requester.class);

	private MethodInvoker invoker;
	private List<MethodParam> methodParams;

	/**
	 * @param object 对象名
	 * @param method 方法名
	 */
	public Requester(Object object, Method method) {
		this.invoker = MethodInvoker.create(object, method);
		this.methodParams = new ArrayList<>();
		initParam(method);
	}

	/**
	 * 初始化形参信息
	 * @param method
	 */
	private void initParam(Method method) {
		// 获取到所有形参
		Parameter[] params = method.getParameters();
		// 获取到注解
		for (Parameter parameter : params) {
			// 默认使用参数名字
			String paramName = parameter.getName();
			Param param = parameter.getAnnotation(Param.class);
			if (param != null) {
				// 如果有注解名, 以注解名为准
				paramName = param.value();
			}
			this.methodParams.add(MethodParam.create(paramName, parameter.getType()));
		}
	}
	
	/**
	 * 反射调用, 支持三种格式的参数
	 * 1. 普通类型, 
	 * 2. 引用类型
	 * 3. 复杂对象
	 * @param paramMap
	 * @return
	 */
	public Object invoke(Map<String, Object> paramMap) {
		final int size = methodParams.size();
		Object[] args = new Object[size];
		for (int i = 0; i < size; i++) {
			MethodParam param = methodParams.get(i);
			Class<?> paramType = param.getParamType();
			if (paramType.isAssignableFrom(FullHttpRequest.class)) {
				args[i] = paramMap.get(HttpConstant.HTTPREQUEST);
			}
			else if (paramType.isAssignableFrom(FullHttpResponse.class)) {
				args[i] = paramMap.get(HttpConstant.HTTPRESPONSE);
			}
			else if (paramType.isAssignableFrom(Map.class)) {
				//	若是map类型, 直接把所有参数丢进去
				args[i] = paramMap;
			}
			else {
				args[i] = ConvertUtils.convert(paramMap.get(param.getName()), paramType);
			}
//			else if (paramType.isAssignableFrom(List.class)) {
//				//	若是list类型, 直接把所有参数丢进去
//				args[i] = JSONObject.parseObject((String)(paramMap.get(param.getName())), List.class);
//			}
//			else if (TypeUtils.isPrimitiveOrWrapperOrString(paramType)) {
//				//	若是基础类型, 直接获取值丢进去
//				Object obj = paramMap.get(param.getName());
//				args[i] = ConvertUtils.convert(obj, param.getParamType());
//			}
//			else {
//				//	否则视为对象类型, 反射生成一个对象
//				JSONObject jsonObject = new JSONObject(paramMap);
//				args[i] = jsonObject.toJavaObject(param.getParamType());
//			}
		}
		return invoker.invoke(args);
	}

	public static Requester create(Object object, Method method) {
		return new Requester(object, method);
	}
	
}
