package sim.component.connection;

import sim.component.System;

/**
 * A simple immutable class where instances act as a pointer to some system output.
 *
 * <p>There is no input validation in this class, making it possible to create invalid pointers
 */
public final class OutputPointer {

    /** System the output bus belongs too */
    public final System system;

    /** Output bus to read from, from the stored system */
    public final int outBus;

    /**
     * Construct an output pointer to the specified output bus.
     *
     * @param system  the system containing the output bus
     * @param outBus the output bus to read from
     */
    public OutputPointer(System system, int outBus) {
        this.system = system;
        this.outBus = outBus;
    }

    /**
     * Get the value of the stored output bus.
     *
     * @return the logical value of the stored output bus
     */
    public boolean getValue() {
        return system.getOut(outBus);
    }
}