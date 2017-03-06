package com.adam.suixinplayer.util;

import com.adam.suixinplayer.entity.SongInfo;
import com.adam.suixinplayer.entity.SongUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析json的工具类
 * Created by Administrator on 2017/3/2.
 */
public class JsonParser {
    /**
     * 解析jsonArray获得songUrls
     *
     * @param urlArray
     * @return
     */
    public static List<SongUrl> parseSongUrls(JSONArray urlArray) throws JSONException {
        List<SongUrl> urls = new ArrayList<>();
        for (int i = 0; i < urlArray.length(); i++) {
            JSONObject obj = urlArray.getJSONObject(i);
            SongUrl url = new SongUrl(
                    obj.getString("down_type"),
                    obj.getString("original"),
                    obj.getString("free"),
                    obj.getString("replay_gain"),
                    obj.getString("song_file_id"),
                    obj.getString("file_size"),
                    obj.getString("file_extension"),
                    obj.getString("file_duration"),
                    obj.getString("can_see"),
                    obj.getString("can_load"),
                    obj.getString("preload"),
                    obj.getString("file_bitrate"),
                    obj.getString("file_link")
            );
            urls.add(url);

        }
        return urls;
    }

    /**
     * 通过解析infoObj获得songInfo
     *
     * @param infoObj
     * @return
     */
    public static SongInfo parseSongInfo(JSONObject infoObj) throws JSONException {
        SongInfo songInfo = new SongInfo(
                infoObj.getString("pic_huge"),
                infoObj.getString("album_1000_1000"),
                infoObj.getString("pic_singer"),
                infoObj.getString("album_500_500"),
                infoObj.getString("song_source"),
                infoObj.getString("compose"),
                infoObj.getString("artist_500_500"),
                infoObj.getString("file_duration"),
                infoObj.getString("album_title"),
                infoObj.getString("title"),
                infoObj.getString("pic_radio"),
                infoObj.getString("language"),
                infoObj.getString("lrclink"),
                infoObj.getString("pic_big"),
                infoObj.getString("pic_premium"),
                infoObj.getString("artist_480_800"),
                infoObj.getString("country"),
                infoObj.getString("artist_id"),
                infoObj.getString("album_id"),
                infoObj.getString("artist_1000_1000"),
                infoObj.getString("all_artist_id"),
                infoObj.getString("artist_640_1136"),
                infoObj.getString("publishtime"),
                infoObj.getString("share_url"),
                infoObj.getString("author"),
                infoObj.getString("pic_small"),
                infoObj.getString("song_id")
        );
        return songInfo;
    }
}
