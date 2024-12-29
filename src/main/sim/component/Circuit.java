package sim.component;

import java.util.Arrays;
import java.util.HashSet;

import sim.adt.BiMap;
import sim.blueprint.Blueprint;
import sim.blueprint.CircuitBlueprint;
import sim.component.connection.OutputPointer;
import sim.component.gates.BufferGate;
import sim.controller.IdGenerator;
import sim.exception.StatusRuntimeException;

/**
 * A circuit is a composite system. Unlike devices it has an internal state (beyond the values of IO buses) and its
 * logic cannot be hardcoded. Instead, circuits are composed of other circuits and devices
 */
public class Circuit implements System {


    /**
     * The input bus of the circuit, each {@code BufferGate} represents an input bus to the circuit. Where the buffer
     * gate's input is from an external (including this circuit's outputs) system, and its outputs are read by
     * internal systems
     */
    protected final BufferGate[] inputBus;

    /**
     * The output bus of the circuit, each {@code BufferGate} represents an output bus from the circuit. Where the
     * buffer gate's input is from an internal system, and its outputs are read by external systems
     */
    protected final BufferGate[] outputBus;

    /** A boolean indicating whether the input buffer has been loaded but not used */
    protected boolean inputBufferLoaded;

    /** A bidirectional map between the alias and index for the input buses */
    protected final BiMap<Integer, String> inputAlias;

    /** A bidirectional map between the alias and index for the output buses */
    protected final BiMap<Integer, String> outputAlias;

    /** Unique integer to identify the circuit amongst other systems */
    protected final int id;

    /** The type of circuit this is */
    protected final String type;

    protected boolean isRegistered;

    /**
     * Set of all internal systems with at least one input value that has changed values from the last
     * update call (aka set of unstable systems).
     *
     * <p>All systems in this set and input buses are updated at every update call. Input buses must always
     * be updated because we cannot guarantee they are stable. Note the parent circuit can prove whether this
     * circuit's input buses are stable, along with the return value of {@link #update} it can deduce
     * whether this system's update function even needs to be called.
     */
    // protected final HashSet<System> unstableSystems;


    //*>> ----------------------------------------------------------------------------------------------------------- <<*//
    //*>>                                                 Constructor                                                 <<*//
    //*>> ----------------------------------------------------------------------------------------------------------- <<*//


    // public static Circuit produceFromBlueprint(String type) {
    //     return blueprints.get(type).build();
    // }

    // public static void recordCircuit(Circuit circuit) {
    //     blueprints.put(circuit.getType(), CircuitBlueprint.from(circuit));
    // }

