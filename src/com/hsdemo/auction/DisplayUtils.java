package com.hsdemo.auction;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.LruCache;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by jtsuji on 11/14/14.
 */
public class DisplayUtils {
  static Typeface pictos;
  static ImageLoader imageLoader;
  static RequestQueue queue;

  public static void init(Context context) {
    queue = Volley.newRequestQueue(context);
    imageLoader = new ImageLoader(queue, new ImageLoader.ImageCache() {
      private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
      public void putBitmap(String url, Bitmap bitmap) {
        mCache.put(url, bitmap);
      }
      public Bitmap getBitmap(String url) {
        return mCache.get(url);
      }
    });
  }

  public static Typeface getPictosTypeface(Context context) {
    if (pictos == null)
      pictos = Typeface.createFromAsset(context.getAssets(), "fonts/HSMobilePictos.ttf");

    return pictos;
  }
  public static int toPx(int dp) {
    return (int) ((dp * Resources.getSystem().getDisplayMetrics().density) + 0.5);
  }

  public static void pictosifyTextView(TextView textView) {
    textView.setTypeface(getPictosTypeface(textView.getContext()));
  }
}
