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
  * [Confirm ads impression](#usage_predefined)
	* [(optional) Track ads behaviour](#usage_track_model)

* [Misc](#misc)
  * [Dependencies](#misc_dependencies)
  * [License](#misc_license)
  * [Contributing](#misc_contributing)

<a name="requirements"></a>
# Requirements

* Android API 10 (Gingerbread 2.3.3+)
* An App Token provided in PubNative Dashboard.

<a name="install"></a>
# Install

Clone the repository and import the `:mediation` project into your app. Allow also to import the `:library` one.

<a name="usage"></a>
# Usage

PubNative mediation is a lean yet complete project that allow you request ads from different networks with remote control from the PubNative Dashboard.

Basic integration steps are:

1. [Request ads](#usage_request): Using `PubnativeNetworkRequest` and `PubnativeNetworkRequestListener`
2. [Confirm ads impression](#usage_confirm_impression): Using the returned `PubnativeAdModel` callbacks with `PubnativeAdModelListener`.
3. [(optional) Track ads behaviour](#usage_track_model): Using the returned `PubnativeAdModel` callbacks with `PubnativeAdModelListener`.

<a name="usage_request"></a>
### 1) Request Ads

In order to request an Ad you need to create a request, fill it with your data and start it providing a callback for the ad response.

You can set up several data before starting the request by using the helper `PubnativeNetworkRequest` methods. This is an optional usage but in the long therm will seriously improve your ad placement behaviour.

Here is a sample on how to use It.

```java
PubnativeNetworkRequest request = new PubnativeNetworkRequest();
request.start(context, "--YOUR_APP_TOKEN_HERE-", "--YOUR_PLACEMENT_ID_HERE--", new PubnativeNetworkRequestListener()
{
	@Override
    public void onRequestStarted(PubnativeNetworkRequest request)
    {
        // Request started
    }

    @Override
    public void onRequestLoaded(PubnativeNetworkRequest request, PubnativeAdModel ad)
    {
        // Requested ad returned
				// Set your ad view with `ad`
    }

    @Override
    public void onRequestFailed(PubnativeNetworkRequest request, Exception exception)
    {
        // Request failed
    }
});
```

<a name="usage_confirm_impression"></a>
### 2) Confirm ad impression

For confirming impressions, the `PubnativeadModel` has methods to track the ad view and confirm the impression automatically, just specify the view that contains the ad to the `startTracking` method.

```java
ad.startTracking(<CONTEXT>, <AD_VIEW_CONTAINER>);
```

<a name="usage_track_model"></a>
### 3) (optional) Tracking ads behaviour

In order to track the ad behaviour, you can set up a callback listener inside the model so you can know when has it confirmed an impression or detected a click.

This step is fully optional, just in case you want to play/stop any service upon ad click or any interaction with the ad.

```java
ad.setListener(new PubnativeAdModelListener()
{
	@Override
  public void onAdImpressionConfirmed(PubnativeAdModel model)
  {
		// Impression of the ad was confirmed
  }

	@Override
  public void onAdClick(PubnativeAdModel model)
  {
		// The ad was clicked
		// It will probably get out of the screen right after this.
  }
});
```

<a name="misc"></a>
# Misc

<a name="misc_dependencies"></a>
### Dependencies

There are no further dependencies than the networks SDK's that this mediation layer is adapting.

<a name="misc_license"></a>
### License

This code is distributed under the terms and conditions of the MIT license.

<a name="misc_contributing"></a>
### Contributing

**NB!** If you fix a bug you discovered or have development ideas, feel free to make a pull request.
