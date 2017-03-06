package com.adam.suixinplayer.util;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/3/3.
 */

public interface BitmapCallback  {
    /**
     * 当图片加载完成后，回调此方法
     * @param bitmap
     */
    void onBitmapLoaded(Bitmap bitmap);
}
