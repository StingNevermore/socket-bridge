package com.nevermore.sbridge.server;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.nevermore.sbridge.io.EventLoopGroupFactoryUtils.determineEventLoopGroupFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.nevermore.sbridge.io.EventLoopGroupFactory;
import com.nevermore.sbridge.io.SharableEventLoopGroup;
import com.nevermore.sbridge.io.impl.EventLoopGroupSharableFactory;
import com.nevermore.sbridge.props.SbridgeProperties;
import com.nevermore.sbridge.props.SbridgeProperties.WebsocketProperties;
import com.nevermore.sbridge.protocols.WebSocketFrameHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
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
public class WebsocketBridgeServer implements ApplicationRunner, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketBridgeServer.class);

    private final WebsocketProperties properties;
    private final EventLoopGroupFactory eventLoopGroupFactory;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    public WebsocketBridgeServer(SbridgeProperties properties,
            ObjectProvider<EventLoopGroupFactory> eventLoopGroupFactoryProvider) {
        this.properties = properties.bridge().websocket();
        var thisFactory = determineEventLoopGroupFactory(properties.bridge().enableSharedEventLoopGroup(),
                properties.bridge().websocket(), eventLoopGroupFactoryProvider);
        eventLoopGroupFactory = checkNotNull(thisFactory, "Can not determine EventLoopGroupFactory");
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!properties.enabled()) {
            logger.info("Websocket bridge is disabled");
            return;
        }
        var port = properties.port();
        var path = properties.path();
        var sharedMode = eventLoopGroupFactory instanceof EventLoopGroupSharableFactory;
        logger.info("Starting WebSocket server on port {} with path: {} running in sharedMode: {}", port, path,
                sharedMode);

        bossGroup = eventLoopGroupFactory.createBossGroup(BridgeServerRole.WEBSOCKET);
        workerGroup = eventLoopGroupFactory.createWorkerGroup(BridgeServerRole.WEBSOCKET);

        startServer(port, path);
    }

    private void startServer(int port, String path) throws Exception {
        try {
            var bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() {
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
                            pipeline.addLast(new WebSocketServerProtocolHandler(path, null, true));

                            // Custom WebSocket frame handler
                            pipeline.addLast(new WebSocketFrameHandler());
                        }
                    });

            serverChannel = bootstrap.bind(port).sync().channel();
            logger.info("WebSocket server started successfully");
        } catch (Exception e) {
            logger.error("Failed to start WebSocket server: ", e);
            destroy();
            throw e;
        }
    }

    @Override
    public void destroy() {
        logger.info("Shutting down WebSocket server");
        if (serverChannel != null) {
            serverChannel.close().syncUninterruptibly();
        }

        shutdownEventLoopGroup(bossGroup);
        shutdownEventLoopGroup(workerGroup);
    }

    private void shutdownEventLoopGroup(EventLoopGroup eventLoopGroup) {
        if (eventLoopGroup != null) {
            if (eventLoopGroup instanceof SharableEventLoopGroup) {
                ((SharableEventLoopGroup) eventLoopGroup).release();
            } else {
                eventLoopGroup.shutdownGracefully();
            }
        }
    }
}
