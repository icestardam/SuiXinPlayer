package com.adam.suixinplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.adam.suixinplayer.util.GlobalConsts;

import java.io.IOException;

/**
 * Created by Administrator on 2017/3/3.
 */
public class PlayMusicService extends Service{
    private MediaPlayer player =new MediaPlayer();
    public static boolean isLoop = true;

    @Override
    public void onCreate() {
        super.onCreate();
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                player.start();
                //当音乐开始的时候，开始发广播
                Intent intent = new Intent(GlobalConsts.ACTION_MUSIC_STARTED);
                sendBroadcast(intent);
            }
        });
        //在service的onCreate中建立一个工作线程用于更新音乐进度
        //要保证run方法可以执行完毕，防止内存泄露
        new Thread(){
            @Override
            public void run() {
                while (isLoop) {
                    try {
                        Thread.sleep(1000);
                        //当音乐正在播放时，发广播
                        if (player.isPlaying()) {
                            int total = player.getDuration();
                            int currentTime = player.getCurrentPosition();
                            Intent intent = new Intent(GlobalConsts.ACTION_UPDATE_MUSIC_PROCESS);
                            intent.putExtra(GlobalConsts.EXTRA_MUSIC_TOTAL_TIME, total);
                            intent.putExtra(GlobalConsts.EXTRA_MUSIC_CURRENT_TIME, currentTime);
                            sendBroadcast(intent);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {

        player.release();

        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    public class MusicBinder extends Binder {
        /**
         * 将播放进度条移到相应位置
         * @param position  移动后的位置
         */
        public void seekTo(int position) {
            player.seekTo(position);
        }
        /**
         * 播放音乐的接口方法
         * @param url
         */
        public  void playMusic(String url){

            try {
                player.reset();
                player.setDataSource(url);

                //异步准备
                player.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void playOrPause() {
            if (player.isPlaying()) {
                player.pause();
            }else{
                player.start();
            }
        }



    }
    public void stopThread() {
        isLoop=false;
    }
}
