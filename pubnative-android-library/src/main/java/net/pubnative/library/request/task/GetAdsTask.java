/**
 * Copyright 2014 PubNative GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.pubnative.library.request.task;

import java.util.ArrayList;

import net.pubnative.library.PubnativeContract;
import net.pubnative.library.PubnativeContract.Response;
import net.pubnative.library.model.NativeAdModel;
import net.pubnative.library.model.VideoAdModel;
import net.pubnative.library.request.AdRequest;

import org.droidparts.concurrent.task.AsyncTaskResultListener;
import org.droidparts.concurrent.task.SimpleAsyncTask;
import org.droidparts.persist.serializer.JSONSerializer;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

public class GetAdsTask extends
        SimpleAsyncTask<ArrayList<? extends NativeAdModel>>
{
    private final AdRequest adRequest;

    public GetAdsTask(Context context, AdRequest adRequest, AsyncTaskResultListener<ArrayList<? extends NativeAdModel>> resultListener)
    {
        super(context, resultListener);
        this.adRequest = adRequest;
    }

    @Override
    protected ArrayList<? extends NativeAdModel> onExecute() throws Exception
    {
        ArrayList<? extends NativeAdModel> result = null;
        JSONObject jsonObject = new GetAdsJSONTask(getContext(), this.adRequest).onExecute();
        if (jsonObject.get(Response.STATUS).equals(Response.STATUS_ok))
        {
            JSONArray jsonArray = jsonObject.getJSONArray(PubnativeContract.Response.ADS);
            JSONSerializer<?> serializer = null;
            switch (adRequest.getEndpoint())
            {
                case NATIVE:
                    serializer = new JSONSerializer<>(NativeAdModel.class, getContext());
                    break;
                case VIDEO:
                    serializer = new JSONSerializer<>(VideoAdModel.class, getContext());
                    break;
            }
            if (serializer != null)
            {
                result = (ArrayList<? extends NativeAdModel>) serializer.deserializeAll(jsonArray);
            }
        }
        else
        {
            String error_message = (String) jsonObject.get(Response.ERROR_MESSAGE);
            throw new Exception(error_message);
        }
        return result;
    }
}
