package sim.component.gates;

/**
 * Provides the not-variation of a logic gate. It copies the current configuration of the supplied {@code gate}
 * instance. The user should use the returned gate instance instead of the original.
 *
 * <p>You can apply the inverter multiple times, but since applying it twice will result in the same logic as no
 * inverter, there isn't a reason to do so.
 */
public final class InverterGate extends Gate {

    /** The gate whose logic to invert */
    private final Gate gate;

    /** The type of inverted logic gate. Unlike the other gates this value cannot be inferred statically */
    private final String type;


    //*>> -------------------- Constructor -------------------- */

    /**
     * Constructs the not-variation of the provided logic gate, as well as copying the gate's current state.
     *
     * @param gate the gate to copy state from
     */
    public InverterGate(Gate gate) {
        super(switch (gate) {
                case AndGate g      -> {yield "Nand-Gate";}
                case OrGate g       -> {yield "Nor-Gate";}
                case XorGate g      -> {yield "Xnor-Gate";}
                case BufferGate g   -> {yield "Not-Gate";}
                case InverterGate g -> {yield g.gate.getType();}
            }, gate.getInputBus().length, gate.getOut()
        );
        this.gate = gate;

        type = getType();
    }


    //*>> -------------------- Logic/getType implementations -------------------- */
    // Only methods that needed to be implemented

    /**
     * {@inheritDoc}
     *
     * <p>A negated version of the stored gate's logic
     */
    @Override
    protected boolean gateLogic(int numTrues) {
        return !this.gate.gateLogic(numTrues);
    }

    @Override
    public String getType() {
        return type;
    }
}
