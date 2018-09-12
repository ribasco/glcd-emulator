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
import com.ibasco.pidisplay.core.u8g2.U8g2Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

public class SerialRemoteListenerTask extends RemoteListenerTask {
    private static final Logger log = LoggerFactory.getLogger(SerialRemoteListenerTask.class);

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

        configurePort(serialPort);
    }

    private void configurePort(SerialPort port) {
        port.setComPortParameters(baudRate, dataBits, stopBits, parity);
        port.setFlowControl(flowControl);
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        log.debug("CANCELLED");
        if (serialPort.closePort()) {
            log.debug("Serial port successfully closed");
        }
    }

    @Override
    protected void failed() {
        super.failed();
        log.error("Serial remote task failed", getException());
    }

    @Override
    protected void process() throws Exception {
        log.debug("Started remote serial task");

        if (serialPort == null)
            throw new IllegalStateException("No serial port specified");

        if (!serialPort.isOpen()) {
            if (!serialPort.openPort()) {
                throw new IOException("Could not connect to serial device");
            }
        }

        setConnected(true);

        log.debug("Using serial port: {}", serialPort);
        while (!isCancelled()) {
            while (serialPort.bytesAvailable() == 0) {
                Thread.sleep(20);
            }
            byte[] readBuffer = new byte[serialPort.bytesAvailable()];
            int numRead = serialPort.readBytes(readBuffer, readBuffer.length);
            ByteBuffer buff = ByteBuffer.wrap(readBuffer);
            while (buff.hasRemaining()) {
                byte val = buff.get();
                //byte val = buff.get();
                processMessage((byte) U8g2Message.U8X8_MSG_BYTE_SEND.getCode(), val);
            }
        }

        setConnected(false);
        serialPort.closePort();
        log.debug("Serial task cancelled");
    }
}
