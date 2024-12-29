package sim.compiler;

import java.util.ArrayList;
import java.util.List;

public abstract class CompilerPass {

    protected int numErrors = 0;
    protected final List<String> errorMessages = new ArrayList<String>();


    protected void error(String message) {
        errorMessages.add(message);
        numErrors++;
    }

    public final int getNumErrors() { return numErrors; }

    public final boolean hasErrors() { return numErrors > 0; }

    public final List<String> getErrorMessages() {
        return new ArrayList<>(errorMessages);
    }
}