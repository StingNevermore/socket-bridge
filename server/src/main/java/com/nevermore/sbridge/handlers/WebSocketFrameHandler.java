package com.nevermore.sbridge.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

/**
 * Handler for WebSocket text frames.
 */
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketFrameHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame frame) throws Exception {
        frame = frame.retain();
        
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        logger.info("Handler added: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        logger.info("Handler removed: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception caught", cause);
        ctx.close();
    }
}
