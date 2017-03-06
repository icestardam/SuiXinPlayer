package com.adam.suixinplayer.model;

import com.adam.suixinplayer.entity.SongInfo;
import com.adam.suixinplayer.entity.SongUrl;

import java.util.List;

/**
 * Created by Administrator on 2017/3/2.
 */
public interface SongInfoCallback {
    /**
     * 当音乐信息加载完毕后调用的回调方法
     * @param urls
     * @param songInfo
     */
    void onSongInfoLoaded(List<SongUrl>urls , SongInfo songInfo);
}
