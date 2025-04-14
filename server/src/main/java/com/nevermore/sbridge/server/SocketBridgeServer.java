package com.nevermore.sbridge.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author Snake
 */
@Component
public class SocketBridgeServer implements ApplicationRunner, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(SocketBridgeServer.class);

    private static final int PORT = 8888;
    private static final String WEBSOCKET_PATH = "/ws";

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Starting WebSocket server on port " + PORT);

        bossGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
        workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

        startServer();
    }

    private void startServer() throws Exception {
        try {
            var bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();

                            // HTTP protocol codec
                            pipeline.addLast(new HttpServerCodec());
                            // HTTP message aggregator
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            // Support for writing large data chunks
                            pipeline.addLast(new ChunkedWriteHandler());

                            // WebSocket protocol handler
                            pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));

                            // Custom WebSocket frame handler
                            pipeline.addLast(new WebSocketFrameHandler());
                        }
                    });

            serverChannel = bootstrap.bind(PORT).sync().channel();
            logger.info("WebSocket server started successfully");
        } catch (Exception e) {
            logger.error("Failed to start WebSocket server: ", e);
            shutdownGracefully();
            throw e;
        }
    }

    @Override
    public void destroy() throws Exception {
        logger.info("Shutting down WebSocket server");
        shutdownGracefully();
    }

    private void shutdownGracefully() {
        if (serverChannel != null) {
            serverChannel.close();
        }

        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }

        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
