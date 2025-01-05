package sim.compiler;

/**
 * Simple class to represent the line and column of a "thing" in a file. These can be obtained respectively
 * through the public final fields. The {@code toString()} method is a simple pretty print of {@code line:column}
 */
public final class Position {

    public final int line;
    public final int column;

    public Position(int line, int column) {
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return line + ":" + column;
    }
}