    /**
     * Create a new circuit type, with nothing internally (asides from input and output busses, disconnected from each other).
     *
     * <p>If you wish to register the circuit as a blueprint, the type must be unique from all registered system blueprints
     * and non-null.
     *
     * <p>The default value of the initial input and output buses is all {@code false}.
     * The default for an alias array is an array where the ith index contains the {@code String} representation of i. I.e. {@code ["0", "1", ...]}
     *
     * <p>Bus aliases allows the user to assign names to each bus. Each alias must be unique for their type of bus
     * (all input bus aliases must be unique from each other, likewise for output bus aliases). The index of each alias
     * is the bus the alias will be assigned to. So input alias "J" at index 0 means the bus at index 0 now has
     * the alias "J".
     * The length of each array must be equal to the given number of the specified bus.
     *
     * @param type the type of circuit
     * @param numInputBus the number of input buses on the circuit. Must be non-negative
     * @param numOutputBus the number of output buses on the circuit. Must be non-negative
     * @param initialOutputState the initial output values of the output buses
     * @param inputPinAlias an array of aliases where alias X at index i creates the bi-directional mapping {@code X <=> inputBus[i]}. All
     *                      entries must be unique.
     * @param outputPinAlias an array of aliases where alias X at index i creates the bi-directional mapping {@code X <=> outputBus[i]}. All
     *                      entries must be unique.
     * @throws StatusRuntimeException with status codes, <ul>
     *  <li>{@link StatusCodesSys#INCONSISTENT_BUS_COUNT} if there is inconsistency between expected number of IO busses and received
     *              (through {@code initialOutputState} or alias arrays)
     *  <li>{@link StatusCodesSys#DUPLICATE_INPUT_ALIAS} if the input alias array contains duplicates
     *  <li>{@link StatusCodesSys#DUPLICATE_OUTPUT_ALIAS} if the output alias array contains duplicates
     * </ul>
     * @see Blueprint
     * @see Blueprint#build(String)
     */
    public Circuit(String type, int numInputBus, int numOutputBus, boolean[] initialOutputState, String[] inputPinAlias, String[] outputPinAlias) {

        this.type = type;
        this.isRegistered = false;

        //>> Input Validation, ensure number of expected buses are consistent

        if (initialOutputState != null && numOutputBus != initialOutputState.length) {
            throw StatusCodesSys.runtimeException(StatusCodesSys.INCONSISTENT_BUS_COUNT, numOutputBus, initialOutputState.length);
        }
        if (inputPinAlias != null && numInputBus != inputPinAlias.length) {
            throw StatusCodesSys.runtimeException(StatusCodesSys.INCONSISTENT_BUS_COUNT, numInputBus, inputPinAlias.length);
        }
        if (outputPinAlias != null && numOutputBus != outputPinAlias.length) {
            throw StatusCodesSys.runtimeException(StatusCodesSys.INCONSISTENT_BUS_COUNT, numOutputBus, outputPinAlias.length);
        }

        //>> Initialize fields

        this.inputBus = new BufferGate[numInputBus];
        this.outputBus = new BufferGate[numOutputBus];
        this.inputBufferLoaded = false;
        this.inputAlias = new BiMap<Integer, String>();
        this.outputAlias = new BiMap<Integer, String>();
        this.id = IdGenerator.get();

        //>> Set Aliases

        if (inputPinAlias == null) {
            inputPinAlias = generateDefaultPinAliases(numInputBus);
        }
        else if ((new HashSet<>(Arrays.asList(inputPinAlias))).size() < inputPinAlias.length) {
            throw StatusCodesSys.runtimeException(StatusCodesSys.DUPLICATE_INPUT_ALIAS, type, (Object[]) inputPinAlias);
        }

        if (outputPinAlias == null) {
            outputPinAlias = generateDefaultPinAliases(numOutputBus);
        }
        else if ((new HashSet<>(Arrays.asList(outputPinAlias))).size() < outputPinAlias.length) {
            throw StatusCodesSys.runtimeException(StatusCodesSys.DUPLICATE_OUTPUT_ALIAS, type, (Object[]) outputPinAlias);
        }

        for (int i = 0; i < inputPinAlias.length; i++) {
            this.inputAlias.put(i, inputPinAlias[i]);
        }
        for (int i = 0; i < outputPinAlias.length; i++) {
            this.inputAlias.put(i, outputPinAlias[i]);
        }

        //>> Create input buffer gates

        for (int i = 0; i < numInputBus; i++) {
            this.inputBus[i] = new BufferGate();
        }

        //>> Create output buffer gates and set initial state

        if (initialOutputState == null) {
            initialOutputState = new boolean[numOutputBus];
        }

        for (int i = 0; i < numOutputBus; i++) {
            this.outputBus[i] = new BufferGate(new boolean[] {initialOutputState[i]});
        }
    }


    /**
     * Generate an array representing the default pin aliases, which is the {@code String} representation of the
     * index in the array + 1. For example, given 5 pins the resulting array will be {@code ["1", "2", "3", "4", "5"]}
     *
     * @param numberOfPins a positive integer
     * @return the default pin alias array
     */
    public static final String[] generateDefaultPinAliases(int numberOfPins) {
        String[] defaultPinAlias = new String[numberOfPins];
        for(int i = 1; i < numberOfPins + 1; i++) {
            defaultPinAlias[i] = Integer.toString(i);
        }
        return defaultPinAlias;
    }


    //*>> ----------------------------------------------------------------------------------------------------------- <<*//
    //*>>                                            Blueprint Management                                             <<*//
    //*>> ----------------------------------------------------------------------------------------------------------- <<*//

    public void register() {
        Blueprint.register(new CircuitBlueprint(this));
        this.isRegistered = true;
    }


    //*>> ----------------------------------------------------------------------------------------------------------- <<*//
    //*>>                                               Connect systems                                               <<*//
    //*>> ----------------------------------------------------------------------------------------------------------- <<*//


    /**
     * {@inheritDoc}
     *
     * <p>Note this method cannot be used to connect an internal system to the circuit's IO buses, if this is the
     * objective then check the {@linkplain #connectInternalBus connect internal bus} method
     *
     * @throws BusDneException {@inheritDoc}
     * @throws ExistingConnectionException {@inheritDoc}
     * @see #connectInternalBus
     */
    @Override
    public void connectInput(System fromSystem, String fromBus, String toBus) {
        inputBus[this.inAliasToIndex(toBus)].connectInput(fromSystem, fromBus, "0");
    }

