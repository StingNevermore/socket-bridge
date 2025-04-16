package com.nevermore.sbridge.server;

import com.nevermore.sbridge.props.SbridgeProperties;
import com.nevermore.sbridge.props.SbridgeProperties.BridgeCommonProperties;

/**
 * @author Snake
 */
public enum BridgeServerRole {
    WEBSOCKET,
    HTTP2,
    QUICK,
    ;

    public BridgeCommonProperties getCommonProperties(SbridgeProperties properties) {
        return switch (this) {
            case WEBSOCKET -> properties.bridge().websocket();
            default -> throw new IllegalArgumentException("Unsupported role: " + this);
        };
    }
}
