package com.ibasco.glcdemu.services;

import com.ibasco.glcdemu.emulator.GlcdEmulator;
import com.ibasco.pidisplay.core.u8g2.U8g2ByteEvent;
import javafx.application.Platform;
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

    private ObjectProperty<GlcdEmulator> emulator = new SimpleObjectProperty<>();

    private StringProperty listenIp = new SimpleStringProperty();

    private IntegerProperty listenPort = new SimpleIntegerProperty();

    private ReadOnlyBooleanWrapper listening = new ReadOnlyBooleanWrapper();

    private ReadOnlyBooleanWrapper connected = new ReadOnlyBooleanWrapper();

    private class JavaNioListenTask extends Task<Void> {

        private Selector selector;
        private ServerSocketChannel socketChannel;
        private GlcdEmulator emulator;

        public JavaNioListenTask() {
            this.emulator = EmulatorService.this.emulator.get();
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
            socketChannel.bind(new InetSocketAddress(listenIp.get(), listenPort.get()));
        }

        @Override
        protected Void call() throws Exception {
            try {
                ByteBuffer recv = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);

                if (emulator == null)
                    throw new NullPointerException("Emulator cannot be null");

                emulator.reset();
                initSocketServer();
                setListening(true);

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
                                        log.debug("Client closed connection");
                                        setConnected(false);
                                        emulator.reset();
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
                                                emulator.processByte(event.getValue());
                                                break;
                                        }
                                    }
                                } catch (IOException e) {
                                    key.cancel();
                                    throw e;
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
                setListening(false);
            }
            return null;
        }
    }

    public boolean isConnected() {
        return connected.get();
    }

    public ReadOnlyBooleanProperty connectedProperty() {
        return connected.getReadOnlyProperty();
    }

    private void setConnected(boolean value) {
        Platform.runLater(() -> connected.set(value));
    }

    public boolean isListening() {
        return listening.get();
    }

    private void setListening(boolean value) {
        Platform.runLater(() -> listening.set(value));
    }

    public ReadOnlyBooleanProperty listeningProperty() {
        return listening.getReadOnlyProperty();
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
    protected void running() {
        log.debug("Service is running");
        listening.set(true);
    }

    @Override
    protected void cancelled() {
        log.debug("Service is cancelled");
        listening.set(false);
        //emulator.get().reset();
    }

    @Override
    protected void failed() {
        log.debug("Service has failed", getException());
        listening.set(false);
    }

    @Override
    protected Task<Void> createTask() {
        return new JavaNioListenTask();
    }
}
