package sim.component.gates;

/**
 * An Xor-Gate is a logical gate that outputs a high if an odd number of inputs are high.
 */
public final class XorGate extends Gate {

    public static final String TYPE = "Xor-Gate";


    //*>> -------------------- Constructor -------------------- */

    /**
     * Construct an Xor-Gate with {@code numInputBuses} inputs and an output initialized to the first (and only element) in
     * {@code initialOutputState}. Note this array must have size 1.
     *
     * @param numInputBuses      the number of input buses
     * @param initialOutputState the initial output state, must either be null or a boolean array of length 1
     * @throws IllegalArgumentException if given <2 input buses, or
     *                                  {@code numOutputBus != length(initialOutputState)}
     */
    public XorGate(int numInputBuses, boolean[] initialOutputState) {
        super(TYPE, numInputBuses, initialOutputState);

        if (numInputBuses < 2) {
            throw new IllegalArgumentException("Xor gates must have at least 2 input buses, received " + numInputBuses);
        }
    }

    /**
     * Construct an Xor-Gate
     *
     * @param numInputBuses      the number of input buses
     * @throws IllegalArgumentException if given <2 input buses
     */
    public XorGate(int numInputBuses) {
        super(TYPE, numInputBuses);

        if (numInputBuses < 2) {
            throw new IllegalArgumentException("Xor gates must have at least 2 input buses, received " + numInputBuses);
        }
    }


    //*>> -------------------- Logic/getType implementations -------------------- */
    // Only methods that needed to be implemented

    /**
     * {@inheritDoc}
     *
     * <p>A xor-gate returns true if there are an odd number of trues
     */
    @Override
    protected boolean gateLogic(int numTrues) {
        return (numTrues % 2) == 1;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
