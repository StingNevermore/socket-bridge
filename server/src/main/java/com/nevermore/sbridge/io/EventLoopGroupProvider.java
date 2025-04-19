package com.nevermore.sbridge.io;

import com.nevermore.sbridge.server.BridgeServerRole;

import io.netty.channel.EventLoopGroup;

/**
 * @author Snake
 */
public interface EventLoopGroupProvider {

    EventLoopGroup bossGroup(BridgeServerRole role);

    EventLoopGroup workerGroup(BridgeServerRole role);
}
