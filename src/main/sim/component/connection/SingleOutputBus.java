package sim.component.connection;

import sim.component.StatusCodesSys;
import sim.component.System;


/**
 * A common interface for all devices that only have a single output bus. The interface provides default
 * implementations for the output bus's alias to/from Index conversions as well as a unified alias for the output
 * bus
 */
public interface SingleOutputBus extends System {

    /**
     * Alias for the output bus, defining it in the interface unifies it across all implementors,
     * simplifying design and making textual representations easier to understand as the alias is consistent.
     */
    public final static String OUT_BUS_ALIAS = "OUT";

    /**
     * {@inheritDoc}
     */ //TODO status error code
    @Override
    default int outAliasToIndex(String alias) {
        if (alias.equals(SingleOutputBus.OUT_BUS_ALIAS)) {
            return 0;
        }

        try {
            if (Integer.parseInt(alias) == 0) {
                return 0;
            }
        }
        catch (NumberFormatException e) {}

        throw StatusCodesSys.runtimeException(StatusCodesSys.UNKNOWN_OUTPUT_ALIAS_TO_INDEX, alias);
    }

    /**
     * {@inheritDoc}
     */ //TODO status error code
    @Override
    default String outIndexToAlias(int index) {
        if (index == 0) {
            return SingleOutputBus.OUT_BUS_ALIAS;
        }

        throw StatusCodesSys.runtimeException(StatusCodesSys.UNKNOWN_OUTPUT_INDEX_TO_ALIAS, index);
    }
}
