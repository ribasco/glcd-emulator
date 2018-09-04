package com.ibasco.glcdemu.services;

import com.ibasco.glcdemu.emulator.GlcdEmulator;
import com.ibasco.pidisplay.core.u8g2.U8g2ByteEvent;
import javafx.beans.property.*;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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

public class EmulatorService extends Service<Void> {

    private static final Logger log = LoggerFactory.getLogger(EmulatorService.class);

    private Selector selector;

    private ServerSocketChannel socketChannel;

    private ObjectProperty<GlcdEmulator> emulator = new SimpleObjectProperty<>();

    private StringProperty listenIp = new SimpleStringProperty();

    private IntegerProperty listenPort = new SimpleIntegerProperty();

    private ReadOnlyBooleanWrapper connected = new ReadOnlyBooleanWrapper();

    public boolean isConnected() {
        return connected.get();
    }

    public ReadOnlyBooleanProperty connectedProperty() {
        return connected.getReadOnlyProperty();
    }

    public String getListenIp() {
        return listenIp.get();
    }

    public StringProperty listenIpProperty() {
        return listenIp;
    }

    public void setListenIp(String listenIp) {
        this.listenIp.set(listenIp);
    }

    public int getListenPort() {
        return listenPort.get();
    }

    public IntegerProperty listenPortProperty() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort.set(listenPort);
    }

    public GlcdEmulator getEmulator() {
        return emulator.get();
    }

    public ObjectProperty<GlcdEmulator> emulatorProperty() {
        return emulator;
    }

    public void setEmulator(GlcdEmulator emulator) {
        this.emulator.set(emulator);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    protected void ready() {
        log.debug("Service is ready");
    }

    @Override
    protected void running() {
        log.debug("Service is running");
        connected.set(true);
    }

    @Override
    protected void succeeded() {
        log.debug("Service succceeded");
    }

    @Override
    protected void cancelled() {
        log.debug("Service is cancelled");
        connected.set(false);
    }

    @Override
    protected void failed() {
        log.debug("Service has failed", getException());
        connected.set(false);
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    ByteBuffer recv = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);

                    if (emulator.get() == null)
                        throw new NullPointerException("Emulator cannot be null");

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
                                        recv.flip();

                                        if (bytesRead == -1) {
                                            client.close();
                                            log.debug("Client closed connection. End of stream");
                                            continue;
                                        }

                                        while (recv.hasRemaining()) {
                                            U8g2ByteEvent event = new U8g2ByteEvent(recv.get(), recv.get());
                                            switch (event.getMessage()) {
                                                case U8X8_MSG_START:
                                                    break;
                                                case U8X8_MSG_END:
                                                    break;
                                                case U8X8_MSG_BYTE_SEND:
                                                    emulator.get().processByte(event.getValue());
                                                    break;
                                            }
                                        }
                                    } catch (IOException e) {
                                        key.cancel();
                                        log.warn("Error during read", e);
                                    }
                                }
                            } finally {
                                iter.remove();
                            }
                        }
                    } //while
                } finally {
                    log.debug("Exiting listen service");
                    socketChannel.close();
                }
                return null;
            }
        };
    }

    private void initSocketServer() throws IOException {
        socketChannel = ServerSocketChannel.open();
        socketChannel.bind(new InetSocketAddress(listenIp.get(), listenPort.get()));
        selector = Selector.open();
        socketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void acceptClient(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        log.debug("Registered accepted client");
    }
}
