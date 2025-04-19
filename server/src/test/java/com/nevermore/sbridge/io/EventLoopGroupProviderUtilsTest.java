package com.nevermore.sbridge.io;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

import com.nevermore.sbridge.io.impl.EventLoopGroupSeperatedProvider;
import com.nevermore.sbridge.io.impl.EventLoopGroupSharableProvider;
import com.nevermore.sbridge.props.SbridgeProperties.BridgeCommonProperties;
import com.nevermore.sbridge.props.SbridgeProperties.EventLoopGroupProperties;

/**
 * @author nevermore
 */
class EventLoopGroupProviderUtilsTest {

    private ObjectProvider<EventLoopGroupProvider> mockProvider;
    private BridgeCommonProperties mockProperties;
    private EventLoopGroupSharableProvider sharableFactory;
    private EventLoopGroupSeperatedProvider seperatedFactory;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        mockProvider = mock(ObjectProvider.class);
        mockProperties = mock(BridgeCommonProperties.class);
        sharableFactory = mock(EventLoopGroupSharableProvider.class);
        seperatedFactory = mock(EventLoopGroupSeperatedProvider.class);
        EventLoopGroupProperties mockEventLoopGroupProps = mock(EventLoopGroupProperties.class);

        when(mockProperties.eventLoopGroup()).thenReturn(mockEventLoopGroupProps);
    }

    @Test
    void testNullPropertiesThrowsException() {
        assertThrows(NullPointerException.class,
                () -> EventLoopGroupProviderUtils.determineEventLoopGroupFactory(true, null, mockProvider),
                "Should throw NullPointerException when properties is null");
    }

    @Test
    void testSharedEnabledAndSeparatedDisabled() {
        // Setup
        when(mockProperties.enableSeparatedEventLoopGroup()).thenReturn(false);
        when(mockProvider.stream()).thenReturn(java.util.stream.Stream.of(sharableFactory, seperatedFactory));

        // Test
        EventLoopGroupProvider result = EventLoopGroupProviderUtils.determineEventLoopGroupFactory(
                true, mockProperties, mockProvider);

        // Verify
        assertSame(sharableFactory, result,
                "Should return EventLoopGroupSharableProvider when shared is enabled and separated is disabled");
    }

    @Test
    void testSharedEnabledButSeparatedAlsoEnabled() {
        // Setup
        when(mockProperties.enableSeparatedEventLoopGroup()).thenReturn(true);
        when(mockProvider.stream()).thenReturn(java.util.stream.Stream.of(sharableFactory, seperatedFactory));

        // Test
        EventLoopGroupProvider result = EventLoopGroupProviderUtils.determineEventLoopGroupFactory(
                true, mockProperties, mockProvider);

        // Verify
        assertSame(seperatedFactory, result,
                "Should return EventLoopGroupSeperatedProvider when separated is enabled even if shared is enabled");
    }

    @Test
    void testSharedDisabled() {
        // Setup - separated setting doesn't matter in this case
        when(mockProvider.stream()).thenReturn(java.util.stream.Stream.of(sharableFactory, seperatedFactory));

        // Test
        EventLoopGroupProvider result = EventLoopGroupProviderUtils.determineEventLoopGroupFactory(
                false, mockProperties, mockProvider);

        // Verify
        assertSame(seperatedFactory, result,
                "Should return EventLoopGroupSeperatedProvider when shared is disabled");
    }

    @Test
    void testFactoryNotFound() {
        // Setup - return empty stream to simulate factory not found
        when(mockProvider.stream()).thenReturn(java.util.stream.Stream.empty());
        when(mockProperties.enableSeparatedEventLoopGroup()).thenReturn(false);

        // Test
        EventLoopGroupProvider result = EventLoopGroupProviderUtils.determineEventLoopGroupFactory(
                true, mockProperties, mockProvider);

        // Verify
        assertNull(result, "Should return null when factory not found");
    }

    @Test
    void testMultipleFactoriesOfSameType() {
        // Setup - simulate multiple factories of same type
        EventLoopGroupSharableProvider sharableFactory2 = mock(EventLoopGroupSharableProvider.class);
        when(mockProvider.stream()).thenReturn(java.util.stream.Stream.of(sharableFactory, sharableFactory2));
        when(mockProperties.enableSeparatedEventLoopGroup()).thenReturn(false);

        // Test & Verify
        assertThrows(IllegalStateException.class,
                () -> EventLoopGroupProviderUtils.determineEventLoopGroupFactory(true, mockProperties, mockProvider),
                "Should throw IllegalStateException when multiple factories of same type found");
    }
}
