package com.ibasco.glcdemu.net.tcp;

import com.ibasco.glcdemu.emulator.GlcdEmulator;
import com.ibasco.glcdemu.exceptions.InvalidOptionException;
import com.ibasco.glcdemu.net.EmulatorListenerTask;
import com.ibasco.glcdemu.net.ListenerOptions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

@SuppressWarnings("Duplicates")
public class TcpEmulatorListenerTask extends EmulatorListenerTask {

    private static final Logger log = LoggerFactory.getLogger(TcpEmulatorListenerTask.class);

    private Selector selector;
    private ServerSocketChannel socketChannel;
    private InetSocketAddress listenAddress;

    public TcpEmulatorListenerTask(GlcdEmulator emulator) {
        super(emulator);
    }

    @Override
    protected void configure(ListenerOptions options) throws Exception {
        String ipAddress = options.get(TcpListenerOptions.IP_ADDRESS);
        Integer port = options.get(TcpListenerOptions.PORT_NUMBER);
        if (StringUtils.isBlank(ipAddress) || port == null) {
            throw new InvalidOptionException("IP address or port must be specified");
        }
        listenAddress = new InetSocketAddress(ipAddress, port);
    }

    private void acceptClient(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        setConnected(true);
        log.debug("Accepted new client: {}", client.getLocalAddress());
    }

    private void initSocketServer() throws IOException {
        socketChannel = ServerSocketChannel.open();
        selector = Selector.open();
        socketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        socketChannel.bind(listenAddress);
        log.debug("Emulator service is now listening on '{}:{}'", listenAddress.getAddress().getHostAddress(), listenAddress.getPort());
    }

    @Override
    protected void process() throws Exception {
        try {
            ByteBuffer recv = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);

            reset();
            initSocketServer();

            //Listen for events
            while (!isCancelled()) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();

                while (iter.hasNext()) {
                    try {
                        SelectionKey key = iter.next();
                        if (key.isAcceptable()) {
                            acceptClient(selector, socketChannel);
                        }

                        if (key.isReadable()) {
                            try {
                                SocketChannel client = (SocketChannel) key.channel();

                                recv.clear();
                                int bytesRead = client.read(recv);

                                if (bytesRead == -1) {
                                    client.close();
                                    log.debug("Client closed connection");
                                    setConnected(false);
                                    reset();
                                    continue;
                                }

                                recv.flip();
                                while (recv.hasRemaining()) {
                                    processByte(recv.get());
                                }
                            } catch (IOException e) {
                                log.warn(e.getMessage());
                                key.cancel();
                                key.channel().close();
                                setConnected(false);
                                reset();
                            }
                        }
                    } finally {
                        iter.remove();
                    }
                }
            } //while
        } finally {
            log.debug("Exiting listen service");
            selector.close();
            socketChannel.close();
        }
    }

    @Override
    protected void cleanup() throws Exception {
        if (selector != null)
            selector.close();
        if (socketChannel != null)
            socketChannel.close();
    }
}
