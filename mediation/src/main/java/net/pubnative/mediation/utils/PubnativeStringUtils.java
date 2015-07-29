package net.pubnative.mediation.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by davidmartin on 28/07/15.
 */
public class PubnativeStringUtils
{
    public static String readTextFromInputStream(InputStream inputStream)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try
        {
            while ((len = inputStream.read(buf)) != -1)
            {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        }
        catch (IOException e)
        {
            System.out.println("PubnativeStringUtils.readTextFile - ERROR: " + e);
        }
        return outputStream.toString();
    }
}
