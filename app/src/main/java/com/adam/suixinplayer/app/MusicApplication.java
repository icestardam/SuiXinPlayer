package com.adam.suixinplayer.app;

import android.app.Application;

import com.adam.suixinplayer.entity.Music;

import java.util.List;

/**
 * Created by Administrator on 2017/3/3.
 */

public class MusicApplication extends Application {
    private static MusicApplication application ;
    private List<Music> musics;
    private int position;

    @Override
    public void onCreate() {
        super.onCreate();
        application=this;
    }

    public static MusicApplication getApp(){
        return application;
    }
    public void setMusics(List<Music>musics){
        this.musics = musics;
    }
    public void setPosition(int position){
        this.position = position;
    }

    /**
     * 获取当前正在播放的音乐
     * @return  m 音乐类
     */
    public Music getCurrentMusic() {
        Music m = musics.get(position);
        return  m;
    }
    /**
     * 换到上一首歌
     */
    public void preMusic() {
        position = position == 0 ? musics.size() - 1 : position - 1;
    }

    /**
     * 换到下一首歌
     */
    public void nextMusic() {
        position = position == musics.size() - 1 ? 0 : position + 1;
    }
}
