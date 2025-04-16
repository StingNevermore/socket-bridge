package com.nevermore.sbridge.io.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.google.common.base.Supplier;
import com.nevermore.sbridge.io.EventLoopGroupFactory;
import com.nevermore.sbridge.io.SharableEventLoopGroup;
import com.nevermore.sbridge.props.SbridgeProperties;
import com.nevermore.sbridge.server.BridgeServerRole;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioIoHandler;

/**
 * @author Snake
 */
@Lazy
@Component
public class EventLoopGroupSharableFactory implements EventLoopGroupFactory {

    private final SbridgeProperties properties;

    public EventLoopGroupSharableFactory(SbridgeProperties properties) {
        this.properties = properties;
    }

    private volatile EventLoopGroup bossGroup;
    private volatile EventLoopGroup workerGroup;

    @Override
    public EventLoopGroup createBossGroup(BridgeServerRole role) {
        var eventLoopGroupProperties = properties.bridge().sharedEventLoopGroup();
        if (bossGroup == null) {
            synchronized (this) {
                if (bossGroup == null) {
                    bossGroup = createEventLoopGroup(eventLoopGroupProperties::bossNThreads);
                }
            }
        }

        return bossGroup;
    }

    @Override
    public EventLoopGroup createWorkerGroup(BridgeServerRole role) {
        var eventLoopGroupProperties = properties.bridge().sharedEventLoopGroup();
        if (workerGroup == null) {
            synchronized (this) {
                if (workerGroup == null) {
                    workerGroup = createEventLoopGroup(eventLoopGroupProperties::workerNThreads);
                }
            }
        }

        return workerGroup;
    }

    private static EventLoopGroup createEventLoopGroup(Supplier<Integer> nThreadsSupplier) {
        return new SharableEventLoopGroup(nThreadsSupplier.get(), NioIoHandler.newFactory());

    }
}
