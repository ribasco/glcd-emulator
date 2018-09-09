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

/**
 * A background service that listens on the network for emulator instructions and processes it accordingly.
 *
 * @author Rafael Ibasco
 */
public class EmulatorService extends Service<Void> {

    private static final Logger log = LoggerFactory.getLogger(EmulatorService.class);

    //<editor-fold desc="Service Properties">
    private ObjectProperty<GlcdEmulator> emulator = new SimpleObjectProperty<>();

    private StringProperty listenIp = new SimpleStringProperty();

    private IntegerProperty listenPort = new SimpleIntegerProperty();

    private ReadOnlyBooleanWrapper clientConnected = new ReadOnlyBooleanWrapper();
    //</editor-fold>

    private class JavaNioListenTask extends Task<Void> {

        private Selector selector;
        private ServerSocketChannel socketChannel;
        private GlcdEmulator emulator;
        private InetSocketAddress listenAddress;

        public JavaNioListenTask() {
            this.emulator = EmulatorService.this.emulator.get();
        }

        private void acceptClient(Selector selector, ServerSocketChannel serverSocket) throws IOException {
            SocketChannel client = serverSocket.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
            setClientConnected(true);
            log.debug("Accepted new client: {}", client.getLocalAddress());
        }

        private void initSocketServer() throws IOException {
            if (listenAddress == null) {
                listenAddress = new InetSocketAddress(listenIp.get(), listenPort.get());
            }
            socketChannel = ServerSocketChannel.open();
            selector = Selector.open();
            socketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_ACCEPT);
            socketChannel.bind(listenAddress);
            log.debug("Emulator service is now listening on '{}:{}'", listenAddress.getAddress().getHostAddress(), listenAddress.getPort());
        }

        @Override
        protected Void call() throws Exception {
            try {
                ByteBuffer recv = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);

                if (emulator == null)
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
                                        log.debug("Client closed connection");
                                        setClientConnected(false);
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
                                    log.warn(e.getMessage());
                                    key.cancel();
                                    key.channel().close();
                                    setClientConnected(false);
                                    emulator.reset();
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
                emulator.reset();
            }
            return null;
        }
    }

    //<editor-fold desc="Service Getter/Setter Properties">
    public boolean isClientConnected() {
        return clientConnected.get();
    }

    public ReadOnlyBooleanProperty clientConnectedProperty() {
        return clientConnected.getReadOnlyProperty();
    }

    private void setClientConnected(boolean value) {
        Platform.runLater(() -> clientConnected.set(value));
    }

    //TODO: Move to concrete task
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
    //</editor-fold>

    @Override
    protected Task<Void> createTask() {
        return new JavaNioListenTask();
    }
}
