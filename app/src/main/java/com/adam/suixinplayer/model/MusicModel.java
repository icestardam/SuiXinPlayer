package com.adam.suixinplayer.model;

import android.os.AsyncTask;
import android.util.Log;

import com.adam.suixinplayer.entity.Music;
import com.adam.suixinplayer.entity.SongInfo;
import com.adam.suixinplayer.entity.SongUrl;
import com.adam.suixinplayer.util.HttpUtils;
import com.adam.suixinplayer.util.JsonParser;
import com.adam.suixinplayer.util.UrlFactory;
import com.adam.suixinplayer.util.XmlParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/2/28.
 */
public class MusicModel {
    /**
     * 异步加载音乐信息
     * @param songId    歌曲的id
     * @param callback
     */
    public void loadSongInfoBySongId(final String songId, final SongInfoCallback callback) {
        AsyncTask<String,String,Music> task = new AsyncTask<String, String, Music>() {
            @Override
            protected Music doInBackground(String... params) {

                try {
                    String url = UrlFactory.getSongInfoUrl(songId);
                    InputStream is = HttpUtils.getInputStream(url);
                    //json解析的是String
                    //先将is解析成String
                    String json = HttpUtils.is2String(is);
                    //解析json {Song:{url:[{},{}]},songinfo:{}}
                    JSONObject object =new JSONObject(json);
                    JSONArray urlArray = object.getJSONObject("songurl").getJSONArray("url");
                    JSONObject infoObj = object.getJSONObject("songinfo");
                    //调用工具类，解析json
                    List<SongUrl> urls = JsonParser.parseSongUrls(urlArray);
                    SongInfo songInfo = JsonParser.parseSongInfo(infoObj);
                    //将urls和songInfo放入Music中
                    Music m = new Music();
                    m.setUrls(urls);
                    m.setSongInfo(songInfo);
                    return m;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Music music) {
                super.onPostExecute(music);
                if(music==null){
                    callback.onSongInfoLoaded(null,null);
                }
                callback.onSongInfoLoaded(music.getUrls(),music.getSongInfo());
            }
        };
        task.execute();
    }
    /**
     * 异步联网加载新歌音乐列表
     * @param offset
     * @param size
     */
    public void loadNewMusicList(final int offset, final int size,final MusicListCallback callback) {
        AsyncTask<String,String,List<Music>> task = new AsyncTask<String,String,List<Music>> () {
            @Override
            protected List<Music> doInBackground(String... params) {
                try {
                    String url = UrlFactory.getNewMusicListUrl(offset, size);
                    InputStream is = HttpUtils.getInputStream(url);
                    List<Music> musics = XmlParser.parseMusicList(is);
                    return musics;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(List<Music> result) {
               // Log.i("info", "音乐列表：" + result.toString());
                callback.onMusicLoaded(result);
            }
        };
        task.execute();

    }

    /**
     * 异步加载歌词 解析歌词文本 封装成HashMap<String,String> Lrc
     * @param lrcPath 歌词的url地址
     * @param callback
     */
    public void loadLrc(final String lrcPath, final LrcCallback callback) {
        AsyncTask<String,String,HashMap<String,String>> task = new AsyncTask<String, String, HashMap<String, String>>() {
            @Override
            protected HashMap<String, String> doInBackground(String... params) {
                try {
                    InputStream is = HttpUtils.getInputStream(lrcPath);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line = null;
                    HashMap<String, String> lrc = new HashMap<>();
                    while ((line = reader.readLine()) != null) {
                        if (!line.startsWith("[")||!line.contains(":")||!line.contains(".")) {
                            continue;
                        }
                        //时间的格式 [11:00.00]
                        String time = line.substring(1, line.indexOf(".") );
                        String lyric = line.substring(line.lastIndexOf("]") + 1);
                        lrc.put(time, lyric);
                    }
                    return lrc;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(HashMap<String, String> lrc) {
                callback.onLrcLoaded(lrc);
            }
        };
        task.execute();

    }


}
