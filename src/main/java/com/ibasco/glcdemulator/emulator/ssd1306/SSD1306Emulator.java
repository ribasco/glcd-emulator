package com.ibasco.glcdemulator.emulator.ssd1306;

import com.ibasco.glcdemulator.annotations.Emulator;
import com.ibasco.glcdemulator.emulator.GlcdEmulatorBase;
import com.ibasco.glcdemulator.emulator.GlcdRegisterSelect;
import com.ibasco.glcdemulator.utils.BitUtils;
import com.ibasco.glcdemulator.utils.PixelBuffer;
import com.ibasco.ucgdisplay.core.u8g2.U8g2ByteEvent;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusInterface;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdControllerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

@Emulator(
        controller = GlcdControllerType.SSD1306,
        description = "Emulator for SSD1306 controller",
        bus = {
                GlcdBusInterface.I2C_HW,
                GlcdBusInterface.I2C_SW,
                GlcdBusInterface.SPI_SW_4WIRE,
                GlcdBusInterface.SPI_HW_4WIRE,
                GlcdBusInterface.PARALLEL_6800,
                GlcdBusInterface.PARALLEL_8080
        },
        defaultBus = GlcdBusInterface.PARALLEL_8080
)
public class SSD1306Emulator extends GlcdEmulatorBase {

    private static final Logger log = LoggerFactory.getLogger(SSD1306Emulator.class);

    private GlcdRegisterSelect dataCommand;

    private int xOffset = 0;

    private int yOffset = 0;

    private int pageSize = -1;

    private int pageIndex = 0;

    private List<ByteBuffer> pageBufferList;

    @Override
    public void reset() {
        xOffset = 0;
        yOffset = 0;
        pageSize = getBuffer().getHeight() / 8;
        if (pageBufferList == null || pageBufferList.isEmpty()) {
            pageBufferList = new ArrayList<>(pageSize);
            for (int i = 0; i < pageSize; i++) {
                pageBufferList.add(createPageBuffer());
            }
        }
        resetPageBuffers();
    }

    @Override
    public void onByteEvent(U8g2ByteEvent event) {

        ByteBuffer pageBuffer = pageBufferList.get(pageIndex);

        switch (event.getMessage()) {
            case U8X8_MSG_BYTE_SET_DC:
                dataCommand = event.getValue() == 0 ? GlcdRegisterSelect.COMMAND : GlcdRegisterSelect.DATA;
                break;
            case U8X8_MSG_BYTE_SEND:
                if (GlcdRegisterSelect.DATA.equals(dataCommand)) {
                    pageBuffer.put((byte) event.getValue());
                }
                //note: command instructions are ignored
                break;
            default:
                break;
        }

        //Start processing the page buffer here
        if (!pageBuffer.hasRemaining()) {
            pageBuffer.flip();
            pageIndex++;
        }

        //Flush to the display once we have collected the data from all pages
        if (pageIndex > (pageSize - 1)) {
            flush();
        }
    }

    private ByteBuffer createPageBuffer() {
        return ByteBuffer.allocate(getBuffer().getWidth()).order(ByteOrder.LITTLE_ENDIAN);
    }

    private void resetPageBuffers() {
        for (ByteBuffer buffer : pageBufferList) {
            buffer.clear();
        }
    }

    private void flush() {
        if (pageBufferList.isEmpty())
            return;

        PixelBuffer buffer = getBuffer();

        for (ByteBuffer pageBuffer : pageBufferList) {
            //log.debug("Flushing page index {}", pageIndex);
            //process one row at a time in a segment, starting from the least significant bit
            for (int pos = 0; pos < 8; pos++) {
                while (pageBuffer.hasRemaining()) {
                    byte segment = pageBuffer.get();
                    int state = BitUtils.readBit(segment, pos);
                    //log.debug("\t(Page={}, Pos={}) Writing to buffer: x={}, y={}", pageIndex, pos, xOffset, yOffset);
                    buffer.write(xOffset++, yOffset, state);
                }
                pageBuffer.rewind();
                xOffset = 0;
                yOffset++;
            }
            xOffset = 0;
            pageBuffer.clear();
        }

        //reset page buffers and index
        yOffset = 0;
        pageIndex = 0;
        resetPageBuffers();
    }

}
