package sim.component;

import java.util.HashMap;
import java.util.IllegalFormatConversionException;
import java.util.Map;

import sim.exception.Status;
import sim.exception.StatusRuntimeException;

public final class StatusCodesSys {

    private static final Map<Integer, String> detailTemplate = new HashMap<>();


    //region; status codes
    public static final int UNKNOWN_INPUT_ALIAS_TO_INDEX = 310;
    public static final int UNKNOWN_INPUT_INDEX_TO_ALIAS = 311;
    public static final int UNKNOWN_OUTPUT_ALIAS_TO_INDEX = 312;
    public static final int UNKNOWN_OUTPUT_INDEX_TO_ALIAS = 313;

    public static final int EXISTING_CONNECTION = 320;
    public static final int BUS_DNE = 321;
    //endregion


    static {
        detailTemplate.put(UNKNOWN_INPUT_ALIAS_TO_INDEX, "Unknown ");
    }

    public static Status message(int code, Object... o) {
        String detail;

        try {
            detail = String.format(StatusCodesSys.detailTemplate.get(code), o);
        }
        catch (NullPointerException e) {
            detail = "Tried generating status-sys-message with code " + code + " but this code is not recognized.";
            code = Status.UNKNOWN_STATUS_CODE;
        }
        catch (IllegalFormatConversionException e) {
            detail = "Tried generating status-sys-message with code " + code + " but the object array contains unexpected elements or "
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

    private StatusCodesSys() {}


}
