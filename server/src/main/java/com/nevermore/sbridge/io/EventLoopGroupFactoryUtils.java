package com.nevermore.sbridge.io;

import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.beans.factory.ObjectProvider;

import com.nevermore.sbridge.io.impl.EventLoopGroupSeperatedFactory;
import com.nevermore.sbridge.io.impl.EventLoopGroupSharableFactory;
import com.nevermore.sbridge.props.SbridgeProperties.BridgeCommonProperties;
import com.nevermore.sbridge.utils.ObjectProviderUtils;

/**
 * @author Snake
 */
public class EventLoopGroupFactoryUtils {

    public static EventLoopGroupFactory determineEventLoopGroupFactory(boolean enableSharedEvenLoopGroup,
            BridgeCommonProperties commonProperties,
            ObjectProvider<EventLoopGroupFactory> eventLoopGroupFactoryProvider) {
        checkNotNull(commonProperties, "commonProperties");
        if (enableSharedEvenLoopGroup) {
            if (!commonProperties.enableSeparatedEventLoopGroup()) {
                return ObjectProviderUtils.getByClass(eventLoopGroupFactoryProvider,
                        EventLoopGroupSharableFactory.class);
            }
        }

        return ObjectProviderUtils.getByClass(eventLoopGroupFactoryProvider, EventLoopGroupSeperatedFactory.class);
    }


}
