package com.cat.net.http.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cat.net.http.controller.IRequestController;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

@Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private static final Logger log = LoggerFactory.getLogger(HttpServerHandler.class);

	private IRequestController controller;

	public HttpServerHandler(IRequestController controller) {
		this.controller = controller;
		log.info("============HttpServerHandler===========:{}", controller);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.info("============channelActive===========");
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.info("============channelInactive===========");
		super.channelInactive(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (cause instanceof TooLongFrameException) {
			sendError(ctx, HttpResponseStatus.BAD_REQUEST);
			return;
		} else if (cause instanceof IllegalArgumentException) {
			sendError(ctx, HttpResponseStatus.NOT_FOUND);
			return;
		}
		if (ctx.channel().isActive()) {
			sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		log.info("============channelInactive===========");
//		if (request.decoderResult() != DecoderResult.SUCCESS) {
//			exceptionCaught(ctx, new IllegalArgumentException());
//			return;
//		}

		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
		response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");

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
	 * 错误时返回
	 * @param ctx
	 * @param status
	 */
	private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
		ByteBuf buffer = ctx.alloc().buffer(100);
		buffer.writeBytes(("Failure: " + status.toString() + "\r\n").getBytes(CharsetUtil.UTF_8));
		DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
				buffer);
		response.headers().set("Content-Type", "text/html; charset=UTF-8");
		response.headers().set("Content-Length", buffer.readableBytes());
		ctx.channel().write(response).addListener(ChannelFutureListener.CLOSE);
		ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
	}

}
