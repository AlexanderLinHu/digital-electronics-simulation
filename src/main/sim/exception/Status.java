package sim.exception;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * The status of an operation is represented by the type, code, and an optional message. The type is represented
 * by {@link Status.Location} indicating where the status message comes from. The integer code specifies
 * the type of status (success, specify exception type, etc). The message provides more information on the status.
 *
 * <p>This class defines codes used globally and are all {@code <100}. Negative codes represents errors due to the programmer.
 * Thus they should never appear in the normal running of the program.
 *
 * <p>All codes 100+ have different meaning defined by their location.
 */
public final class Status {

    public static final int IMPOSSIBLE = -99;
    public static final int INCORRECT_FORMAT_OBJECTS = -3;
    public static final int UNKNOWN_STATUS_CODE = -2;
    public static final int UNKNOWN_ERROR = -1;
    public static final int OK = 0;
    public static final int INFO = 1;
    public static final int CARRIER = 2;
    public static final int WAITING_USER_INPUT = 10;
    public static final int UNEXPECTED_NEGATIVE_VALUE = 22;

    public enum Location {

        /** System */
        SYS,
        /** Compiler */
        CPL,
        /** Controller */
        CTR,
        /** Blueprint */
        BLP,
        /** Unknown */
        UNK,
        /** Other */
        OTH
    }

    public final Location location;
    public final int code;
    public final String detail;
    public final String time;

    public Status(Location location, int code, String detail) {
        this.location = location;
        this.code = code;
        this.detail = detail;
        this.time = LocalTime.now().truncatedTo(ChronoUnit.MILLIS).toString();
    }

    public Status(Location type, int code) {
        this(type, code, "");
    }


    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Status)) {
            return false;
        }

        Status otherStatus = (Status) other;
        return (location == otherStatus.location) && (code == otherStatus.code);
    }
}
