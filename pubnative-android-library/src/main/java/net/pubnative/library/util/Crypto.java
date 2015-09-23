package net.pubnative.library.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class Crypto
{
    public static String sha1(String string)
    {
        String result = "";
        StringBuilder stringBuilder = new StringBuilder();
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = string.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();
            for (final byte b : bytes)
            {
                stringBuilder.append(String.format("%02X", b));
            }
            result = stringBuilder.toString().toLowerCase(Locale.US);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static String md5(String s)
    {
        String result = "";
        try
        {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
            {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            result = hexString.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return result;
    }
}
