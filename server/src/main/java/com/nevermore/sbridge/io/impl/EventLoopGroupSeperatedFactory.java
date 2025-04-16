package com.nevermore.sbridge.io.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.nevermore.sbridge.io.EventLoopGroupFactory;
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
public class EventLoopGroupSeperatedFactory implements EventLoopGroupFactory {

    private final SbridgeProperties properties;

    public EventLoopGroupSeperatedFactory(SbridgeProperties properties) {
        this.properties = properties;
    }

    @Override
    public EventLoopGroup createBossGroup(BridgeServerRole role) {
        checkNotNull(role, "role");
        var eventLoopGroupProperties = role.getCommonProperties(properties).eventLoopGroup();
        return new MultiThreadIoEventLoopGroup(eventLoopGroupProperties.bossNThreads(), NioIoHandler.newFactory());
    }

    @Override
    public EventLoopGroup createWorkerGroup(BridgeServerRole role) {
        checkNotNull(role, "role");
        var eventLoopGroupProperties = role.getCommonProperties(properties).eventLoopGroup();
        return new MultiThreadIoEventLoopGroup(eventLoopGroupProperties.workerNThreads(), NioIoHandler.newFactory());
    }
}
