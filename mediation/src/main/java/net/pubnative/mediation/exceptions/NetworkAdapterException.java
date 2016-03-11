package net.pubnative.mediation.exceptions;

public class NetworkAdapterException extends Exception {

    //==============================================================================================
    // Public
    //==============================================================================================
    public enum NETWORK {
        FACEBOOK,
        YAHOO,
        PUBNATIVE
    }

    public String getNetwork() {
        return mNetwork.name();
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    private NETWORK mNetwork;
    private Integer mErrorCode;

    public NetworkAdapterException(NETWORK network, String exceptionMessage) {
        this(network, null, exceptionMessage);
    }

    public NetworkAdapterException(NETWORK network, Integer errorCode, String exceptionMessage) {
        super(exceptionMessage);

        mNetwork = network;
        mErrorCode = errorCode;
    }

    public NetworkAdapterException(NETWORK network, String exceptionMessage, Throwable cause) {
        this(network, null, exceptionMessage, cause);
    }

    public NetworkAdapterException(NETWORK network, Integer errorCode, String exceptionMessage, Throwable cause) {
        super(exceptionMessage, cause);

        mNetwork = network;
        mErrorCode = errorCode;
    }

    @Override
    public String toString() {
        return "NetworkAdapterException{" +
                "network=" + getNetwork() +
                ", errorCode=" + mErrorCode +
                ", exception=" + getMessage() +
                '}';
    }
}
