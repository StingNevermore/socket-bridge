package com.nevermore.sbridge.io;

import com.nevermore.sbridge.server.BridgeServerRole;

import io.netty.channel.EventLoopGroup;

/**
 * @author Snake
 */
public interface EventLoopGroupFactory {

    EventLoopGroup createBossGroup(BridgeServerRole role);

    EventLoopGroup createWorkerGroup(BridgeServerRole role);
}
