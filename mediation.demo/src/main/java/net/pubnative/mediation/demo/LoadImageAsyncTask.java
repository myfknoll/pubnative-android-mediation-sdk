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

package net.pubnative.mediation.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

public class LoadImageAsyncTask extends AsyncTask<Object, String, Bitmap> {

    private Bitmap    bitmap    = null;
    private ImageView imageView = null;

    @Override
    protected Bitmap doInBackground(Object... args) {
        try {
            if (args != null && args.length == 2) {
                String imageURL = (String) args[0];
                this.imageView = (ImageView) args[1];
                if (this.imageView != null && !TextUtils.isEmpty(imageURL)) {
                    this.bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageURL).getContent());
                }
            }
        } catch (Exception e) {
            // Bitmap exception, do nothing
        }
        return this.bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap image) {
        if (image != null && this.imageView != null) {
            this.imageView.setImageBitmap(image);
        }
    }
}
