package sim.component.gates;

/**
 * A Buffer-Gate is a logical gate with 1 input and the output always has the same output of the input.
 */
public final class BufferGate extends Gate {

    public static final String TYPE = "Buffer-Gate";


    //*>> -------------------- Constructor -------------------- */

    /**
     * Construct a Buffer-Gate with an output initialized to the first (and only element) in
     * {@code initialOutputState}. Note this array must have size 1.
     *
     * @param initialOutputState the initial output state, must either be null or a boolean array of length 1
     * @throws IllegalArgumentException if {@code initialOutputState != null && length(initialOutputState) != 1}
     */
    public BufferGate(boolean[] initialOutputState) {
        super(TYPE, 1, initialOutputState);
    }

    /**
     * Construct a Buffer-Gate with an output initialized to false
     */
    public BufferGate() {
        super(TYPE, 1);
    }


    //*>> -------------------- Logic/getType implementations -------------------- */
    // Only methods that needed to be implemented

    /**
     * {@inheritDoc}
     *
     * <p>A buffer-gate's bus reflects the input
     */
    @Override
    protected boolean gateLogic(int numTrues) {
        return numTrues == 1;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
