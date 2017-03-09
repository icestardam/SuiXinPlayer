package com.adam.suixinplayer.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.adam.suixinplayer.R;
import com.adam.suixinplayer.adapter.MusicAdapter;
import com.adam.suixinplayer.app.MusicApplication;
import com.adam.suixinplayer.entity.Music;
import com.adam.suixinplayer.entity.SongInfo;
import com.adam.suixinplayer.entity.SongUrl;
import com.adam.suixinplayer.model.MusicListCallback;
import com.adam.suixinplayer.model.MusicModel;
import com.adam.suixinplayer.model.SongInfoCallback;
import com.adam.suixinplayer.service.PlayMusicService;

import java.util.List;

/**
 * Created by Administrator on 2017/2/28.
 */
public class NewMusicListFragment extends Fragment {
    private ListView listView;
    private MusicModel musicModel;
    private List<Music> musics;
    private MusicAdapter adapter ;

    private PlayMusicService.MusicBinder binder ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_list, null);
        initViews(view);
        musicModel = new MusicModel();
        musicModel.loadNewMusicList(0,20,new MusicListCallback(){

            @Override
            public void onMusicLoaded(List<Music> musics) {
                NewMusicListFragment.this.musics = musics;
                setAdapter();
            }
        });
        setListener();
        return view;
    }
    //添加监听器
    private void setListener() {
        //设置滚动listview时的监听器
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            boolean isBottom = false;
            boolean requesting =false;//避免重复加载

            @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.i("info", "onScrollStateChanged "+isBottom);
                    switch (scrollState) {
                        case SCROLL_STATE_IDLE:
                            if (isBottom && !requesting) {
                                requesting = true;
                                int offset = NewMusicListFragment.this.musics.size();
                                musicModel.loadNewMusicList(offset, 20, new MusicListCallback() {
                                    @Override
                                    public void onMusicLoaded(List<Music> musics) {
                                        if (musics == null || musics.isEmpty()) {//服务端没有数据了
                                            Toast.makeText(getActivity(), "没有数据了", Toast.LENGTH_LONG).show();
                                            requesting = false;
                                            return;

                                        }
                                        NewMusicListFragment.this.musics.addAll(musics);
                                        adapter.notifyDataSetChanged();
                                        requesting= false;
                                    }
                                });
                            }
                            break;
                        case SCROLL_STATE_FLING:
                            break;
                        case SCROLL_STATE_TOUCH_SCROLL:
                            break;
                    }
                }
            @Override
            public void onScroll(AbsListView view,
                                 int firstVisibleItem,//第一个可见控件的position
                                 int visibleItemCount,//可见控件的数量
                                 int totalItemCount) {
                Log.i("info", "onScroll "+isBottom);
                if (firstVisibleItem + visibleItemCount == totalItemCount&&totalItemCount>0) {
                    isBottom=true;
                }else {
                    isBottom= false;
                }

            }


            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //将musics 和 position存入app中
                    MusicApplication application =MusicApplication.getApp();
                application.setMusics(musics);
                application.setPosition(position);

                final Music music = musics.get(position);
                String songId = music.getSong_id();
                musicModel.loadSongInfoBySongId(songId, new SongInfoCallback() {
                    //请求完毕后，返回歌曲的信息
                    @Override
                    public void onSongInfoLoaded(List<SongUrl> urls, SongInfo songInfo) {
                        Log.i("info", urls.toString()+"\n-------------------------\n"+songInfo.toString());
                        //将urls和SongInfo存入music中
                        music.setUrls(urls);
                        music.setSongInfo(songInfo);
                        String url = urls.get(0).getFile_link();
                        binder.playMusic(url);
                    }

                });
            }
        });
    }


    private void initViews(View view) {
        listView = (ListView) view.findViewById(R.id.listview);
    }

    @Override
    public void onDestroy() {
        //将adapter中的线程关掉
        adapter.stopThread();
        super.onDestroy();
    }
    private void setAdapter() {
        adapter = new MusicAdapter(musics,getActivity(),listView);
        listView.setAdapter(adapter);

    }

    public void setBinder(PlayMusicService.MusicBinder binder) {
        this.binder = binder;
    }
}
