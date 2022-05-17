package com.maehem.gamepad;

/**
 * Registers used for communicating with the XRA1405
 */
@SuppressWarnings("SpellCheckingInspection")
enum Xra1405Register {
    GSR1(0x00),  // GPIO State for P0-7                         (Read Only)
    GSR2(0x01),  // GPIO State for P8-15                        (Read Only)
    OCR1(0x02),  // Output Control for P0-7
    OCR2(0x03),  // Output Control for P8-15
    PIR1(0x04),  // Input Polarity Inversion for P0-P7
    PIR2(0x05),  // Input Polarity Inversion for P8-P15
    GCR1(0x06),  // GPIO Configuration for P0-P7
    GCR2(0x07),  // GPIO Configuration for P8-P15
    PUR1(0x08),  // Input Internal Pull-up Resistor Enable/Disable for P0-P7
    PUR2(0x09),  // Input Internal Pull-up Resistor Enable/Disable for P8-P15
    IER1(0x0A),  // Input Interrupt Enable for P0-P7
    IER2(0x0B),  // Input Interrupt Enable for P8-P15
    TSCR1(0x0C), // Output Three-State Control for P0-P7
    TSCR2(0x0D), // Output Three-State Control for P8-P15
    ISR1(0x0E),  // Input Interrupt Status for P0-P7            (Read Only)
    ISR2(0x0F),  // Input Interrupt Status for P8-P15           (Read Only)
    REIR1(0x10), // Input Rising Edge Interrupt Enable for P0-P7
    REIR2(0x11), // Input Rising Edge Interrupt Enable for P8-P15
    FEIR1(0x12), // Input Falling Edge Interrupt Enable for P0-P7
    FEIR2(0x13), // Input Falling Edge Interrupt Enable for P8-P15
    IFR1(0x14),  // Input Filter Enable/Disable for P0-P7
    IFR2(0x15);  // Input Filter Enable/Disable for P8-P15


    private final byte value;
    private final byte writeAddress;
    private final byte readAddress;

    Xra1405Register(int value) {
        this((byte) value);
    }

    /**
     * Pre-calculates the read and write address for this register and stores them.
     * To differentiate between a read and a write, reads have the MSB set, 
     * whereas writes have the MSB clear.
     * While this could be calculated on-the-fly, these values are used so often 
     * that it makes sense to cache them.
     *
     * @param value Raw address of register with MSB unset, used for calculating 
     * R/W addresses
     */
    Xra1405Register(byte value) {
        this.value = value;
        this.writeAddress = (byte) ((value << 1) & 0x7E);
        this.readAddress = (byte) (writeAddress | 0x80);
    }

    public byte getValue() {
        return this.value;
    }

    public byte getReadAddress() {
        return readAddress;
    }

    public byte getWriteAddress() {
        return writeAddress;
    }
}