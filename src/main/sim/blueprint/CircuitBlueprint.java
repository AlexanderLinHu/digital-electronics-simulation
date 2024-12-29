package sim.blueprint;

import java.util.List;

import sim.component.Circuit;
import sim.component.connection.OutputPointer;

public final class CircuitBlueprint extends Blueprint {

    private final String type;
    private final String[] inputPinAlias;
    private final String[] outputPinAlias;

    private final List<InternalSysReference> InternalSystems;
    private final List<InternalConnection> internalConnections;

    public CircuitBlueprint(Circuit circuit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Circuit build() {
        throw new UnsupportedOperationException("Blueprint.build() is not implemented");
    }

    @Override
    public String getType() {
        throw new UnsupportedOperationException("Blueprint.getName() is not implemented");
    }

    private static class InternalSysReference {
        /** Blueprint name */
        final String type;

        /** Alias given to instantiated system */
        final String alias;

        InternalSysReference(String type, String alias) {
            this.type = type;
            this.alias = alias;
        }
    }

    private static class InternalConnection {
        final String target;
        final OutputPointer[] connections;

        InternalConnection(String target, OutputPointer[] connections) {
            this.target = target;
            this.connections = connections;
        }
    }
}
