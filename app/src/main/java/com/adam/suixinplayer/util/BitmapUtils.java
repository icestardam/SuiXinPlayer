package com.adam.suixinplayer.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;

import com.adam.suixinplayer.app.MusicApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/3/1.
 */
public class BitmapUtils {
    /**
     * 异步模糊化图片
     * @param bitmap
     * @param r 模糊半径
     * @param bitmapCallback
     */
    public static void loadBlurBitmap(final Bitmap bitmap, final int r, final BitmapCallback bitmapCallback) {
        AsyncTask<String,String,Bitmap> task = new AsyncTask<String, String, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                Bitmap b = BitmapUtils.createBlurBitmap(bitmap, r);
                return b;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                bitmapCallback.onBitmapLoaded(bitmap);
            }
        };
        task.execute();

    }

    /**
     * 传递bitmap 传递模糊半径 返回一个被模糊的bitmap
     * 比较耗时
     * @param sentBitmap
     * @param radius
     * @return
     */
    private static Bitmap createBlurBitmap(Bitmap sentBitmap, int radius) {
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        if (radius < 1) {
            return (null);
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;
        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];
        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);

        }
        yw = yi = 0;
        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;
        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];

                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];

                }

            }
            stackpointer = radius;
            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);

                }
                p = pix[yw + vmin[x]];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi++;

            }
            yw += w;

        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;
                sir = stack[i + radius];
                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];
                rbs = r1 - Math.abs(i);
                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];

                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];

                }
                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;

                }
                p = x + vmin[y];
                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi += w;
            }
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

    /**
     * 异步在线加载图片
     *
     * @param url
     * @param bitmapCallback
     * @return
     */
    public static void loadBitmap(final String url, final BitmapCallback bitmapCallback) {
        AsyncTask<String, String, Bitmap> task = new AsyncTask<String, String, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                try {
                    //查询文件缓存中是否有，如果有就用文件缓存的
                    String fileName = url.substring(url.lastIndexOf("/"));
                    File file = new File(MusicApplication.getApp().getCacheDir(), fileName);
                    Bitmap b = loadBitmap(file);
                    if (b != null) {//如果文件缓存中有
                        return b;
                    }
                    InputStream is = HttpUtils.getInputStream(url);
                    b = BitmapFactory.decodeStream(is);
                    //将bitmap存入文件缓存中
                    save(b, file);
                    return b;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                bitmapCallback.onBitmapLoaded(bitmap);
            }
        };
        task.execute();
    }

    /**
     * 将bitmap存入文件缓存
     *
     * @param bitmap 需要缓存的图片
     * @param file   目标文件
     */
    public static void save(Bitmap bitmap, File file) throws FileNotFoundException {
        if (!file.getParentFile().exists()) {//父目录不存在
            file.getParentFile().mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
    }

    /**
     * 加载文件缓存中的bitmap
     *
     * @param file 目标文件
     * @return
     */
    public static Bitmap loadBitmap(File file) {
        if (!file.exists()) {//文件不存在
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        return bitmap;
    }

    /**
     * 将Bitmap压缩的工具方法
     *
     * @param is     输入流
     * @param width  图片的目标宽度
     * @param height 图片的目标高度
     * @return
     */
    public static Bitmap loadBitmap(InputStream is, int width, int height) throws IOException {
        //1.处理is，读取到byte[]中
        byte[] buffer = new byte[10240];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int length = 0;
        while ((length = is.read(buffer)) != -1) {
            bos.write(buffer, 0, length);
            bos.flush();
        }
        byte[] bytes = bos.toByteArray();
        int i = bytes.length;
        bos.close();
        //2.读取byte[],获取图片原始宽高
        Options ops = new Options();
        ops.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, i, ops);
        int w = ops.outWidth / width;
        int h = ops.outHeight / height;
        //3.根据原始宽高计算对应的压缩比
        int scale = w > h ? w : h;

        //4.设置options的inSampleSize 压缩图片 得到bitmap
        ops.inSampleSize = scale;
        ops.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, ops);
        return bitmap;
    }

    /**
     * 根据相应的缩放比例进行压缩并返回图片
     *
     * @param pathPicBackground 图片的url地址
     * @param scale             缩放比例
     * @param bitmapCallback    回调
     */
    public static void loadBitmap(final String pathPicBackground, final int scale, final BitmapCallback bitmapCallback) {
        AsyncTask<String, String, Bitmap> task = new AsyncTask<String, String, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                try {
                    String fileName = pathPicBackground.substring(pathPicBackground.lastIndexOf("/"));
                    File file = new File(MusicApplication.getApp().getCacheDir(), fileName);
                    Bitmap b = null;
                    Options opts = new Options();
                    opts.inSampleSize = scale;
                    if (file.exists()) {//文件缓存中有
                        b = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
                        return b;
                    }
                    InputStream is = HttpUtils.getInputStream(pathPicBackground);
                    //得到图片的原始尺寸
                    b = BitmapFactory.decodeStream(is);
                    //保存至文件缓存
                    save(b, file);
                    //压缩图片
                    b = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
                    return b;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;


            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                bitmapCallback.onBitmapLoaded(bitmap);
            }
        };
        task.execute();
    }


}
