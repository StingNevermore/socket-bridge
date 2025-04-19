package com.nevermore.sbridge.io;

import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.beans.factory.ObjectProvider;

import com.nevermore.sbridge.io.impl.EventLoopGroupSeperatedProvider;
import com.nevermore.sbridge.io.impl.EventLoopGroupSharableProvider;
import com.nevermore.sbridge.props.SbridgeProperties.BridgeCommonProperties;
import com.nevermore.sbridge.utils.ObjectProviderUtils;

/**
 * @author Snake
 */
public class EventLoopGroupProviderUtils {

    public static EventLoopGroupProvider determineEventLoopGroupFactory(boolean enableSharedEvenLoopGroup,
            BridgeCommonProperties commonProperties,
            ObjectProvider<EventLoopGroupProvider> eventLoopGroupFactoryProvider) {
        checkNotNull(commonProperties, "commonProperties");
        if (enableSharedEvenLoopGroup) {
            if (!commonProperties.enableSeparatedEventLoopGroup()) {
                return ObjectProviderUtils.getByClass(eventLoopGroupFactoryProvider,
                        EventLoopGroupSharableProvider.class);
            }
        }

        return ObjectProviderUtils.getByClass(eventLoopGroupFactoryProvider, EventLoopGroupSeperatedProvider.class);
    }


}
