package com.adam.suixinplayer.util;

import android.util.Log;
import android.util.Xml;

import com.adam.suixinplayer.entity.Music;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * XML解析器
 * Created by Administrator on 2017/2/28.
 */
public class XmlParser {

    public static List<Music> parseMusicList(InputStream is) throws Exception {

        //parser
        XmlPullParser parser = Xml.newPullParser();
        //把is放入parser中
        parser.setInput(is, "utf-8");
        //从parser中拿出EventType
        int eventType = parser.getEventType();
        //用EvertType在while中判断
        List<Music> musics = new ArrayList<>();
        Music music = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG://开始标签
                    String tag = parser.getName();
                    if ("song".equals(tag)) {
                        music = new Music();
                        musics.add(music);
                    } else if ("artist_id".equals(tag)) {
                        music.setArtist_id(parser.nextText());
                    } else if ("language".equals(tag)) {
                        music.setLanguage(parser.nextText());
                    } else if ("pic_big".equals(tag)) {
                        music.setPic_big(parser.nextText());
                    } else if ("pic_small".equals(tag)) {
                        music.setPic_small(parser.nextText());
                    } else if ("lrclink".equals(tag)) {
                        music.setLrclink(parser.nextText());
                    } else if ("hot".equals(tag)) {
                        music.setHot(parser.nextText());
                    } else if ("all_artist_id".equals(tag)) {
                        music.setAll_artist_id(parser.nextText());
                    } else if ("style".equals(tag)) {
                        music.setStyle(parser.nextText());
                    } else if ("song_id".equals(tag)) {
                        music.setSong_id(parser.nextText());
                    } else if ("title".equals(tag)) {
                        music.setTitle(parser.nextText());
                    } else if ("ting_uid".equals(tag)) {
                        music.setTing_uid(parser.nextText());
                    } else if ("author".equals(tag)) {
                        music.setAuthor(parser.nextText());
                    } else if ("album_id".equals(tag)) {
                        music.setAlbum_id(parser.nextText());
                    } else if ("album_title".equals(tag)) {
                        music.setAlbum_title(parser.nextText());
                    } else if ("artist_name".equals(tag)) {
                        music.setArtist_name(parser.nextText());
                    }
                    break;

            }
            //获取下一个事件
            eventType = parser.next();

        }

        return musics;
    }
}


