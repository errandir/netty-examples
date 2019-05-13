package com.github.errandir.netty.examples;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class ByteChannelWriter extends ChannelInboundHandlerAdapter {
    private final WritableByteChannel output;

    public ByteChannelWriter(WritableByteChannel output) {
        this.output = output;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuffer src = ((ByteBuf)msg).nioBuffer();
            while (src.hasRemaining())
                output.write(src);
        } else {
            super.channelRead(ctx, msg);
        }
    }
}
