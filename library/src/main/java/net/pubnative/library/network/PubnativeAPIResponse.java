// The MIT License (MIT)
//
// Copyright (c) 2016 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package net.pubnative.library.network;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class is used as a data holder for the PubnativeAPIRequestManager response callback
 */
public class PubnativeAPIResponse {

    private static String TAG = PubnativeAPIResponse.class.getSimpleName();
    private String    mResult;
    private Exception mError;

    //==============================================================================================
    // Getters and setters
    //==============================================================================================
    public String getResult() {

        Log.v(TAG, "getResult");
        return mResult;
    }

    public void setResult(String result) {

        Log.v(TAG, "setResult(String)");
        mResult = result;
    }

    public void setResult(InputStream is) {

        Log.v(TAG, "setResult(InputStream)");
        BufferedInputStream inputStream = new BufferedInputStream(is);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            int result = inputStream.read();
            while (result != -1) {
                byte byteResult = (byte) result;
                outputStream.write(byteResult);
                result = inputStream.read();
            }
            setResult(outputStream.toString());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            mError = e;
        }
    }

    public Exception getError() {

        Log.v(TAG, "getError");
        return mError;
    }

    public void setError(Exception error) {

        Log.v(TAG, "setError");
        mError = error;
    }

    public boolean isSuccess() {

        Log.v(TAG, "isSuccess");
        return mError == null;
    }
}
