package sim.blueprint;

import java.util.HashMap;

import sim.component.System;

/**
 * Blueprints store the internal configurations of systems, allowing mass creations of any system type
 * that has been saved, without the need to redefine everything about the system.
 *
 * <p>For circuits, this includes all internal systems and connections between each other and the circuit's IO busses.
 *
 * <p>No 2 blueprints may have the same name (aka type).
 */
public abstract sealed class Blueprint permits DeviceBlueprint, CircuitBlueprint, ClockBlueprint {

    /**
     * Maps {@code types ==> Blueprint}
     */
    protected static final HashMap<String, Blueprint> blueprints = new HashMap<>();

    public static void register(Blueprint blueprint) {
        if (blueprint == null) {
            throw new NullPointerException("Cannot register a null blueprint");
        }
        if (blueprints.containsKey(blueprint.getType())) {
            throw StatusCodesBlp.runtimeException(StatusCodesBlp.BLUEPRINT_TYPE_ALREADY_REGISTERED, blueprint.getType());
        }
        blueprints.put(blueprint.getType(), blueprint);
    }

    /**
     * Check if the given type has an associated blueprint.
     *
     * @param type the type to check its existence
     * @return {@code true} if provided type has an associated blueprint, otherwise {@code false}
     */
    public static boolean hasType(String type) {
        return blueprints.containsKey(type);
    }

    /**
     * Create a new system of the given type and return it.
     *
     * <p>The system returned is a {@code Circuit} if the given type is associated with a {@link CircuitBlueprint}, otherwise
     * a {@code Device} is returned
     *
     * @param type the type of system to build
     * @throws StatusRuntimeException with status codes, <ul>
     *  <li>{@link StatusCodesBlp#BLUEPRINT_TYPE_NOT_RECOGNIZED} if the given is not recognized
     *       (and thus has no associated blueprint to build from)
     * @return the newly created system
     */
    public static System build(String type) {
        try {
            return blueprints.get(type).build();
        }
        catch (NullPointerException e) {
            throw StatusCodesBlp.runtimeException(StatusCodesBlp.BLUEPRINT_TYPE_NOT_RECOGNIZED, type);
        }
    }


    /**
     * Construct the system stored in this blueprint
     *
     * @return the newly created system
     */ //TODO can we just type match in `static build` and delegate to corresponding constructor?
    public abstract System build();

    /**
     * Get the type of system stored in the blueprint
     *
     * @return the type of the system stored in the blueprint
     */
    public abstract String getType();
}
