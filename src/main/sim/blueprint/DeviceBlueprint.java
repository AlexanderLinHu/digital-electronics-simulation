package sim.blueprint;

import sim.component.Circuit;

public final class DeviceBlueprint extends Blueprint {

    private final String name;
    private final String[] inputPinAlias;
    private final String[] outputPinAlias;

    public DeviceBlueprint() {
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
}
