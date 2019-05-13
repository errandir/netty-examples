package com.github.errandir.netty.examples;

import java.io.*;
import java.nio.channels.FileChannel;
import org.junit.Test;

import static io.netty.buffer.Unpooled.wrappedBuffer;
import static java.nio.file.Files.readAllBytes;
import static org.junit.Assert.*;

public class ByteChannelWriterTest {

    private static String testDirectoryPath() {
        return ByteChannelWriterTest.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    private static File testOutputFile() {
        return new File(testDirectoryPath(), "test.output");
    }

    @Test public void fileChannel() throws InterruptedException, IOException {
        byte[] bytesToTransfer = { 1, 2, 3, 4, 5, 6, 7 };
        testOutputFile().delete();
        try (RandomAccessFile file = new RandomAccessFile(testOutputFile(), "rwd");
             final FileChannel channel = file.getChannel();
             TestClientServerConnection connection = new TestClientServerConnection(new ByteChannelWriter(channel))) {
            assertEquals(0, channel.size());
            connection.getClientChannel().writeAndFlush(wrappedBuffer(bytesToTransfer));
            connection.getClientChannel().close();
            connection.getServerChannel().closeFuture().sync();
            assertArrayEquals(bytesToTransfer, readAllBytes(testOutputFile().toPath()));
        }
    }
}
