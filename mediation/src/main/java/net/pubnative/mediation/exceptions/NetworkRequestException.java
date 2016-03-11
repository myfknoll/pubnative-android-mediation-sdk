package net.pubnative.mediation.exceptions;

public class NetworkRequestException extends Exception {

    public enum EXCEPTION_TYPE {
        INVALID_PARAMETERS {
            @Override
            String getReadableName() {
                return "Invalid start parameters";
            }
        },
        NULL_INVALID_CONFIG {
            @Override
            String getReadableName() {
                return "Null or invalid config";
            }
        },
        PLACEMENT_NOT_FOUND {
            @Override
            String getReadableName() {
                return "Placement(%s) not found";
            }
        },
        NO_ELEMENT_FOR_PLACEMENT {
            @Override
            String getReadableName() {
                return "Retrieved config contains null element for placement(%s)";
            }
        },
        DISABLED_PLACEMENT {
            @Override
            String getReadableName() {
                return "Placement(%s) is disabled";
            }
        },
        NO_NETWORK_FOR_PLACEMENT {
            @Override
            String getReadableName() {
                return "No network is configured for placement(%s)";
            }
        },
        FREQUENCY_CAP {
            @Override
            String getReadableName() {
                return "(frequency_cap) too many ads";
            }
        },
        PACING_CAP {
            @Override
            String getReadableName() {
                return "(pacing_cap) too many ads";
            }
        },
        NO_FILL {
            @Override
            String getReadableName() {
                return "No fill available for %s";
            }
        };

        abstract String getReadableName();
    }

    private EXCEPTION_TYPE mExceptionType;
    private String mExceptionValue;

    public NetworkRequestException(EXCEPTION_TYPE exception_type) {
        this(exception_type, null, exception_type.getReadableName());
    }

    public NetworkRequestException(EXCEPTION_TYPE exception_type, String exceptionValue) {
        this(exception_type, exceptionValue, String.format(exception_type.getReadableName(), exceptionValue));
    }

    public NetworkRequestException(EXCEPTION_TYPE exception_type, String exceptionValue, String exceptionMessage) {
        super(exceptionMessage);
        mExceptionType = exception_type;
        mExceptionValue = exceptionValue;
    }

    public NetworkRequestException(EXCEPTION_TYPE exception_type, String exceptionValue, String exceptionMessage, Throwable cause) {
        super(exceptionMessage, cause);
        mExceptionType = exception_type;
        mExceptionValue = exceptionValue;
    }

    @Override
    public String toString() {
        return "NetworkRequestException{" +
                String.format(mExceptionType.getReadableName(), mExceptionValue) +
                ", message=" + getMessage();
    }
}
