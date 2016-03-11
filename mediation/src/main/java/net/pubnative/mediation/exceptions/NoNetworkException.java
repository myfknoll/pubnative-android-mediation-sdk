package net.pubnative.mediation.exceptions;

public class NoNetworkException extends Exception {

    public NoNetworkException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public NoNetworkException(String exceptionMessage, Throwable cause) {
        super(exceptionMessage, cause);
    }
}
