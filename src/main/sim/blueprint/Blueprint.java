package sim.blueprint;

import java.util.HashMap;

import sim.component.System;

public abstract sealed class Blueprint permits DeviceBlueprint, CircuitBlueprint, ClockBlueprint {

    /**
     * Maps {@code types ==> Blueprint}. This allows the user to store designs and mass produce any system type
     * that has been saved, without the need to redefine everything about the system.
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

    public static boolean hasType(String type) {
        return blueprints.containsKey(type);
    }

    public static System build(String name) {
        try {
            return blueprints.get(name).build();
        }
        catch (NullPointerException e) {
            throw StatusCodesBlp.runtimeException(StatusCodesBlp.BLUEPRINT_TYPE_NOT_RECOGNIZED, name);
        }
    }


    public abstract System build();
    public abstract String getType();
}
