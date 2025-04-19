package com.nevermore.sbridge.io.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.nevermore.sbridge.io.EventLoopGroupProvider;
import com.nevermore.sbridge.props.SbridgeProperties;
import com.nevermore.sbridge.server.BridgeServerRole;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;

/**
 * @author Snake
 */
@Lazy
@Component
public class EventLoopGroupSeperatedProvider implements EventLoopGroupProvider {

    private final SbridgeProperties properties;

    public EventLoopGroupSeperatedProvider(SbridgeProperties properties) {
        this.properties = properties;
    }

    @Override
    public EventLoopGroup bossGroup(BridgeServerRole role) {
        checkNotNull(role, "role");
        var eventLoopGroupProperties = role.getCommonProperties(properties).eventLoopGroup();
        return new MultiThreadIoEventLoopGroup(eventLoopGroupProperties.bossNThreads(), NioIoHandler.newFactory());
    }

    @Override
    public EventLoopGroup workerGroup(BridgeServerRole role) {
        checkNotNull(role, "role");
        var eventLoopGroupProperties = role.getCommonProperties(properties).eventLoopGroup();
        return new MultiThreadIoEventLoopGroup(eventLoopGroupProperties.workerNThreads(), NioIoHandler.newFactory());
    }
}
