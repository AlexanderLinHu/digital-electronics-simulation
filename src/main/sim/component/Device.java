package sim.component;

import sim.component.connection.OutputPointer;
import sim.controller.IdGenerator;
import sim.exception.StatusRuntimeException;


/**
 * Provides a skeleton implementation for devices, which are systems that can be represented without the use of
 * other systems. This includes logic gates. Device updates are effectively atomic operations since they have no
 * internal systems.
 *
 * <p>To complete implementation the programmer only need to implement the "Index and Alias Conversion" section
 * as defined in the {@link System} interface, as well as the abstract {@linkplain Device#deviceLogic device logic}
 * method introduced in this class.
 *
 * @see System
 * @see Circuit
 */
public abstract class Device implements System {

    /**
     * An array of {@code OutputPointer} where the indices represent input lines and the elements stores the
     * location of the output bus to read from.
     */
    protected final OutputPointer[] inputBus;

    /**
     * Used to store a snapshot of the values in the input bus. It stores the boolean wrapper objects to permit
     * null values. Null values should be present if and only if the input buffer is not loaded. Either all elements
     * are null or none are.
     *
     * <p>For information on why this is needed see the {@linkplain System#loadInputBuffer loadInputBuffer method
     * in the Connectable interface}
     */
    protected final Boolean[] inputBuffer;

    /**
     * Stores the state of the output of the device. Technically, this is the state of the output bus at a given time
     * but since the internal elements are not being modelled, the output buffer can be updated using the
     * {@link #update()} method.
     */
    protected final boolean[] outputBuffer;

    /**
     * Unique integer to identify the device amongst other systems
     */
    protected final int id;

    protected final String type;


    //*>> ----------------------------------------------------------------------------------------------------------- <<*//
    //*>>                                                 Constructor                                                 <<*//
    //*>> ----------------------------------------------------------------------------------------------------------- <<*//

    /**
     * TODO
     *
     * @param type         the device type
     * @param numInputBus  the number of input buses
     * @param numOutputBus the number of output buses
     * @param initialOutputState the value(s) of the output buses on device creation, if {@code null} then all output
     *                              buses are initialized to {@code false}
     * @throws IllegalArgumentException if given a negative number of input or output buses, or
     *                                  {@code numOutputBus != length(initialOutputState)}
     */
    public Device(String type, int numInputBus, int numOutputBus, boolean[] initialOutputState) {
        this.type = type == null ? "" : type;
        this.id = IdGenerator.get();

        if ((numInputBus < 0) || (numOutputBus < 0)) {
            throw new IllegalArgumentException(
                "System with id " + id  + " of type <" + getType() + "> received a negative number of IO buses");
        }

        // Initialize fields
        this.inputBus = new OutputPointer[numInputBus];
        this.inputBuffer = new Boolean[numInputBus];
        this.outputBuffer = new boolean[numOutputBus];

        if (initialOutputState == null) {
            return;
        }

        if (numOutputBus != initialOutputState.length) {
            throw new IllegalArgumentException(
                "System with id " + id  + " of type <" + getType() + "> expected " + numOutputBus
                + " output buses but received " + initialOutputState.length + " initial output values"
            );
        }

        for (int i = 0; i < numOutputBus; i++) {
            this.outputBuffer[i] = initialOutputState[i];
        }
    }

    //*>> ----------------------------------------------------------------------------------------------------------- <<*//
    //*>>                                               Connect systems                                               <<*//
    //*>> ----------------------------------------------------------------------------------------------------------- <<*//


    /**
     * @throws StatusRuntimeException {@inheritDoc}
     */
    @Override
    public void connectInput(System fromSystem, String fromBus, String toBus) {
        final int toBusConverted;
        final int fromBusConverted;

        toBusConverted = inAliasToIndex(toBus);
        fromBusConverted = fromSystem.outAliasToIndex(fromBus);

        if (inputBus[toBusConverted] != null) {
            throw StatusCodesSys.runtimeException(StatusCodesSys.EXISTING_CONNECTION, toBus);
        }

        inputBus[toBusConverted] = new OutputPointer(fromSystem, fromBusConverted);
    }

    //*>> ----------------------------------------------------------------------------------------------------------- <<*//
    //*>>                                              Update Operations                                              <<*//
    //*>> ----------------------------------------------------------------------------------------------------------- <<*//


    /**
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public boolean update() {
        // A null in the inputBuffer means it is not loaded
        if (inputBuffer[0] == null) {
            loadInputBuffer();
        }

        // Update the device's output buses
        deviceLogic();

        // Invalidate the cached buffer
        for (int i = 0; i < inputBuffer.length; i++) {
            inputBuffer[i] = null;
        }

        return true;
    }

    /**
     * This method should only be called by {@link Device#update()}, update the state of the
     * {@code outputBuses} based on the current state of the {@code inputBuffer}
     */
    protected abstract void deviceLogic();

    /**
     * {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public void loadInputBuffer() {
        for (int i = 0; i < inputBus.length; i++) {
            inputBuffer[i] = inputBus[i].getValue();
        }
    }

    //*>> ----------------------------------------------------------------------------------------------------------- <<*//
    //*>>                                         Index and Alias Conversion                                          <<*//
    //*>> ----------------------------------------------------------------------------------------------------------- <<*//
    // No methods in this section can be implemented at this level of abstraction


    //*>> ----------------------------------------------------------------------------------------------------------- <<*//
    //*>>                                               Get Operations                                                <<*//
    //*>> ----------------------------------------------------------------------------------------------------------- <<*//

    @Override
    public boolean[] getOut() {
        boolean[] out = new boolean[outputBuffer.length];

        for (int i = 0; i < out.length; i++) {
            out[i] = outputBuffer[i];
        }

        return out;
    }

    @Override
    public boolean getOut(String bus) {
        return outputBuffer[outAliasToIndex(bus)];
    }

    @Override
    public boolean getOut(int bus) {
        return outputBuffer[bus];
    }

    @Override
    public OutputPointer[] getInputBus() {
        OutputPointer[] inputs = new OutputPointer[inputBus.length];

        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = inputBus[i];
        }

        return inputs;
    }

    @Override
    public int getID() {
        return id;
    }
}
