package sim.component;

// import java.util.ArrayList;

import sim.component.connection.OutputPointer;
import sim.exception.StatusRuntimeException;

/**
 * Common interface all systems must implement, providing methods to interface with other systems
 *
 * <h2>Methods</h2>
 * There are 5 types of methods in the interface, methods are always split into 1 of 5 categories and follow
 * the same order as seen in this interface, allowing for easy navigation of the code. Furthermore, some documentation
 * may refer to these sections instead of listing their methods. Implementors are free to introduce new methods
 * and, to the fullest extent possible, be placed (and documented) in the most appropriate section.
 * <ul>
 *  <li>
 *      <b>Connect Systems:</b> Establish connections with other systems
 *  </li>
 *  <li>
 *      <b>Update Operations:</b> Methods that deal with updating the state of the system
 *  </li>
 *  <li>
 *      <b>Validation:</b> Methods that deal with validating the system
 *  </li>
 *  <li>
 *      <b>Index and Alias Conversion:</b> Various methods to convert bus alias to/from indices
 *  </li>
 *  <li>
 *      <b>Get Operations:</b> Various methods to get certain values
 *  </li>
 * </ul>
 *
 * <p>Unless otherwise specified, {@code null} is not a valid argument to any method and likely {@code NullPointerException} will be thrown
 * if {@code null} is passed in.
 */
public interface System {

    public static final String DEFAULT_SYSTEM_NAME = "__noName";


    //*>> ----------------------------------------------------------------------------------------------------------- <<*//
    //*>>                                               Connect systems                                               <<*//
    //*>> ----------------------------------------------------------------------------------------------------------- <<*//

    /**
     * Establish a unidirectional connection originating from the specified external bus to the specified bus on this system.
     *
     * <p>Some systems may not have input buses in which case this method will throw an {@code UnsupportedOperationException}.
     * Systems are assumed to support inputs, it must be explicitly documented when they do not.
     *
     * @param  fromSystem  the system containing the {@code fromBus}
     * @param  fromBus alias of the output bus from which the signal originates from
     * @param  toBus   alias of the input bus to receive the signal
     * @throws UnsupportedOperationException if this system does not have input buses
     * @throws StatusRuntimeException with status codes, <ul>
     *  <li>{@value StatusCodesSys#EXISTING_CONNECTION} if {@code toBus} is already connected
     *  <li>{@value StatusCodesSys#BUS_DNE} if at least one of the specified buses does not exist
     * </ul>
     */
    void connectInput(System fromSystem, String fromBus, String toBus);


    //*>> ----------------------------------------------------------------------------------------------------------- <<*//
    //*>>                                              Update Operations                                              <<*//
    //*>> ----------------------------------------------------------------------------------------------------------- <<*//

    /**
     * Update the state of the system.
     *
     * <p>When the method returns, the values of the output bus values accurately reflect the expected values based on the input bus values.
     * Additionally, if this is a circuit, the internal state is also updated. If an input buffer is loaded, use those values.
     *
     * <p>A system should rarely ever call its own update method, as it can't "see" the bigger picture so doesn't know whether it's inputs are
     * "ready". In most cases, the method should called by an {@link sim.component.update.UpdateStrategy} object since they know the order to call
     * update methods.
     *
     * <p>The method returns true if no internal system has changed states, this allows the caller to know whether
     * there are still signal changes being propagated internally or whether it is stable and there is no need
     * to update the system without changes to its input. As devices do not have internal states, they will always
     * return {@code true}
     *
     * @return {@code true} if internal state is stable (no internal systems changed state, including output busses). {@code false} otherwise
     * @throws NullPointerException if not all input buses have been connected
     */
    boolean update();

    /**
     * Read and store the value(s) from the input buses, but does not update the state of the system.
     *
     * <p>This is necessary for circuits where multiple systems need to be updated simultaneously. The method
     * creates a snapshot capturing the current state of the input buses. This way, changes made in other systems'
     * will not be reflected when {@code update()} is called, giving the appearance systems were simultaneously updated
     *
     * <p>Similar to {@link #update()} this method is primarily for the managing {@code Circuit} class.
     *
     * <p>While it is possible to call {@code loadInputBuffer} any number of times before calling {@code update},
     * there isn't really a reason to do this.
     *
     * @throws NullPointerException if not all input buses have been connected
     */
    void loadInputBuffer();


