package com.cat.net.http.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cat.net.http.controller.IRequestController;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;

@Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private static final Logger log = LoggerFactory.getLogger(HttpServerHandler.class);

	private IRequestController controller;
	
	/**
	 * 建立连接成功之后, 生成
	 */
	private FullHttpResponse response;

	public HttpServerHandler(IRequestController controller) {
		this.controller = controller;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (this.response == null) {
			this.response = createResponse();
		}
		controller.onException(response, cause);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
//		log.info("============channelRead0===========");
		if (!request.decoderResult().isSuccess()) {
			exceptionCaught(ctx, new IllegalArgumentException());
			return;
		}
		controller.onReceive(request, response);
		boolean keepAlive = HttpUtil.isKeepAlive(request);
		if (keepAlive) {
			response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
			response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		}
		ChannelFuture future = ctx.writeAndFlush(response);
		future.addListener(ChannelFutureListener.CLOSE);
	}
	
	/**
	 * 创建一个HttpResponse
	 * @return
	 */
	private FullHttpResponse createResponse() {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
		response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		return response;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		log.info("===============channelActive===================={}", controller);
		this.response = createResponse();
		controller.onConnect(this.response);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//		log.info("===============channelInactive===================={}", controller);
		controller.onClose(this.response);
	}
	
}