    /**
     * Establish a unidirectional connection between an internal system and a circuit's IO bus.
     *
     * <p>This method is meant for internal systems that need to communicate with the circuit's IO buses as it would
     * otherwise be impossible to make such a connection since the IO buses are never shared outside of the instance.
     * As such, it is required to pass in the calling circuit as one of the system parameters.
     * <ul>
     *  <li> If this circuit is passed as the source system then the source bus will reference an input bus' output
     *  <li> If this circuit is passed as the target system then the target bus will reference an output bus' input
     * </ul>
     *
     * <p>Note since the other system is an internal system of this circuit, it should not be used in this method
     * with any other circuit aside from this one.
     *
     * @param sourceSystem  the device containing the {@code sourceBus}
     * @param sourceBus     alias of the output bus from which the signal originates from
     * @param targetSystem  the device containing the {@code targetBus}
     * @param targetBus     alias of the input bus to receive the signal
     * @throws BusDneException if either the {@code sourceBus} or {@code targetBus} is not recognized
     * @throws IllegalArgumentException if one of the given buses do not exist or the circuit is not one of the
     *                                  system input
     * @throws ExistingConnectionException if the receiving bus has an existing connection
     * @see #connectInput
     */
    public void connectInternalBus(System sourceSystem, String sourceBus,
                                   System targetSystem, String targetBus) {
        if (isRegistered) {
            //TODO error
        }

        // To make a connection from a circuit IO bus to a system, the circuit must be passed as one of the devices
        if (!((sourceSystem == this) ^ (targetSystem == this))) {
            throw new IllegalArgumentException(
                "When connecting a circuit's IO bus with an internal system, the circuit itself must be declared " +
                "as exactly one of the source system or the target system."
            );
        }

        // Connecting input of internal-device to output of specified circuit's input-bus
        if (sourceSystem == this) {
            int sourceBusConverted = this.inAliasToIndex(sourceBus);
            targetSystem.connectInput(this.inputBus[sourceBusConverted], "0", targetBus);
        }
        // Connecting specified input of circuit's output-bus to output of specified internal-device
        else {
            int targetBusConverted = this.outAliasToIndex(targetBus);
            this.outputBus[targetBusConverted].connectInput(sourceSystem, sourceBus, "0");
        }
    }

    //*>> ----------------------------------------------------------------------------------------------------------- <<*//
    //*>>                                              Update Operations                                              <<*//
    //*>> ----------------------------------------------------------------------------------------------------------- <<*//

    @Override
    public boolean update() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public void loadInputBuffer() {
        for (System inputPin : inputBus) {
            inputPin.update();
        }
        inputBufferLoaded = true;
    }


    //*>> ----------------------------------------------------------------------------------------------------------- <<*//
    //*>>                                         Index and Alias Conversion                                          <<*//
    //*>> ----------------------------------------------------------------------------------------------------------- <<*//

    /**
     * {@inheritDoc}
     * @throws StatusRuntimeException {@inheritDoc}
     */
    @Override
    public int inAliasToIndex(String alias) {
        Integer index = inputAlias.getKey(alias);
        if (index != null) {
            return index;
        }

        throw StatusCodesSys.runtimeException(StatusCodesSys.UNKNOWN_INPUT_ALIAS, alias);
    }

    /**
     * {@inheritDoc}
     * @throws StatusRuntimeException {@inheritDoc}
     */
    @Override
    public String inIndexToAlias(int index) {
        String alias = inputAlias.getValue(index);
        if (alias != null) {
            return alias;
        }

        throw StatusCodesSys.runtimeException(StatusCodesSys.UNKNOWN_INPUT_INDEX, index);
    }

    /**
     * {@inheritDoc}
     * @throws StatusRuntimeException {@inheritDoc}
     */
    @Override
    public int outAliasToIndex(String alias) {
        Integer index = outputAlias.getKey(alias);
        if (index != null) {
            return index;
        }

        throw StatusCodesSys.runtimeException(StatusCodesSys.UNKNOWN_OUTPUT_ALIAS, alias);
    }

    /**
     * {@inheritDoc}
     * @throws StatusRuntimeException {@inheritDoc}
     */
    @Override
    public String outIndexToAlias(int index) {
        String alias = outputAlias.getValue(index);
        if (alias != null) {
            return alias;
        }

        throw StatusCodesSys.runtimeException(StatusCodesSys.UNKNOWN_OUTPUT_INDEX, index);
    }

    //*>> ----------------------------------------------------------------------------------------------------------- <<*//
    //*>>                                               Get Operations                                                <<*//
    //*>> ----------------------------------------------------------------------------------------------------------- <<*//

    @Override
    public boolean[] getOut() {
        boolean[] out = new boolean[outputBus.length];

        for (int i = 0; i < out.length; i++) {
            out[i] = outputBus[i].getOut(0);
        }

        return out;
    }

    @Override
    public boolean getOut(String bus) {
        return outputBus[outAliasToIndex(bus)].getOut(0);

    }

    @Override
    public boolean getOut(int bus) {
        return outputBus[bus].getOut(0);
    }

    public OutputPointer[] getInputBus() {
        OutputPointer[] inputs = new OutputPointer[inputBus.length];

        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = inputBus[i].getInputBus()[0];
        }

        return inputs;
    }

    public int getID() {
        return id;
    }

    public String getType() {
        return type;
    }
}