    //*>> ----------------------------------------------------------------------------------------------------------- <<*//
    //*>>                                                 Validation                                                  <<*//
    //*>> ----------------------------------------------------------------------------------------------------------- <<*//

    /**
     * Validate the system is correctly setup. This method also validates internal system(s) (likewise it must
     * propagate internal failed-validation-reports).
     *
     * (details unknown atm)
     */
    // ArrayList<FailedValidationReport> validate(); //TODO: Figure out how to implement validation (visitor?)


    //*>> ----------------------------------------------------------------------------------------------------------- <<*//
    //*>>                                         Index and Alias Conversion                                          <<*//
    //*>> ----------------------------------------------------------------------------------------------------------- <<*//

    /**
     * Converts an input {@code String} alias to the index of the bus. Conversion is case-insensitive.
     *
     * @param alias the alias to convert to index
     * @return int index of the bus
     * @throws StatusRuntimeException with status codes, <ul>
     *  <li>{@value StatusCodesSys#UNKNOWN_INPUT_ALIAS_TO_INDEX} if alias is not recognized
     */
    int inAliasToIndex(String alias);

    /**
     * Converts the index of an input bus and returns the alias (or the {@code String} of the index if no alias exists)
     *
     * @param index the index to convert to its alias
     * @return {@code String} alias of the index (if no alias exists, returns the provided index as a string)
     * @throws StatusRuntimeException with status codes, <ul>
     *  <li>{@value StatusCodesSys#UNKNOWN_INPUT_INDEX_TO_ALIAS} if index is negative or greater than number of inputs
     */
    String inIndexToAlias(int index);


    /**
     * Converts an output {@code String} alias to the index of the bus. Conversion is case-insensitive.
     *
     * @param alias the alias to convert to index
     * @return int index of the bus
     * @throws StatusRuntimeException with status codes, <ul>
     *  <li>{@value StatusCodesSys#UNKNOWN_OUTPUT_ALIAS_TO_INDEX} if alias is not recognized
     */
    int outAliasToIndex(String alias);

    /**
     * Converts the index of an output bus and returns the alias (or the {@code String} of the index if no alias exists)
     *
     * @param index the index to convert to its alias
     * @return {@code String} alias of the index (if no alias exists, returns the provided index as a string)
     * @throws StatusRuntimeException with status codes, <ul>
     *  <li>{@value StatusCodesSys#UNKNOWN_OUTPUT_INDEX_TO_ALIAS} if index is negative or greater than number of outputs
     */
    String outIndexToAlias(int index);


    //*>> ----------------------------------------------------------------------------------------------------------- <<*//
    //*>>                                               Get Operations                                                <<*//
    //*>> ----------------------------------------------------------------------------------------------------------- <<*//

    /**
     * Get the value of all output buses for the system.
     *
     * <p>The resulting array will always follow the indexing obtained from {@link #getOut(int)}. More formally,
     * {@code for all integers i s.t. 0 <= i < numOutputBuses, getOut()[i] == getOut(i)}.
     *
     * @return the values of all output buses for this system
     */
    boolean[] getOut();

    /**
     * Get the value of a specific output bus for this system.
     *
     * @param bus the output bus to read from
     * @return a {@code boolean} indicating the value of the output bus
     * @throws StatusRuntimeException see {@link #outAliasToIndex(String)}
     */
    boolean getOut(String bus);

    /**
     * Get the value of a specific output bus for this system. This method assumes the provided bus exists.
     *
     * @param bus the output bus to read from
     * @return a {@code boolean} indicating the value of the output bus
     * @throws ArrayIndexOutOfBoundsException if index is not valid
     */
    boolean getOut(int bus);

    /**
     * Get all {@link OutputPointer} to the input of this system.
     *
     * <p>The resulting array's indices matches the input-index. So if a connection is made to bus "0" then the
     * first index of the returned array will be that connection.
     *
     * <p>This array will contain {@code null} values if and only if there are input buses that are not connected
     * when this method is called.
     *
     * @return an ordered array representing the input bus
     */
    OutputPointer[] getInputBus();

    /**
     * Get the id of this system.
     *
     * @return the system's id
     */
    int getID();

    /**
     * A type is an identifier for the component. 2 systems with the same type have the same functionality, just like
     * how 2 systems with the same identifier are the same system
     *
     * @return the name of the system
     */
    String getType();
}
