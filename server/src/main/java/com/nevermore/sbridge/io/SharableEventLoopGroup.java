package com.nevermore.sbridge.io;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import io.netty.channel.IoHandlerFactory;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.util.ReferenceCounted;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.ReferenceCountUpdater;

/**
 * @author Snake
 */
public class SharableEventLoopGroup extends MultiThreadIoEventLoopGroup implements ReferenceCounted {

    private static final long REFCNT_FIELD_OFFSET =
            ReferenceCountUpdater.getUnsafeOffset(SharableEventLoopGroup.class, "refCnt");
    private static final AtomicIntegerFieldUpdater<SharableEventLoopGroup> AIF_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(SharableEventLoopGroup.class, "refCnt");

    private static final ReferenceCountUpdater<SharableEventLoopGroup> updater =
            new ReferenceCountUpdater<>() {
                @Override
                protected AtomicIntegerFieldUpdater<SharableEventLoopGroup> updater() {
                    return AIF_UPDATER;
                }

                @Override
                protected long unsafeOffset() {
                    return REFCNT_FIELD_OFFSET;
                }
            };

    public SharableEventLoopGroup(int nThreads, IoHandlerFactory ioHandlerFactory) {
        super(nThreads, ioHandlerFactory);
    }

    // Value might not equal "real" reference count, all access should be via the updater
    @SuppressWarnings({"unused", "FieldMayBeFinal"})
    private volatile int refCnt = updater.initialValue();

    @Override
    public int refCnt() {
        return updater.refCnt(this);
    }

    @Override
    public ReferenceCounted retain() {
        return updater.retain(this);
    }

    @Override
    public ReferenceCounted retain(int increment) {
        return updater.retain(this, increment);
    }

    @Override
    public ReferenceCounted touch() {
        return touch(null);
    }

    @Override
    public ReferenceCounted touch(Object hint) {
        return this;
    }

    @Override
    public boolean release() {
        return handleRelease(updater.release(this));
    }

    @Override
    public boolean release(int decrement) {
        return handleRelease(updater.release(this, decrement));
    }

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        return super.shutdownGracefully(quietPeriod, timeout, unit);
    }

    private boolean handleRelease(boolean result) {
        if (result) {
            shutdownGracefully();
        }
        return result;
    }
}
