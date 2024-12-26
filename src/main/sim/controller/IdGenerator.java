package sim.controller;

public final class IdGenerator {

    private static int id = 0;

    public static int get() {
        return id++;
    }

    private IdGenerator() {}
}
