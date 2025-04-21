package com.nevermore.sbridge.handlers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nevermore.sbridge.proto.BridgeMessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * WebSocketFrameHandler的单元测试，使用Netty测试工具
 */
public class WebsocketFrameHandlerTest {

    private EmbeddedChannel channel;

    @BeforeEach
    void setUp() {
        // 创建一个嵌入式通道，添加待测试的处理器
        channel = new EmbeddedChannel(new WebSocketFrameHandler());
    }

    @Test
    void testBinaryFrameProcessing() {
        BridgeMessage message = BridgeMessage.newBuilder()
                .setMessageId(1)
                .setMessageType(1)
                .build();
        ByteBuf byteBuf = Unpooled.wrappedBuffer(message.toByteArray());
        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(byteBuf);

        channel.writeInbound(frame);
        // 清理
        channel.finish();
    }

    @Test
    void testUnsupportedFrameType() {
        // 创建一个文本帧（不支持的类型）
        TextWebSocketFrame textFrame = new TextWebSocketFrame("test message");

        // 尝试写入并期望异常
        Exception exception = assertThrows(Exception.class, () -> {
            channel.writeInbound(textFrame);
        }, "Should throw exception for unsupported frame type");

        // 验证异常信息
        assertTrue(exception.getCause() instanceof UnsupportedOperationException,
                "Should be UnsupportedOperationException");
        String errorMessage = exception.getCause().getMessage();
        assertTrue(errorMessage.contains("Unsupported frame type"),
                "Error message should contain 'Unsupported frame type'");

        // 完成处理并清理
        channel.finish();
    }

    @Test
    void testExceptionHandling() {
        // 创建异常处理测试的通道
        EmbeddedChannel testChannel = new EmbeddedChannel(new WebSocketFrameHandler());

        // 触发异常处理流程
        testChannel.pipeline().fireExceptionCaught(new RuntimeException("Test exception"));

        // 验证通道已关闭
        assertFalse(testChannel.isOpen(), "Channel should be closed after exception");
        testChannel.finish();
    }

    @Test
    void testChannelLifecycle() {
        // 创建一个新的通道来测试生命周期事件
        EmbeddedChannel lifecycleChannel = new EmbeddedChannel();

        // 添加处理器，这会触发handlerAdded方法
        WebSocketFrameHandler handler = new WebSocketFrameHandler();
        lifecycleChannel.pipeline().addLast(handler);

        // 移除处理器，这会触发handlerRemoved方法
        lifecycleChannel.pipeline().remove(handler);

        // 完成处理（没有显式断言，因为我们只是验证没有异常发生）
        lifecycleChannel.finish();
    }
}

