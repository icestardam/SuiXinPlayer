package com.adam.suixinplayer.model;

import com.adam.suixinplayer.entity.Music;

import java.util.List;

/**
 * Created by Administrator on 2017/3/1.
 */
public interface MusicListCallback {
    /**
     * 当音乐列表加载完成后返回会调用的回调方法
     * @param musics
     */
    void onMusicLoaded(List<Music>musics);

}
