package com.adam.suixinplayer.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/3/4.
 */

public class DateUtils {
    private static final SimpleDateFormat format = new SimpleDateFormat("mm:ss");

    /**
     * 按照format，解析时间戳
     * 返回格式化后的时间字符串
     * @param time  时间戳
     * @return
     */
    public static String parseTime(int time) {
        return format.format(new Date(time));
    }
}
