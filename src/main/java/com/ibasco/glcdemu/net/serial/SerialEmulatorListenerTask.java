package com.ibasco.glcdemu.net.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.ibasco.glcdemu.emulator.GlcdEmulator;
import com.ibasco.glcdemu.enums.SerialBaudRate;
import com.ibasco.glcdemu.enums.SerialFlowControl;
import com.ibasco.glcdemu.enums.SerialParity;
import com.ibasco.glcdemu.enums.SerialStopBits;
import com.ibasco.glcdemu.net.EmulatorListenerTask;
import com.ibasco.glcdemu.net.ListenerOptions;
import com.ibasco.glcdemu.services.SerialPortService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * A service task for receiving a byte stream via Serial communication interface.
 *
 * @author Rafael Ibasco
 */
public class SerialEmulatorListenerTask extends EmulatorListenerTask {
    private static final Logger log = LoggerFactory.getLogger(SerialEmulatorListenerTask.class);

    private int baudRate = SerialBaudRate.RATE_9600.toValue();

    private int parity = SerialParity.NONE.toValue();

    private int flowControl = SerialFlowControl.NONE.toValue();

    private int stopBits = SerialStopBits.ONE_STOP_BIT.toValue();

    private int dataBits = 8;

    private SerialPort serialPort;

    public SerialEmulatorListenerTask(GlcdEmulator emulator) {
        super(emulator);
    }

    @Override
    protected void configure(ListenerOptions options) {
        log.debug("Processing serial options: {}", options);

        SerialPortService serialPortService = options.get(SerialListenerOptions.PORT_SERVICE);
        String portName = options.get(SerialListenerOptions.SERIAL_PORT_NAME);
        serialPort = serialPortService.findSerialPortByName(portName);
        baudRate = options.get(SerialListenerOptions.BAUD_RATE);
        parity = options.get(SerialListenerOptions.PARITY);
        flowControl = options.get(SerialListenerOptions.FLOW_CONTROL);
        stopBits = options.get(SerialListenerOptions.STOP_BITS);
        dataBits = options.get(SerialListenerOptions.DATA_BITS);

        if (serialPort == null) {
            String msg = "No serial ports found";
            updateMessage(msg);
            throw new IllegalStateException(msg);
        }

        configurePort(serialPort);
    }

    private void configurePort(SerialPort port) {
        port.setComPortParameters(baudRate, dataBits, stopBits, parity);
        port.setFlowControl(flowControl);
        log.debug("Port settings: Baudrate={}, DataBits={}, StopBits={}, Parity={}, Flow Control={}", baudRate, dataBits, stopBits, parity, flowControl);
    }

    @Override
    protected void cleanup() {
        if (serialPort != null && serialPort.closePort()) {
            log.debug("Serial port successfully closed");
            updateMessage("Serial port closed");
        }
    }

    @Override
    protected String getName() {
        return "SERIAL-LISTENER";
    }

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
        int size = calculateBufferSize();
        updateMessage("Calculated buffer size: " + size);

        try (BufferedInputStream bis = new BufferedInputStream(serialPort.getInputStream(), size)) {
            log.debug("Using serial port: {}", serialPort);
            reset();
            setConnected(true);
            while (!isCancelled()) {
                if (bis.available() > 0) {
                    processByte((byte) bis.read());
                }
            }
        }
    }
}
