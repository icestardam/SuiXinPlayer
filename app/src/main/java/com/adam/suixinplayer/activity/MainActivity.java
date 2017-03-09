package com.adam.suixinplayer.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adam.suixinplayer.R;
import com.adam.suixinplayer.adapter.SearchMusicAdapter;
import com.adam.suixinplayer.app.MusicApplication;
import com.adam.suixinplayer.entity.Music;
import com.adam.suixinplayer.entity.SongInfo;
import com.adam.suixinplayer.entity.SongUrl;
import com.adam.suixinplayer.fragment.HotMusicListFragment;
import com.adam.suixinplayer.fragment.NewMusicListFragment;
import com.adam.suixinplayer.model.LrcCallback;
import com.adam.suixinplayer.model.MusicListCallback;
import com.adam.suixinplayer.model.MusicModel;
import com.adam.suixinplayer.model.SongInfoCallback;
import com.adam.suixinplayer.service.PlayMusicService;
import com.adam.suixinplayer.ui.CircleImageView;
import com.adam.suixinplayer.util.BitmapCallback;
import com.adam.suixinplayer.util.BitmapUtils;
import com.adam.suixinplayer.util.DateUtils;
import com.adam.suixinplayer.util.GlobalConsts;
import com.adam.suixinplayer.util.UrlFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends FragmentActivity{
    private ViewPager viewPager;
    private RadioGroup radioGroup;
    private RadioButton rbNewMusic;
    private RadioButton rbHotMusic;

    public List<Fragment> fragments;

    private ServiceConnection conn;

    private MusicInfoReceiver receiver;
    private CircleImageView civBBPic;
    private TextView tvBBtitle;
    //播放界面控件
    private RelativeLayout rlPlayMusic;
    private TextView tvPMTotalTime,tvPMCurrentTime,tvPMSong,tvPMSinger,tvPMLrc;
    private SeekBar seekBar ;
    private ImageView ivPMBackground,ivPMAlbum;

    private PlayMusicService.MusicBinder binder;
    private MusicModel model;


    private PlayMusicService service = new PlayMusicService();

    //搜索界面控件
    private Button btnToSearch,btnSearching,btnCancel;
    private EditText etSearch;
    private RelativeLayout rlSearch;
    private ListView lvSearchResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        model= new MusicModel();
        //初始化控件
        initViews();
        //初始化ViewPager的adapter
        setPagerAdapter();
        //监听
        setListeners();
        //绑定音乐service
        bindMusicService();
        //注册组件
        registerComponent();

    }
    @Override
    protected void onDestroy() {

        //解绑service
        this.unbindService(conn);
        //注销receiver
        this.unregisterReceiver(receiver);

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //当点击返回键时：
        //如果是在音乐播放界面，则将播放界面隐藏
        if (rlPlayMusic.getVisibility() == View.VISIBLE) {
            rlPlayMusic.setVisibility(View.INVISIBLE);
            ScaleAnimation anim = new ScaleAnimation(1, 0, 1, 0, 0, rlPlayMusic.getHeight());
            anim.setDuration(300);
            rlPlayMusic.startAnimation(anim);
        } else if (rlSearch.getVisibility() == View.VISIBLE) {//如果是在搜索界面，则将播放界面隐藏
            rlSearch.setVisibility(View.INVISIBLE);
        } else {//不在播放界面，则按照返回键的正常规则返回
            //断开service中的thread 避免出现illegalStateException
            service.stopThread();
            super.onBackPressed();
        }
    }

    /**
     * 注册组件
     */
    private void registerComponent() {
        receiver = new MusicInfoReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalConsts.ACTION_MUSIC_STARTED);
        filter.addAction(GlobalConsts.ACTION_UPDATE_MUSIC_PROCESS);
        this.registerReceiver(receiver, filter);
    }



    /**
     * 绑定音乐服务
     */
    private void bindMusicService() {
        Intent intent = new Intent(this, PlayMusicService.class);
        conn=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //将binder传给fragment
                binder = (PlayMusicService.MusicBinder) service;
                NewMusicListFragment f1 = (NewMusicListFragment) fragments.get(0);
                f1.setBinder(binder);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        this.bindService(intent,conn, Service.BIND_AUTO_CREATE);
    }


    /**
     * 设置监听器
     */
    private void setListeners() {
        //为搜索界面的搜索按钮设置监听器
        btnSearching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = etSearch.getText().toString();
                if (keyword == null || keyword.equals("")) {
                    Toast.makeText(MainActivity.this, "请输入关键词", Toast.LENGTH_SHORT).show();
                    return;
                }

                model.searchMusicList(keyword, new MusicListCallback() {
                    @Override
                    public void onMusicLoaded(List<Music> musics) {
                        SearchMusicAdapter adapter = new SearchMusicAdapter(MainActivity.this,musics);
                        lvSearchResult.setAdapter(adapter);
                    }
                });
            }
        });
        //为搜索按钮设置监听器，跳转到搜索界面
        btnToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlSearch.setVisibility(View.VISIBLE);

            }
        });
        //为取消按钮设置监听器
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlSearch.setVisibility(View.INVISIBLE);
            }
        });
        //给relativeLayout设置onTouch，拦截事件
        rlPlayMusic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //消费点击事件
                return true;
            }
        });
        //设置用户拖动seekbar的监听器
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {//判断是否为用户拖动
                    binder.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //点圆形图标显示音乐播放界面
        civBBPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlPlayMusic.setVisibility(View.VISIBLE);
                //设置缩放动画
                ScaleAnimation anim = new ScaleAnimation(0,1,0,1,0,rlPlayMusic.getHeight());
                anim.setDuration(300);
                rlPlayMusic.startAnimation(anim);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case (0):
                        rbNewMusic.setChecked(true);
                        break;
                    case 1:
                        rbHotMusic.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case(R.id.rb_new_song):
                        viewPager.setCurrentItem(0);
                        break;
                    case(R.id.rb_hot_song):
                        viewPager.setCurrentItem(1);
                        break;
                }
            }
        });
    }

    /**
     * 控制暂停，上一首，下一首3个按钮
     * @param view
     */
    public void controllMusic(View view) {
        MusicApplication application = MusicApplication.getApp();
        switch (view.getId()) {
            case R.id.ivPMStart:
                binder.playOrPause();
                break;
            case R.id.ivPMPre:
                final Music music1 = application.getCurrentMusic();
                String songId1 = music1.getSong_id();
                model.loadSongInfoBySongId(songId1, new SongInfoCallback() {
                    @Override
                    public void onSongInfoLoaded(List<SongUrl> urls, SongInfo songInfo) {
                        music1.setUrls(urls);
                        music1.setSongInfo(songInfo);
                        //播放音乐
                        binder.playMusic(music1.getUrls().get(0).getFile_link());

                    }
                });
                break;
            case R.id.ivPMNext:
                final Music music2 = application.getCurrentMusic();
                String songId2 =music2.getSong_id();
                model.loadSongInfoBySongId(songId2, new SongInfoCallback() {
                    @Override
                    public void onSongInfoLoaded(List<SongUrl> urls, SongInfo songInfo) {
                        music2.setUrls(urls);
                        music2.setSongInfo(songInfo);
                        //播放音乐
                        binder.playMusic(music2.getUrls().get(0).getFile_link());
                    }
                });
                break;
        }

    }

    /**
     * 设置viewPager的adapter
     */
    private void setPagerAdapter() {
        fragments = new ArrayList<>();

        fragments.add(new NewMusicListFragment());
        fragments.add(new HotMusicListFragment());

        PagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

    }

    private void initViews() {
        rbHotMusic = (RadioButton)findViewById(R.id.rb_hot_song);
        rbNewMusic = (RadioButton)findViewById(R.id.rb_new_song);
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        radioGroup = (RadioGroup)findViewById(R.id.rg1);

        civBBPic = (CircleImageView) findViewById(R.id.civ_bottom_bar_pic);
        tvBBtitle = (TextView) findViewById(R.id.tv_bottom_bar_title);

        rlPlayMusic = (RelativeLayout) findViewById(R.id.rlPlayMusic);
        tvPMCurrentTime = (TextView) findViewById(R.id.tvPMCurrentTime);
        tvPMTotalTime = (TextView) findViewById(R.id.tvPMTotalTime);
        tvPMSinger = (TextView) findViewById(R.id.tvPMSinger);
        tvPMSong = (TextView) findViewById(R.id.tvPMTitle);
        tvPMLrc = (TextView) findViewById(R.id.tvPMLrc);
        ivPMAlbum = (ImageView) findViewById(R.id.ivPMAlbum);
        ivPMBackground = (ImageView) findViewById(R.id.ivPMBackground);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        btnToSearch = (Button) findViewById(R.id.btnToSearch);
        btnSearching = (Button) findViewById(R.id.btnSearch);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        etSearch = (EditText) findViewById(R.id.etSearch);
        rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);
        lvSearchResult = (ListView) findViewById(R.id.lvSearchResult);

    }
    class MainPagerAdapter extends FragmentPagerAdapter{


        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    /**
     * 广播接收者 接收音乐信息的广播
     */
    private class MusicInfoReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(GlobalConsts.ACTION_UPDATE_MUSIC_PROCESS)) {
                //更新seekBar及当前时间
                int totalTime =intent.getIntExtra(GlobalConsts.EXTRA_MUSIC_TOTAL_TIME, 0);
                int currentTime = intent.getIntExtra(GlobalConsts.EXTRA_MUSIC_CURRENT_TIME, 0);
                //设置格式化后的时间
                String time = DateUtils.parseTime(currentTime);
                tvPMCurrentTime.setText(time);
                tvPMTotalTime.setText(DateUtils.parseTime(totalTime));
                //seekbar
                seekBar.setMax(totalTime);
                seekBar.setProgress(currentTime);
                Music m = MusicApplication.getApp().getCurrentMusic();
                HashMap<String,String> lrc = m.getLrc();
                if (lrc != null) {
                    String content = lrc.get(time);
                    if (content != null) {//当前歌词需要更新
                        tvPMLrc.setText(content);
                    }
                }
            }
            if (action.equals(GlobalConsts.ACTION_MUSIC_STARTED)) {
                //音乐开始播放 获取音乐信息
                final Music m= MusicApplication.getApp().getCurrentMusic();
                tvBBtitle.setText(m.getTitle());
                String pathPicSmall = m.getPic_small();
                BitmapUtils.loadBitmap(pathPicSmall, new BitmapCallback() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap) {
                        if (bitmap != null) {//下载成功
                            civBBPic.setImageBitmap(bitmap);
                            //让图片转起来
                            RotateAnimation anim = new RotateAnimation(0, 360, civBBPic.getWidth() / 2, civBBPic.getHeight() / 2);
                            anim.setDuration(100000);
                            //匀速旋转
                            anim.setInterpolator(new LinearInterpolator());
                            //无限重复
                            anim.setRepeatCount(Animation.INFINITE);
                            civBBPic.startAnimation(anim);


                        }else{
                            civBBPic.setImageResource(R.mipmap.ic_launcher);
                        }
                    }
                });
                //设置音乐图片
                String pathPicAlbum = m.getSongInfo().getAlbum_500_500();
                if (pathPicAlbum.equals("")) {
                    pathPicAlbum = m.getSongInfo().getAlbum_1000_1000();
                }
                BitmapUtils.loadBitmap(pathPicAlbum, new BitmapCallback() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap) {
                        if (bitmap != null) {
                            ivPMAlbum.setImageBitmap(bitmap);

                        }else {
                            ivPMAlbum.setImageResource(R.drawable.default_music_pic);
                        }
                    }
                });
                //设置背景图片
                String pathPicBackground = m.getSongInfo().getArtist_480_800();
                if (pathPicBackground.equals("")) {
                    pathPicBackground = m.getSongInfo().getArtist_500_500();
                }
                if (pathPicBackground.equals("")) {
                    pathPicBackground = m.getSongInfo().getArtist_640_1136();
                }
                if (pathPicBackground.equals("")) {
                    pathPicBackground = pathPicAlbum;
                }
                BitmapUtils.loadBitmap(pathPicBackground,8, new BitmapCallback() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap) {
                        if (bitmap != null) {//背景图片下载成功
                            //模糊化处理图片
                            BitmapUtils.loadBlurBitmap(bitmap,10, new BitmapCallback() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap) {
                                    ivPMBackground.setImageBitmap(bitmap);
                                }
                            });
                        }else {
                            ivPMBackground.setImageResource(R.drawable.default_music_background);
                        }
                    }
                });

                tvPMSong.setText(m.getSongInfo().getTitle());
                tvPMSinger.setText(m.getSongInfo().getAuthor());
                //下载歌词，并解析
                if (m.getLrc()!=null) {//歌词已经下载过了
                    return;
                }

                String lrcLink=m.getSongInfo().getLrclink();
                if (lrcLink == null||lrcLink.equals("")) {
                    Toast.makeText(context, "该歌曲没有歌词",Toast.LENGTH_LONG).show();
                    return;
                }
                model.loadLrc(lrcLink, new LrcCallback() {
                    @Override
                    public void onLrcLoaded(HashMap<String, String> lrc) {
                        m.setLrc(lrc);//保存至Music中
                    }
                });


            }
        }
    }
}
