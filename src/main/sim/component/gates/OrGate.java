package sim.component.gates;

/**
 * An Or-Gate is a logical gate that outputs a high if at least 1 input is high.
 */
public final class OrGate extends Gate {

    public static final String TYPE = "Or-Gate";


    //*>> -------------------- Constructor -------------------- */

    /**
     * Construct an Or-Gate with {@code numInputBuses} inputs and an output initialized to the first (and only element) in
     * {@code initialOutputState}. Note this array must have size 1.
     *
     * @param numInputBuses      the number of input buses
     * @param initialOutputState the initial output state, must either be null or a boolean array of length 1
     * @throws IllegalArgumentException if given <2 input buses, or
     *                                  {@code numOutputBus != length(initialOutputState)}
     */
    public OrGate(int numInputBuses, boolean[] initialOutputState) {
        super(TYPE, numInputBuses, initialOutputState);

        if (numInputBuses < 2) {
            throw new IllegalArgumentException("Or gates must have at least 2 input buses, received " + numInputBuses);
        }
    }


    /**
     * Construct an Or-Gate with {@code numInputBuses} inputs and an output initialized to false
     *
     * @param numInputBuses      the number of input buses
     * @throws IllegalArgumentException if given <2 input buses
     */
    public OrGate(int numInputBuses) {
        super(TYPE, numInputBuses);

        if (numInputBuses < 2) {
            throw new IllegalArgumentException("Or gates must have at least 2 input buses, received " + numInputBuses);
        }
    }


    //*>> -------------------- Logic/getType implementations -------------------- */
    // Only methods that needed to be implemented

    /**
     * {@inheritDoc}
     *
     * <p>An Or-Gate returns true if at least one input is true
     */
    @Override
    protected boolean gateLogic(int numTrues) {
        return numTrues > 0;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
