package com.nevermore.sbridge.messages;

import com.nevermore.sbridge.proto.BridgeMessage;
import com.nevermore.sbridge.server.BridgeServerRole;

/**
 * @author Snake
 */
public record MessageDefinition(
        BridgeServerRole role,
        BridgeMessage message) {
}
