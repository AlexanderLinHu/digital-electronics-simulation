package sim.exception;

public class StatusException extends Exception {

    private Status status;

    public StatusException(Status status) {
        this.status = status;
    }

    public StatusException(Status status, Throwable cause) {
        super(cause);
        this.status = status;
    }

    @Override
    public String getMessage() {
        return status.detail;
    }
}
