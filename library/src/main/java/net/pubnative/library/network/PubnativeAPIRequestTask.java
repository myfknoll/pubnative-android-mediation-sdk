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

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;

public class PubnativeAPIRequestTask {

    private static String TAG = PubnativeAPIRequestTask.class.getSimpleName();
    //PubnativeAPIRequest
    private PubnativeAPIRequest mPubnativeAPIRequest;
    //PubnativeAPIRequest runnable
    private Runnable            mExecuteRequestRunnable;
    private Executor            mResponsePoster;

    //==============================================================================================
    // Public
    //==============================================================================================
    public Runnable getExecuteRequestRunnable() {

        Log.v(TAG, "getExecuteRequestRunnable");
        if (mExecuteRequestRunnable == null) {
            mExecuteRequestRunnable = new Runnable() {

                @Override
                public void run() {

                    executeRequest();
                }
            };
        }
        return mExecuteRequestRunnable;
    }

    public void setRequest(PubnativeAPIRequest pubnativeAPIRequest) {

        Log.v(TAG, "setRequest");
        mPubnativeAPIRequest = pubnativeAPIRequest;
    }

    public void setResponsePoster(Executor responsePoster) {

        Log.v(TAG, "setResponsePoster");
        mResponsePoster = responsePoster;
    }

    //==============================================================================================
    // Private
    //==============================================================================================
    private void executeRequest() {

        Log.v(TAG, "executeRequest");
        final PubnativeAPIResponse pubnativeAPIResponse = new PubnativeAPIResponse();
        try {
            URL requestURL = new URL(mPubnativeAPIRequest.getUrl());
            HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();
            connection.setConnectTimeout(PubnativeAPIRequest.TIME_OUT);
            connection.setReadTimeout(PubnativeAPIRequest.TIME_OUT);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                pubnativeAPIResponse.setResult(connection.getInputStream());
            } else {
                throw new Exception("Server error: " + responseCode);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            pubnativeAPIResponse.setError(e);
        } finally {
            mResponsePoster.execute(new Runnable() {

                @Override
                public void run() {

                    processResponse(pubnativeAPIResponse);
                }
            });
        }
    }

    private void processResponse(PubnativeAPIResponse pubnativeAPIResponse) {

        Log.v(TAG, "processResponse");
        if (pubnativeAPIResponse.isSuccess()) {
            mPubnativeAPIRequest.invokeOnResponse(pubnativeAPIResponse.getResult());
        } else {
            mPubnativeAPIRequest.invokeOnError(pubnativeAPIResponse.getError());
        }
        PubnativeAPIRequestManager.recycleTask(this);
    }
}
