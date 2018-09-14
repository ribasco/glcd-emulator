package com.ibasco.glcdemu.net.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.ibasco.glcdemu.emulator.GlcdEmulator;
import com.ibasco.glcdemu.enums.SerialBaudRate;
import com.ibasco.glcdemu.enums.SerialFlowControl;
import com.ibasco.glcdemu.enums.SerialParity;
import com.ibasco.glcdemu.enums.SerialStopBits;
import com.ibasco.glcdemu.net.ListenerOptions;
import com.ibasco.glcdemu.net.RemoteListenerTask;
import com.ibasco.glcdemu.services.SerialPortService;
import com.ibasco.glcdemu.utils.FpsCounter;
import com.ibasco.pidisplay.core.u8g2.U8g2Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

/**
 * Serial Service. This method is really slow (e.g. only 5 fps on 115200 baudrate), use this if you have no other option.
 */
public class SerialRemoteListenerTask extends RemoteListenerTask {
    private static final Logger log = LoggerFactory.getLogger(SerialRemoteListenerTask.class);

    private static final int MSG_START = 0xFE;

    private static final int MSG_END = 0xFF;

    private static final int U8X8_MSG_BYTE_SEND = 0x17;

    private int baudRate = SerialBaudRate.RATE_9600.toValue();

    private int parity = SerialParity.NONE.toValue();

    private int flowControl = SerialFlowControl.NONE.toValue();

    private int stopBits = SerialStopBits.ONE_STOP_BIT.toValue();

    private int dataBits = 8;

    private SerialPort serialPort;

    public SerialRemoteListenerTask(GlcdEmulator emulator) {
        super(emulator);
    }

    @Override
    protected void processOptions(ListenerOptions options) {
        log.debug("Processing serial options: {}", options);

        SerialPortService serialPortService = options.get(SerialListenerOptions.PORT_SERVICE);
        String portName = options.get(SerialListenerOptions.SERIAL_PORT_NAME);
        serialPort = serialPortService.findSerialPortByName(portName);
        baudRate = options.get(SerialListenerOptions.BAUD_RATE);
        parity = options.get(SerialListenerOptions.PARITY);
        flowControl = options.get(SerialListenerOptions.FLOW_CONTROL);
        stopBits = options.get(SerialListenerOptions.STOP_BITS);
        dataBits = options.get(SerialListenerOptions.DATA_BITS);

        if (serialPort == null)
            throw new IllegalStateException("No serial port specified");

        configurePort(serialPort);
    }

    private void configurePort(SerialPort port) {
        port.setComPortParameters(baudRate, dataBits, stopBits, parity);
        port.setFlowControl(flowControl);
        log.debug("Port settings: Baudrate={}, DataBits={}, StopBits={}, Parity={}, Flow Control={}", baudRate, dataBits, stopBits, parity, flowControl);
    }

    @Override
    protected void closeResources() {
        if (serialPort != null && serialPort.closePort()) {
            log.debug("Serial port successfully closed");
            updateMessage("Serial port closed");
        }
    }

    @Override
    protected String getName() {
        return "SERIAL-LISTENER";
    }

    private boolean isValidMessage(int msg) {
        return msg != -1 && (msg == MSG_START || msg == MSG_END || msg == U8X8_MSG_BYTE_SEND);
    }

    @Override
    protected void failed() {
        super.failed();
        log.debug("Serial Service Failed. Last Byte Count: {}", byteCount);
    }

    private FpsCounter fpsCounter = new FpsCounter(1, TimeUnit.SECONDS);

    private int byteCount = 0;

    @Override
    protected void process() throws Exception {
        log.debug("Started remote serial task");

        if (!serialPort.isOpen()) {
            if (!serialPort.isOpen()) {
                updateMessage("Opening serial port");
                if (!serialPort.openPort()) {
                    updateMessage("Could not open serial device");
                    throw new IOException("Could not connect to serial device");
                }
            }
        }

        updateMessage("Connected to serial device");

        try (BufferedInputStream bis = new BufferedInputStream(serialPort.getInputStream(), 14400)) {
            log.debug("Using serial port: {}", serialPort);

            reset();
            setConnected(true);

            int size = calculateBufferSize();
            ByteBuffer buffer = ByteBuffer.allocate(8192);

            updateMessage("Calculated buffer size: " + size);

            boolean collect = false;


            fpsCounter.setListener(e -> log.debug("FPS: {}", e));

            while (!isCancelled()) {
                while (bis.available() > 0) {
                    fpsCounter.pulse();
                    int msg = bis.read();
                    if (msg == MSG_START) {
                        //check if buffer needs to be processed
                        if (buffer.position() > 0) {
                            log.debug("Buffer size: {}", buffer.position());
                            processBuffer(buffer);
                        }
                        collect = true;
                    } else {
                        fpsCounter.pulse();
                        if (collect) {
                            buffer.put((byte) msg);
                        }
                    }
                }
            }
        }
    }

    private void processBuffer(ByteBuffer buffer) {
        try {
            buffer.flip();
            while (buffer.hasRemaining()) {
                fpsCounter.pulse();
                processMessage((byte) U8g2Message.U8X8_MSG_BYTE_SEND.getCode(), buffer.get());
                fpsCounter.count();
            }
        } finally {
            buffer.clear();
        }

    }
}
