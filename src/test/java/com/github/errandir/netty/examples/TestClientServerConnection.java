package com.github.errandir.netty.examples;

import io.netty.bootstrap.*;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.*;

public class TestClientServerConnection implements AutoCloseable {
    private final EventLoopGroup serverEventLoopGroup;
    private final NioEventLoopGroup clientEventLoopGroup;
    private final Channel serverParentChannel;
    private Channel serverChannel;
    private final Channel clientChannel;


    private static final int TEST_PORT = 10000;

    public TestClientServerConnection(ChannelHandler... handlers) throws InterruptedException {

        serverEventLoopGroup = new NioEventLoopGroup();
        serverParentChannel = new ServerBootstrap()
                .group(serverEventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override public void initChannel(SocketChannel ch) {
                        serverChannel = ch;
                        ch.pipeline().addLast(handlers);
                    }
                })
                .bind(TEST_PORT).sync().channel();

        clientEventLoopGroup = new NioEventLoopGroup();
        clientChannel = new Bootstrap()
                .group(clientEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override public void initChannel(SocketChannel ch) {}
                }).connect("localhost", TEST_PORT).sync().channel();
    }

    @Override public void close() {
        clientChannel.close();
        clientEventLoopGroup.shutdownGracefully();
        serverParentChannel.close();
        serverEventLoopGroup.shutdownGracefully();
    }

    public Channel getServerChannel() {
        return serverChannel;
    }

    public Channel getClientChannel() {
        return clientChannel;
    }
}
