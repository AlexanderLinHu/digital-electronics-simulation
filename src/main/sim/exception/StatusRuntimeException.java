package sim.exception;

public class StatusRuntimeException extends RuntimeException {
    private final Status status;

    public StatusRuntimeException(Status status) {
        this.status = status;
    }

    public StatusRuntimeException(Status status, Throwable cause) {
        super(cause);
        this.status = status;
    }

    public int getCode() {
        return status.code;
    }

    @Override
    public String getMessage() {
        return status.detail;
    }
}
