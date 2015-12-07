// The MIT License (MIT)
//
// Copyright (c) 2015 PubNative GmbH
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

package net.pubnative.mediation.request;

import net.pubnative.mediation.request.model.PubnativeAdModel;

public interface PubnativeNetworkRequestListener {

    /**
     * Invoked when ad request starts with valid params
     *
     * @param request Object used to make the ad request.
     */
    void onRequestStarted(PubnativeNetworkRequest request);

    /**
     * Invoked when ad request returns valid ads.
     *
     * @param request Object used to make the ad request.
     * @param ad      Loaded ad model.
     */
    void onRequestLoaded(PubnativeNetworkRequest request, PubnativeAdModel ad);

    /**
     * Invoked when ad request fails or when no ad is retrieved.
     *
     * @param request   Object used to make the ad request.
     * @param exception Exception with proper message of request failure.
     */
    void onRequestFailed(PubnativeNetworkRequest request, Exception exception);
}
