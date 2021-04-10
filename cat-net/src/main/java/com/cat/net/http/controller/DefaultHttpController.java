package com.cat.net.http.controller;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.cat.net.http.HttpConstant;
import com.cat.net.http.base.RequestInfo;
import com.cat.net.http.process.RequestProcessor;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.CharsetUtil;

/**
 * 默认Http处理器, 仅支持简单参数, 不支持复杂参数以及文件上传操作 游戏内的http处理, 仅仅支持后台的相关低并发操作,
 * 
 * @author Jeremy
 */
@Component
public class DefaultHttpController implements IRequestController {

	private static final Logger log = LoggerFactory.getLogger(DefaultHttpController.class);

	@Autowired
	protected RequestProcessor processor;

	protected transient boolean running; // 服务器状态, true-运行中

	public void onReceive(FullHttpRequest httpRequest, FullHttpResponse httpResponse) throws Exception {
		if (!running) {
			log.error("默认HTTP分发处理器, 服务器不在运行状态, 舍弃消息");
			return;
		}
		URI uri = new URI(httpRequest.uri());
		String url = uri.getPath();// 获取到请求uri
		Map<String, Object> paramMap = paramMap(httpRequest, httpResponse);
		RequestInfo requestInfo = RequestInfo.create(url, paramMap);
		try {
			String ret = processor.invoke(requestInfo);
			httpResponse.content().writeCharSequence(ret, CharsetUtil.UTF_8);
		} catch (Exception e) {
			;
			httpResponse.content().writeCharSequence(HttpResponseStatus.BAD_REQUEST.toString(), CharsetUtil.UTF_8);
			log.error("DefaultHttpController error", e);
		}
	}

	/**
	 * 解析参数
	 * @param httpRequest
	 * @return
	 * @throws IOException
	 */
	private Map<String, Object> paramMap(FullHttpRequest httpRequest, FullHttpResponse httpResponse) throws IOException {
		HttpMethod method = httpRequest.method();
		
		Map<String, Object> paramMap = new HashMap<>();
		//	添加默认httpRequest, httpResponse
		paramMap.put(HttpConstant.HTTPREQUEST, httpRequest);
		paramMap.put(HttpConstant.HTTPRESPONSE, httpResponse);
		
		if (HttpMethod.GET == method) {
			// 是GET请求
			QueryStringDecoder decoder = new QueryStringDecoder(httpRequest.uri());
			Map<String, List<String>> parameters = decoder.parameters();
			for (Entry<String, List<String>> entry : parameters.entrySet()) {
				paramMap.put(entry.getKey(), entry.getValue().get(0)); // value取第一个
			}
		} else if (HttpMethod.POST == method) {
			// 是POST请求
			//Json内容
			HttpHeaders headers = httpRequest.headers();
			if (headers.containsValue(HttpConstant.CONTENTTYPE, HttpHeaderValues.APPLICATION_JSON, true)) {
				if (httpRequest.content().isReadable()) {
					String text = httpRequest.content().toString(CharsetUtil.UTF_8);
					JSONObject jsonObject =JSONObject.parseObject(text);
					paramMap.putAll(jsonObject);
				}
			}else if(headers.containsValue(HttpConstant.CONTENTTYPE, HttpHeaderValues.MULTIPART_FORM_DATA, true)){
				//	内容在data
				HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), httpRequest);
				List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
				for (InterfaceHttpData parm : parmList) {
					if (parm.getHttpDataType() == HttpDataType.Attribute) {
						Attribute data = (Attribute) parm;
						paramMap.put(data.getName(), data.getValue());
					}
				}
			}
		}
		return paramMap;
	}

	@Override
	public void serverStatus(boolean running) {
		this.running = running;
	}

}
