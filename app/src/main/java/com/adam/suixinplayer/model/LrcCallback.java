package com.adam.suixinplayer.model;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/3/4.
 */

public interface LrcCallback {
    /**
     * 回调方法 当歌词下载完成后回调该方法
     * @param lrc
     */
    public void onLrcLoaded(HashMap<String, String> lrc);
}
