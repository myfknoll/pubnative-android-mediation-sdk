package net.pubnative.mediation.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by davidmartin on 10/08/15.
 */
public class LoadImageAsyncTask extends AsyncTask<Object, String, Bitmap>
{
    private Bitmap    bitmap    = null;
    private ImageView imageView = null;

    @Override
    protected Bitmap doInBackground(Object... args)
    {
        try
        {
            if(args != null && args.length == 2)
            {
                String imageURL = (String) args[0];
                this.imageView = (ImageView) args[1];
                if (this.imageView != null && !TextUtils.isEmpty(imageURL))
                {
                    this.bitmap = BitmapFactory.decodeStream((InputStream) new URL(imageURL).getContent());
                }
            }
        }
        catch (Exception e)
        {
            // Bitmap exception, do nothing
        }
        return this.bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap image)
    {
        if (image != null && this.imageView != null)
        {
            this.imageView.setImageBitmap(image);
        }
    }
}
