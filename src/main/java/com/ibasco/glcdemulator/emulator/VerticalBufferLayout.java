/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Emulator
 * Filename: VerticalBufferLayout.java
 *
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 Rafael Luis Ibasco
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * =========================END==================================
 */
package com.ibasco.glcdemulator.emulator;

import com.ibasco.glcdemulator.utils.BitUtils;
import com.ibasco.glcdemulator.utils.PixelBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class VerticalBufferLayout extends BufferLayout {

    private static final Logger log = LoggerFactory.getLogger(VerticalBufferLayout.class);

    private int xOffset = 0;

    private int yOffset = 0;

    private int pageIndex = 0;

    private int pageSize;

    private List<ByteBuffer> pageBufferList = new ArrayList<>();

    @Override
    public void processByte(byte data) {
        if (pageBufferList.isEmpty())
            throw new IllegalStateException("Page buffer is currently empty");

        if (pageIndex > (pageSize - 1))
            throw new IllegalStateException(String.format("Page index is greater than the maximum page limit (page index = %d, max page index = %d)", pageIndex, pageSize - 1));

        ByteBuffer pageBuffer = pageBufferList.get(pageIndex).put(data);

        //Start processing the page buffer here
        if (!pageBuffer.hasRemaining()) {
            pageBuffer.flip();
            pageIndex++;
        }

        //Flush to the display once we have collected the data from all pages
        if (pageIndex > (pageSize - 1))
            flush();
    }

    @Override
    public void initialize() {
        log.debug("Initializing page buffer strategy");
    }

    @Override
    public void reset() {
        log.debug("Resetting page buffer properties");
        xOffset = 0;
        yOffset = 0;
        pageIndex = 0;
        pageSize = getBuffer().getHeight() / 8; //must be a multiple of 8
        pageBufferList = new ArrayList<>(pageSize);
        for (int i = 0; i < pageSize; i++) {
            pageBufferList.add(createPageBuffer());
        }
        //resetPageBuffers();
    }

    private void resetPageBuffers() {
        for (ByteBuffer buffer : pageBufferList)
            buffer.clear();
    }

    private ByteBuffer createPageBuffer() {
        return ByteBuffer.allocate(getBuffer().getWidth()).order(ByteOrder.LITTLE_ENDIAN);
    }

    private void flush() {
        if (pageBufferList.isEmpty())
            return;

        PixelBuffer buffer = getBuffer();

        //log.debug("Flushing buffer (Page Buffer List Size={}, Page Size={}, Page Index={}, Buffer Width={}, Buffer Height={})", pageBufferList.size(), pageSize, pageIndex, buffer.getWidth(), buffer.getHeight());

        for (ByteBuffer pageBuffer : pageBufferList) {
            //process one row of bits at a time from a segment, starting from the least significant bit
            for (int pos = 0; pos < 8; pos++) {
                while (pageBuffer.hasRemaining()) {
                    byte segment = pageBuffer.get();
                    int state = BitUtils.readBit(segment, pos);
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
