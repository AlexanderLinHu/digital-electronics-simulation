package sim.blueprint;

import java.util.HashMap;
import java.util.IllegalFormatConversionException;
import java.util.Map;

import sim.exception.Status;
import sim.exception.StatusRuntimeException;

public final class StatusCodesBlp {
    private static final Map<Integer, String> detailTemplate = new HashMap<>();


    //region; status codes
    public static final int BLUEPRINT_TYPE_ALREADY_REGISTERED = 332;
    public static final int BLUEPRINT_TYPE_NOT_RECOGNIZED = 334;
    //endregion

    //TODO add messages 
    static {

    }

    public static Status message(int code, Object... o) {
        String detail;

        try {
            detail = String.format(StatusCodesBlp.detailTemplate.get(code), o);
        }
        catch (NullPointerException e) {
            detail = "Tried generating status-blp-message with code " + code + " but this code is not recognized.";
            code = Status.UNKNOWN_STATUS_CODE;
        }
        catch (IllegalFormatConversionException e) {
            detail = "Tried generating status-blp-message with code " + code + " but the object array contains unexpected elements or "
                    + "is in the wrong order.";
            code = Status.INCORRECT_FORMAT_OBJECTS;
        }

        return new Status(Status.Location.SYS, code, detail);
    }

    public static StatusRuntimeException runtimeException(int code, Object... o) {
        return runtimeException(code, null, o);
    }

    public static StatusRuntimeException runtimeException(int code, Throwable cause, Object... o) {
        return new StatusRuntimeException(message(code, o), cause);
    }

    private StatusCodesBlp() {}
}