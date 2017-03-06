package com.adam.suixinplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.adam.suixinplayer.R;
import com.adam.suixinplayer.entity.Music;
import com.adam.suixinplayer.util.ImageLoader;

import java.util.List;

/**
 * 音乐的适配器
 * Created by Administrator on 2017/3/1.
 */
public class MusicAdapter extends BaseAdapter {
    private List<Music> musics;
    private Context context;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;

    public MusicAdapter(List<Music> musics, Context context, ListView listView) {
        this.musics = musics;
        this.context = context;
        this.imageLoader = new ImageLoader(context,listView);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return musics.size();
    }

    @Override
    public Object getItem(int position) {
        return musics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_lv_music, null);
            holder = new ViewHolder();
            holder.ivAlbum = (ImageView) convertView.findViewById(R.id.iv_album);
            holder.tvSong = (TextView) convertView.findViewById(R.id.tv_song);
            holder.tvSinger = (TextView) convertView.findViewById(R.id.tv_singer);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        Music music = (Music) getItem(position);
        holder.tvSinger.setText(music.getArtist_name());
        holder.tvSong.setText(music.getTitle());

        //利用工具类加载图片
        imageLoader.displayImage(holder.ivAlbum,music.getPic_small());


        return convertView;
    }



    private class ViewHolder {
        ImageView ivAlbum;
        TextView tvSong;
        TextView tvSinger;
    }

    public void stopThread() {
        imageLoader.stopThread();
    }

}
