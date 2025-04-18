package com.nevermore.sbridge.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.netty.channel.IoHandlerFactory;
import io.netty.util.concurrent.Future;

/**
 * Nevermore
 */
class SharableEventLoopGroupTest {

    private SharableEventLoopGroup eventLoopGroup;

    @BeforeEach
    void setUp() {
        IoHandlerFactory mockIoHandlerFactory = mock(IoHandlerFactory.class);
        eventLoopGroup = new SharableEventLoopGroup(2, mockIoHandlerFactory);
    }

    @Test
    void testInitialReferenceCount() {
        assertEquals(1, eventLoopGroup.refCnt(), "Initial reference count should be 1");
    }

    @Test
    void testRetain() {
        eventLoopGroup.retain();
        assertEquals(2, eventLoopGroup.refCnt(), "Reference count should be incremented to 2");

        eventLoopGroup.retain(3);
        assertEquals(5, eventLoopGroup.refCnt(), "Reference count should be incremented by 3");
    }

    @Test
    void testRelease() {
        // Increase ref count first
        eventLoopGroup.retain();
        assertEquals(2, eventLoopGroup.refCnt());

        // Release once, should not shut down
        boolean result = eventLoopGroup.release();
        assertFalse(result, "Release should return false when ref count > 0");
        assertEquals(1, eventLoopGroup.refCnt(), "Reference count should be decremented");

        // Final release, should trigger shutdown
        SharableEventLoopGroup spyGroup = spy(eventLoopGroup);
        Future<?> mockFuture = mock(Future.class);
        doReturn(mockFuture).when(spyGroup).shutdownGracefully(anyLong(), anyLong(), any(TimeUnit.class));

        result = spyGroup.release();
        assertTrue(result, "Release should return true when ref count reaches 0");
        assertEquals(0, spyGroup.refCnt(), "Reference count should be 0");
        verify(spyGroup).shutdownGracefully();
    }

    @Test
    void testReleaseWithDecrement() {
        // Set ref count to 5
        eventLoopGroup.retain(4);
        assertEquals(5, eventLoopGroup.refCnt());

        // Release 3, should not shut down
        boolean result = eventLoopGroup.release(3);
        assertFalse(result, "Release should return false when ref count > 0");
        assertEquals(2, eventLoopGroup.refCnt(), "Reference count should be decremented by 3");

        // Release remaining 2, should trigger shutdown
        SharableEventLoopGroup spyGroup = spy(eventLoopGroup);
        spyGroup.retain(); // make ref count 3 for the spy
        Future<?> mockFuture = mock(Future.class);
        doReturn(mockFuture).when(spyGroup).shutdownGracefully(anyLong(), anyLong(), any(TimeUnit.class));

        result = spyGroup.release(3);
        assertTrue(result, "Release should return true when ref count reaches 0");
        assertEquals(0, spyGroup.refCnt(), "Reference count should be 0");
        verify(spyGroup).shutdownGracefully();
    }

    @Test
    void testTouch() {
        assertSame(eventLoopGroup, eventLoopGroup.touch(), "Touch should return this");
        assertSame(eventLoopGroup, eventLoopGroup.touch("hint"), "Touch with hint should return this");
    }
}
