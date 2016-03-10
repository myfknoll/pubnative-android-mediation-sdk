![ScreenShot](PNLogo.png)

PubNative is an API-based publisher platform dedicated to native advertising which does not require the integration of an Library.

Through PubNative, publishers can request over 20 parameters to enrich their ads and thereby create any number of combinations for unique and truly native ad units.

# pubnative-android-mediation-sdk

pubnative-android-mediation-sdk is an Open Source client mediation layer for integrating multiple ad networks inside your app with remote control using the PubNative Dashboard.

## Contents

* [Requirements](#requirements)
* [Install](#install)
* [Usage](#usage)
  * [Request ads](#usage_request)
  * [Track ad](#usage_track_ad)

* [Misc](#misc)
  * [License](#misc_license)
  * [Contributing](#misc_contributing)

<a name="requirements"></a>
# Requirements

* Android API 10 (Gingerbread 2.3.3+)
* An App Token provided in PubNative Dashboard.
* A Placement ID that'

<a name="install"></a>
# Install

Clone the repository and import the `:mediation` project into your app.

<a name="usage"></a>
# Usage

PubNative mediation is a lean yet complete project that allow you request ads from different networks with remote control from the PubNative Dashboard.

Basic integration steps are:

1. [Request ads](#usage_request): Using `PubnativeNetworkRequest`
3. [Track ad](#usage_track_ad): Using the returned `PubnativeAdModel`

<a name="usage_request"></a>
### 1) Request Ads

In order to request an Ad you need to create a request, fill it with your data and start it providing a callback for the ad response.

You can set up several data before starting the request by using the helper `PubnativeNetworkRequest` methods. This is an optional usage but in the long term will seriously improve your ad placement behaviour.

Here is a sample on how to use It.

```java
PubnativeNetworkRequest request = new PubnativeNetworkRequest();
request.start(context, "<APP_TOKEN>", "<PLACEMENT_ID>", new PubnativeNetworkRequest.Listener()
{
	@Override
    public void onPubnativeNetworkRequestStarted(PubnativeNetworkRequest request) {
    {
        // Request started
    }

    @Override
    public void onPubnativeNetworkRequestLoaded(PubnativeNetworkRequest request, PubnativeAdModel ad) {
    {
        // Requested ad returned
    }

    @Override
    public void onPubnativeNetworkRequestFailed(PubnativeNetworkRequest request, Exception exception) {
    {
        // Request failed
    }
});
```

<a name="usage_track_ad"></a>
### 2) Track ad

For confirming impressions and handling clicks, the `PubnativeadModel` has methods to automatically track the ad view and confirm the impression and to handle the click and open the offer, just specify the view that contains the ad to the `startTracking` method.

```java
ad.startTracking(<CONTEXT>, <AD_VIEW>);
```

Optionally, you can set up a listener on the model to listen for callbacks on the tracking process.

```java
ad.setListener(new PubnativeAdModel.Listener() {

    @Override
    public void onAdImpressionConfirmed(PubnativeAdModel model) {
        // Called when the ad impression was confirmed
    }
    
    @Override
    public void onAdClick(PubnativeAdModel model) {
        // Called when the ad was clicked
    }
});
```

<a name="misc"></a>
# Misc

<a name="misc_license"></a>
### License

This code is distributed under the terms and conditions of the MIT license.

<a name="misc_contributing"></a>
### Contributing

**NB!** If you fix a bug you discovered or have development ideas, feel free to make a pull request.
