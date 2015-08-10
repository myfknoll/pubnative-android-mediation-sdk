package net.pubnative.library.predefined;

import java.util.ArrayList;

public class PubnativeActivityDelegateManager
{
    private static PubnativeActivityDelegateManager instance;
    private ArrayList<PubnativeActivityDelegate>    delegates = new ArrayList<PubnativeActivityDelegate>();

    private PubnativeActivityDelegateManager()
    {
    }

    private static PubnativeActivityDelegateManager getInstance()
    {
        if (instance == null)
        {
            instance = new PubnativeActivityDelegateManager();
        }
        return instance;
    }

    public static void addDelegate(PubnativeActivityDelegate delegate)
    {
        if (!PubnativeActivityDelegateManager.getInstance().delegates.contains(delegate))
        {
            PubnativeActivityDelegateManager.getInstance().delegates.add(delegate);
        }
    }

    public static void removeDelegate(PubnativeActivityDelegate delegate)
    {
        PubnativeActivityDelegateManager.getInstance().delegates.remove(delegate);
    }
}
