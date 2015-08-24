package net.pubnative.mediation.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by davidmartin on 28/07/15.
 */
public class PubnativeStringUtils
{
    public static String readStringFromInputStream(InputStream inputStream)
    {
        BufferedReader bufferReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try
        {
            String line;
            bufferReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferReader.readLine()) != null)
            {
                stringBuilder.append(line);
            }
        }
        catch (IOException e)
        {
            System.out.println("PubnativeStringUtils.readTextFile - ERROR: " + e);
        }
        finally
        {
            if (bufferReader != null)
            {
                try
                {
                    bufferReader.close();
                }
                catch (IOException e)
                {
                    System.out.println("PubnativeStringUtils.readTextFile - ERROR: " + e);
                }
            }
        }
        return stringBuilder.toString();
    }
}
