package com.nevermore.sbridge.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * @author Snake
 */
@ConfigurationProperties("sbridge")
public record SbridgeProperties(@DefaultValue BridgeProperties bridge) {

    public record BridgeProperties(@DefaultValue("true") boolean enableSharedEventLoopGroup,
                                   @DefaultValue EventLoopGroupProperties sharedEventLoopGroup,
                                   @DefaultValue WebsocketProperties websocket) {

    }

    public record WebsocketProperties(@DefaultValue("true") boolean enabled, //
                                      @DefaultValue("9501") int port, //
                                      @DefaultValue("/ws") String path,
                                      @DefaultValue("false") boolean enableSeparatedEventLoopGroup,
                                      @DefaultValue("65535") int maxFrameLength,
                                      @DefaultValue("65535") int maxPayloadLength,
                                      @DefaultValue("true") boolean enablePingPong,
                                      @DefaultValue EventLoopGroupProperties eventLoopGroup)
            implements BridgeCommonProperties {

    }

    public record EventLoopGroupProperties(@DefaultValue("0") int bossNThreads, @DefaultValue("0") int workerNThreads) {

    }

    public interface BridgeCommonProperties {
        boolean enabled();

        boolean enableSeparatedEventLoopGroup();

        int port();

        EventLoopGroupProperties eventLoopGroup();
    }
}
