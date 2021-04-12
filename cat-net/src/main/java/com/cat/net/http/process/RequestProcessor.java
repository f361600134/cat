package com.cat.net.http.process;

import static com.cat.net.http.HttpConstant.SLASH;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.cat.net.http.annatation.RequestMapping;
import com.cat.net.http.base.RequestInfo;
import com.cat.net.http.base.Requester;
import com.cat.net.util.ClassUtils;
import com.cat.net.util.StringUtils;

//@Component
public class RequestProcessor implements InitializingBean{
	
	private static final Logger log = LoggerFactory.getLogger(RequestProcessor.class);
	
	@Autowired
	private ApplicationContext context;
	
	/**
	 * key：协议id
	 * value: 消息对象
	 */
	private Map<String, Requester> requesterMap;
	
	public RequestProcessor() {
		requesterMap = new HashMap<>();
	}

	/**
	 * 加载完成后, 初始化handler
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		//	所有加RequestMapping的类
		Map<String, Object> beanMaps = context.getBeansWithAnnotation(RequestMapping.class);
		if (beanMaps == null || beanMaps.isEmpty()) {
			return;
		}
		for (Object bean : beanMaps.values()) {
			Class<?> clazz = bean.getClass();
			RequestMapping mapping = clazz.getAnnotation(RequestMapping.class);
			if (mapping == null) return;
			String clazzName = clazzName(clazz, mapping);
			//	只有带有RequestMethod的才视为对外api
			Method[] methods = clazz.getDeclaredMethods();
			for (Method method : methods) {
				RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
				if (methodMapping == null) continue;
				String methodName = methodName(method, methodMapping);
				
				String requestKey = clazzName.concat(methodName);
				this.requesterMap.put(requestKey, Requester.create(bean, method));
			}
		}
	}
	
	/**
	 * 获取到映射地址,也是类名
	 * @param clazz
	 * @param mapping
	 * @return
	 */
	private String clazzName(Class<?> clazz, RequestMapping mapping) {
		//	获取映射url
		String clazzName = mapping.value();
		if (clazzName == null || clazzName.equals("")) {
			//	未指定映射, 使用类名作为映射
			clazzName = clazz.getSimpleName();
			clazzName = StringUtils.firstCharLower(clazzName);
		}
		clazzName = clazzName.startsWith(SLASH) ? clazzName : SLASH.concat(clazzName);
		return clazzName;
	}
	
	/**
	 * 获取映射地址, 也是方法名
	 * @param method 
	 * @param mapping
	 * @return
	 */
	public String methodName(Method method, RequestMapping mapping) {
		String methodName = mapping.value();
		if (methodName == null || methodName.equals("")) {
			methodName = method.getName();
		}
		methodName = methodName.startsWith(SLASH) ? methodName : SLASH.concat(methodName);
		return methodName;
	}
	
	/**
	 * 协议调用
	 * 
	 * @param RequestInfo	请求信息, 包含了url以及Map类型的参数
	 * @throws Exception 异常通过框架层处理, 返回对应错误码
	 */
	public String invoke(RequestInfo requestInfo) throws Exception {
		String url = requestInfo.getUrl();
		Requester requester = requesterMap.get(url);
		String ret = "";
		if (requester != null) {
			long begin = System.currentTimeMillis();
			Object obj = requester.invoke(requestInfo.getParamMap());
			ret = ClassUtils.isPrimitiveOrStr(obj) ? String.valueOf(obj) : JSON.toJSONString(obj);
			long used = System.currentTimeMillis() - begin;
			// 协议处理超过1秒
			if (used > 1000) {
				log.error("协议[{}]处理慢!!!耗时{}ms", url, used);
			}
		}
		return ret;
	}
	
	
	public Requester getRequester(String url){
		return requesterMap.get(url);
	}
	
}
