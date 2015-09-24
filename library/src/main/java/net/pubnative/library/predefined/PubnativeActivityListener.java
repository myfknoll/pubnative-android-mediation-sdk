package net.pubnative.library.predefined;

public interface PubnativeActivityListener
{
    void onPubnativeActivityStarted(String identifier);

    void onPubnativeActivityFailed(String identifier, Exception exception);

    void onPubnativeActivityOpened(String identifier);

    void onPubnativeActivityClosed(String identifier);
}
