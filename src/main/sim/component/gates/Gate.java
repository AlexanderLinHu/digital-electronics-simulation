package sim.component.gates;

import sim.component.Device;
import sim.component.StatusCodesSys;
import sim.component.connection.SingleOutputBus;

/**
 * Completes the implementation needs from {@link Device} for a generic gate. Gate's do not support advanced
 * input alias and the only alias for the output bus is {@value SingleOutputBus#OUT_BUS_ALIAS}.
 * The output of gate's is only dependent on the number of high input buses (except for and-gates, which are
 * also dependent on the total number of inputs).
 *
 * <p>The {@code Gate} abstract class only requires implementors to complete the {@code gateLogic(int)} method,
 * which receives as input the number of high input buses read. The method returns what the gate's output bus
 * should be based on this number.
 */
public abstract sealed class Gate extends Device implements SingleOutputBus
                permits AndGate, OrGate, XorGate, BufferGate, InverterGate {

    //*>> -------------------- Constructors -------------------- */

    /**
     * Construct a logic gate
     *
     * @param numInputBuses      the number of input buses
     * @param initialOutputState the initial output state, must either be null or a boolean array of length 1
     * @throws IllegalArgumentException if given a non-positive number of input buses, or
     *                                  {@code numOutputBus != length(initialOutputState)}
     * @see Device#Device
     */
    public Gate(String type, int numInputBuses, boolean[] initialOutputState) {
        super(type, numInputBuses, 1, initialOutputState);
    }

    /**
     * Construct a logic gate
     *
     * @param numInputBuses      the number of input buses
     * @throws IllegalArgumentException if given a non-positive number of input buses, or
     *                                  {@code numOutputBus != length(initialOutputState)}
     */
    public Gate(String type, int numInputBuses) {
        this(type, numInputBuses, new boolean[1]);
    }

    //*>> -------------------- Connect systems -------------------- */
    // Using Device implementation


    //*>> -------------------- Update Operations -------------------- */

    @Override
    protected final void deviceLogic() {
        int numTrues = 0;
        for (boolean value : this.inputBuffer) {
            if (value == true) {numTrues++;}
        }
        this.outputBuffer[0] = this.gateLogic(numTrues);
    }

    /**
     * Computes the value of the gate's output based on the input.
     *
     * @implNote
     * This method is called in the {@code update()} method, the implementor is expected to compute if the out is
     * true or false based on the number of trues read from the input.
     * Recall the number of buses is accessible by {@code this.inputBuses.length}
     *
     * @param numTrues the number of trues read from the input
     * @return new value of the (only) output bus
     */
    protected abstract boolean gateLogic(int numTrues);


    //*>> -------------------- Index and Alias Conversion -------------------- */

    /**
     * {@inheritDoc}
     *
     * <p>Gate input buses' do not have aliases, this method is fundamentally a string to integer conversion
     * (provided the given number represents a valid bus).
     *
     * @throws BusDneException {@inheritDoc}
     */
    @Override
    public final int inAliasToIndex(String alias) {

        try {
            int index = Integer.parseInt(alias);
            if (index >= 0 && index < this.inputBus.length) {
                return index;
            }
        }
        catch (NumberFormatException e) {}

        throw StatusCodesSys.runtimeException(StatusCodesSys.UNKNOWN_INPUT_ALIAS_TO_INDEX, alias);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Gate input buses' do not have aliases, this method is fundamentally an integer to string conversion
     * (provided the number represents a valid bus).
     *
     * @throws BusDneException {@inheritDoc}
     */
    @Override
    public final String inIndexToAlias(int index) {
        if (index >= 0 && index < this.inputBus.length) {
            return Integer.toString(index);
        }

        throw StatusCodesSys.runtimeException(StatusCodesSys.UNKNOWN_INPUT_INDEX_TO_ALIAS, index);
    }

    //Output alias conversion methods are handled by the SingleOutputBus interface


    //*>> -------------------- Get Operations -------------------- */
    // Using Device implementation, note getType() is not possible to be implemented in Device or here
}
