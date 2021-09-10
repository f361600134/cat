package com.cat.net.http.base;

import java.util.Map;

/**
 * request 請求信息, 可以在下个版本, 移除掉RequestInfo, 没必要搞一个对象
 * @author Jeremy
 */
public class RequestInfo {

	private String url;
	private Map<String, Object> paramMap;
	
	private RequestInfo(String url, Map<String, Object> paramMap) {
		super();
		this.url = url;
		this.paramMap = paramMap;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	public Map<String, Object> getParamMap() {
		return paramMap;
	}
	public void setParamMap(Map<String, Object> paramMap) {
		this.paramMap = paramMap;
	}
	
	public static RequestInfo create(String url, Map<String, Object> paramMap) {
		return new RequestInfo(url, paramMap);
	}
	
}
