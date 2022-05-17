package com.maehem.gamepad;

import com.pi4j.context.Context;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiConfig;
import com.pi4j.io.spi.SpiMode;
import com.pi4j.io.spi.SpiProvider;

/**
 * Implementation of the Exar XRA1405 GPIO Expander using SPI with Pi4J
 */
public class Xra1405Component {
    /**
     * Default SPI channel for the XRA1405
     */
    public static final int DEFAULT_SPI_CHANNEL = 0;
    /**
     * Default SPI baud rate for the XRA1405
     */
    public static final int DEFAULT_SPI_BAUD_RATE = 1000000;

    /**
     * Pi4J SPI instance
     */
    protected Spi spi;

    /**
     * Creates a new instance with the default channel and baud rate.
     *
     * @param pi4j Pi4J context
     */
    public Xra1405Component(Context pi4j) {
        this(pi4j, DEFAULT_SPI_CHANNEL, DEFAULT_SPI_BAUD_RATE);
    }

    /**
     * Creates a new instance with a custom channel and baud rate.
     *
     * @param pi4j       Pi4J context
     * @param spiChannel SPI channel
     * @param spiBaud    SPI baud rate
     */
    public Xra1405Component(Context pi4j, int spiChannel, int spiBaud) {
         // get a SPI I/O provider from the Pi4J context
        SpiProvider spiProvider = pi4j.provider("pigpio-spi");
        Spi spi = spiProvider.create(buildSpiConfig(pi4j, spiChannel, spiBaud));
        this.spi = spi;
        this.init();
    }

    /**
     * Creates a new instance using the SPI instance from Pi4J.
     *
     * @param spi      SPI instance
     */
//    public Xra1405Component(Spi spi) {
//        this.spi = spi;
//        this.init();
//    }

    /**
     * This will also setup the internal timer to use for timeout handling and enables the CRC coprocessor.
     * The antennas of the PCD will automatically be enabled as part of this routine.
     */
    private void init() {
        writeRegister(Xra1405Register.PUR1, (byte) 0b1111_1111); // Pullup inputs 0-7

    }

    /**
     * Returns the current SPI instance for the LED matrix.
     *
     * @return SPI instance
     */
    protected Spi getSpi() {
        return this.spi;
    }

    /**
     * Builds a new SPI configuration for the RFID component
     *
     * @param pi4j    Pi4J context
     * @param channel SPI channel
     * @param baud    SPI baud rate
     * @return SPI configuration
     */
    private static SpiConfig buildSpiConfig(Context pi4j, int channel, int baud) {
//        return Spi.newConfigBuilder(pi4j)
//            .id("SPI" + channel)
//            .name("GPIO Expander SPI")
//            .address(channel)
//            .baud(baud)
//            .build();
        // create SPI config
         var config  = Spi.newConfigBuilder(pi4j)
             .id("my-spi")
             .name("My SPI")
             .address(channel)
             .baud(baud)
             .mode(SpiMode.MODE_0)
             .build();
             //.chipSelect(SpiChipSelect.CS_0)      // <----- CONFIGURE SPI CS/ADDRESS/CHANNEL
             //.bus(SpiBus.BUS_1)                    <----- CONFIGURE SPI BUS


             return config;
 // use try-with-resources to auto-close SPI when complete
// try (var spi = pi4j.spi().create(config)) {
//
//     // write data using output stream
//     spi.out().write(sample);
// }
    }

    public byte getGpioStateL() {
        return readRegister(Xra1405Register.GSR1);
    }
    
    /**
     * Writes a single byte to the specified PCD register.
     *
     * @param register PCD register to write
     * @param value    Byte to be written
     */
    private void writeRegister(Xra1405Register register, byte value) {
        spi.transfer(new byte[]{register.getWriteAddress(), value});
    }

    /**
     * Writes one or more bytes to the specified PCD register.
     *
     * @param register PCD register to write
     * @param values   Bytes to be written
     */
    private void writeRegister(Xra1405Register register, byte[] values) {
        final var buffer = new byte[values.length + 1];
        buffer[0] = register.getWriteAddress();
        System.arraycopy(values, 0, buffer, 1, values.length);

        spi.transfer(buffer);
    }

    /**
     * Writes a short to the specified PCD registers by splitting into two bytes.
     *
     * @param registerHigh PCD register where upper half (MSB) is stored
     * @param registerLow  PCD register where lower half (LSB) is stored
     * @param value        Short to be written to registers
     */
    private void writeRegister(Xra1405Register registerHigh, Xra1405Register registerLow, short value) {
        int tmp = value & 0xFFFF;
        writeRegister(registerHigh, (byte) ((tmp >> 8) & 0xFF));
        writeRegister(registerLow, (byte) (tmp & 0xFF));
    }

    /**
     * Reads a single byte from the specified PCD register.
     *
     * @param register PCD register to read
     * @return Byte read from register
     */
    private byte readRegister(Xra1405Register register) {
        final var buffer = new byte[]{register.getReadAddress(), 0};
        spi.transfer(buffer);
        return buffer[1];
    }

    /**
     * Reads the specified amount of bytes from the specified PCD register.
     * Supports bit-oriented frames where the first relevant bit is shifted accordingly.
     *
     * @param register    PCD register to read
     * @param length      Amount of bytes to read from the PCD including the partial byte (only applicable if rxAlignBits != 0)
     * @param rxAlignBits Position of first bit which is relevant, specify 0 to consider all 8 bits valid
     * @return Byte array with retrieved data
     */
    private byte[] readRegister(Xra1405Register register, int length, int rxAlignBits) {
        // Break out early if zero-length was given
        if (length == 0) {
            return new byte[]{};
        }

        // Create buffer for retrieving data
        final var buffer = new byte[length + 1];
        for (int i = 0; i < length; i++) {
            buffer[i] = register.getReadAddress();
        }
        buffer[buffer.length - 1] = 0;

        // Transfer buffer
        spi.transfer(buffer);

        // Prepare result buffer
        final var result = new byte[length];
        int resultIndex = 0;

        // Start at buffer position 1 as the first byte (where the command was stored) is always zero
        int bufferIndex = 1;

        // Adjust first byte for bit-oriented frames
        if (rxAlignBits != 0) {
            // Create bitmask where LSB is shifted by given amount
            byte mask = (byte) ((0xFF << rxAlignBits) & 0xFF);
            // Mask received first byte and store into result buffer
            result[resultIndex++] = (byte) (buffer[bufferIndex++] & ~mask);
        }

        // Copy all pending bytes into the result buffer
        System.arraycopy(buffer, bufferIndex, result, resultIndex, length - bufferIndex + 1);

        return result;
    }
}