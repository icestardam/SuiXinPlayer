package com.adam.suixinplayer.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2017/2/28.
 */
public class UrlFactory {
    /**
     * 配置获取新歌榜的url
     * @param offset    从第几位开始
     * @param size  榜单长度
     * @return url
     */
    public static String getNewMusicListUrl(int offset ,int size){
        String url = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.billboard.billList&format=xml&type=1&offset="+offset+"&size="+size;
        return url;
    }

    /**
     * 用songId，配置查询歌曲信息的url
     * @param songId    歌曲id
     * @return
     */
    public static String getSongInfoUrl(String songId){
        String url = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.song.getInfos&format=json&songid=" + songId + "&ts=1408284347323&e=JoN56kTXnnbEpd9MVczkYJCSx%2FE1mkLx%2BPMIkTcOEu4%3D&nw=2&ucf=1&res=1";
        return url;
    }

    /**
     * 用关键词，配置搜索歌曲的url
     * @param keyword 关键词
     * @return
     */
    public static String getSearchMusicUrl(String keyword) {
        try {
            keyword = URLEncoder.encode(keyword, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.search.common&format=json&query="+keyword+"&page_no=1&page_size=30";
        return url;
    }
}
