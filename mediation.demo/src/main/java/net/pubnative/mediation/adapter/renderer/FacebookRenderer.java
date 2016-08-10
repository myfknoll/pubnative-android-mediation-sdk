package net.pubnative.mediation.adapter.renderer;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.facebook.ads.MediaView;

import net.pubnative.mediation.adapter.model.FacebookNativeAdModel;

import java.util.WeakHashMap;

public class FacebookRenderer implements PubnativeRenderer<FacebookNativeAdModel> {

    private static final String TAG = FacebookRenderer.class.getSimpleName();

    private WeakHashMap<View, MediaViewHolder> mViewHolder;

    public FacebookRenderer() {
        mViewHolder = new WeakHashMap<>();
    }

    @Override
    public View createView(Context context, ViewGroup parent) {

        Log.v(TAG, "FacebookRenderer - createView");
        RelativeLayout container = new RelativeLayout(context);
        ViewGroup.LayoutParams params = parent.getLayoutParams();
        parent.addView(container, params);
        MediaView.LayoutParams mediaParams = new MediaView.LayoutParams(params.width, params.height);

        if (params instanceof ViewGroup.MarginLayoutParams) {
            final ViewGroup.MarginLayoutParams marginParams =
                    (ViewGroup.MarginLayoutParams) params;
            mediaParams.setMargins(marginParams.leftMargin,
                                             marginParams.topMargin,
                                             marginParams.rightMargin,
                                             marginParams.bottomMargin);
        }

        if (params instanceof RelativeLayout.LayoutParams) {
            final RelativeLayout.LayoutParams relativeParams =
                    (RelativeLayout.LayoutParams) params;
            final int[] rules = relativeParams.getRules();
            for (int i = 0; i < rules.length; i++) {
                mediaParams.addRule(i, rules[i]);
            }
            container.setVisibility(View.INVISIBLE);
        }

        MediaView mediaView = new MediaView(context);
        int containerIndex = parent.indexOfChild(container);
        parent.addView(mediaView, containerIndex + 1, mediaParams);

        return container;
    }

    @Override
    public void renderView(View view, FacebookNativeAdModel adModel) {

        Log.d(TAG, "FacebookRenderer - renderView");
        MediaViewHolder viewHolder = mViewHolder.get(view);
        if (viewHolder == null) {
            viewHolder = MediaViewHolder.prepareView(view);
            mViewHolder.put(view, viewHolder);
        }

        MediaView mediaView = viewHolder.getMediaView();
        if (mediaView != null) {
            adModel.updateMediaView(mediaView);
            mediaView.setVisibility(View.VISIBLE);
            view.setVisibility(View.INVISIBLE);
        }


    }

    static class MediaViewHolder {

        private MediaView        mMediaView;

        private MediaViewHolder(MediaView mediaView) {

            mMediaView = mediaView;
        }

        static MediaViewHolder prepareView(View view) {
            MediaView mediaView = null;
            if (view != null) {
                ViewGroup parent = (ViewGroup) view.getParent();
                int mainViewIndex = parent.indexOfChild(view);
                View viewBelow = parent.getChildAt(mainViewIndex + 1);
                if (viewBelow instanceof MediaView) {
                    mediaView = (MediaView) viewBelow;
                }
            }
            return new MediaViewHolder(mediaView);
        }

        public MediaView getMediaView() {

            return mMediaView;
        }

    }
}
