package com.ibasco.glcdemu.exceptions;

public class InvalidPixelDimensions extends PixelBufferException {
    public InvalidPixelDimensions(Integer width, Integer height) {
        super(String.format("Invalid pixel dimensions specified. Must be a multiple of 8 (Width = %d, Height = %d)", width, height));

    }
}
