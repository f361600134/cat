package com.cat.net.network.bootstrap;

import static com.cat.net.network.bootstrap.CustomeInnerProtoId.HEARTBEAT_PING;
import static com.cat.net.network.bootstrap.CustomeInnerProtoId.HEARTBEAT_PONG;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
/**
 * 自定义心跳
 * @author Jeremy
 */
public class CustomeHeartbeatHandler extends SimpleChannelInboundHandler<ByteBuf> {
	
	private static final Logger log = LoggerFactory.getLogger(CustomeHeartbeatHandler.class);

//	@Override
//	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//		super.channelRead(ctx, msg);
//	}
	
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
//        if(byteBuf.getInt(0) == HEARTBEAT_PING) {
//            sendPong(channelHandlerContext);
//        } else if(byteBuf.getInt(0) == HEARTBEAT_PONG) {
//        	log.info("=====>{}", "get pong msg from " + channelHandlerContext.channel().remoteAddress());
//        } 
        log.info("=====>{}", byteBuf.getInt(0));
        log.info("============================");
    }

    protected void sendPong(ChannelHandlerContext channelHandlerContext) {
        ByteBuf buf = channelHandlerContext.alloc().buffer(3);
        buf.writeInt(HEARTBEAT_PONG);
        channelHandlerContext.writeAndFlush(buf);
        log.info("=====>{}", "send pong message to " + channelHandlerContext.channel().remoteAddress());
    }

    protected void sendPing(ChannelHandlerContext channelHandlerContext) {
        ByteBuf buf = channelHandlerContext.alloc().buffer(3);
        buf.writeInt(HEARTBEAT_PING);
        channelHandlerContext.writeAndFlush(buf);
        log.info("=====>{}", "send ping message to " + channelHandlerContext.channel().remoteAddress());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case ALL_IDLE:
                    handlALLIdle(ctx);
                    break;
                case READER_IDLE:
                    handlReadIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handlWriteIdle(ctx);
                    break;
                 default:
                     break;
            }
        }
    }

    protected void handlReadIdle(ChannelHandlerContext channelHandlerContext) {
        log.info("=====>{}", "READ_IDLE---");
    }

    protected void handlWriteIdle(ChannelHandlerContext channelHandlerContext) {
        log.info("=====>{}", "WRITE_IDLE---");
    }

    protected void handlALLIdle(ChannelHandlerContext channelHandlerContext) {
        log.info("=====>{}", "ALL_IDLE---");
        sendPing(channelHandlerContext);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("=====>{}", "channel:" + ctx.channel().remoteAddress() + " is active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("=====>{}", "channel:" + ctx.channel().remoteAddress() + " is inactive");
        ctx.close();
    }
}