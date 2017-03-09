package com.adam.suixinplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.adam.suixinplayer.R;
import com.adam.suixinplayer.entity.Music;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Administrator on 2017/3/9.
 */

public class SearchMusicAdapter extends BaseAdapter {
    private Context context;
    private List<Music> musics;
    private LayoutInflater inflater;

    public SearchMusicAdapter(Context context, List<Music> musics) {
        this.context = context;
        this.musics = musics;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return musics.size();
    }

    @Override
    public Music getItem(int position) {
        return musics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_lv_search_result, null);
            holder = new ViewHolder();
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.tvSinger = (TextView) convertView.findViewById(R.id.tvSinger);
            convertView.setTag(holder);
        }
        holder= (ViewHolder) convertView.getTag();
        Music m =  getItem(position);
        holder.tvSinger.setText(m.getAuthor());
        holder.tvTitle.setText(m.getTitle());
        return convertView;
    }
    class ViewHolder{
        TextView tvTitle;
        TextView tvSinger;
    }
}
