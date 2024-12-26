package sim.component.gates;


/**
 * An And-Gate is a logical gate that outputs a high if all inputs are high.
 */
public final class AndGate extends Gate {

    public static final String TYPE = "And-Gate";

    //*>> -------------------- Constructor -------------------- */

    /**
     * Construct an And-Gate with {@code numInputBuses} inputs and an output initialized to the first (and only element) in
     * {@code initialOutputState}. Note this array must have size 1.
     *
     * @param numInputBuses         the number of input buses
     * @param initialOutputState    the value(s) of the output buses on device creation, if {@code null} then
     *                              all output buses are initialized to {@code false}
     * @throws IllegalArgumentException if given <2 input buses, or
     *                                  {@code numOutputBus != length(initialOutputState)}
     */
    public AndGate(int numInputBuses, boolean[] initialOutputState) {
        super(TYPE, numInputBuses, initialOutputState);

        if (numInputBuses < 2) {
            throw new IllegalArgumentException("And gates must have at least 2 input buses, received " + numInputBuses);
        }
    }

    /**
     * Construct an And-Gate with {@code numInputBuses} inputs and an output initialized to false
     *
     * @param numInputBuses      the number of input buses
     * @throws IllegalArgumentException if given <2 input buses
     */
    public AndGate(int numInputBuses) {
        super(TYPE, numInputBuses);

        if (numInputBuses < 2) {
            throw new IllegalArgumentException("And gates must have at least 2 input buses, received " + numInputBuses);
        }
    }


    //*>> -------------------- Logic/getType implementations -------------------- */
    // Only methods that needed to be implemented

    /**
     * {@inheritDoc}
     *
     * <p>An and-gate returns true if all inputs are true
     */
    @Override
    protected boolean gateLogic(int numTrues) {
        return numTrues == this.inputBus.length;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
