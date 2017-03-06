package com.adam.suixinplayer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import com.adam.suixinplayer.R;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 异步加载图片的工具类
 * 时间图片的内存缓存与文件缓存
 * Created by Administrator on 2017/3/2.
 */
public class ImageLoader {
    private Context context;
    //用于轮循的集合
    private List<ImageLoadTask> tasks = new ArrayList<ImageLoadTask>();
    //用于轮循任务的工作线程
    private Thread workThread;
    private boolean isLoop = true;
    private ListView listView;

    //
    private HashMap<String, SoftReference<Bitmap>> cache =new HashMap<String, SoftReference<Bitmap>>();



    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_LOAD_IMAGE_SUCCESS:
                    ImageLoadTask task = (ImageLoadTask) msg.obj;
                    ImageView imageView = (ImageView) listView.findViewWithTag(task.path);
                    if (imageView != null) {
                        if (task.bitmap != null) {
                            //下载图片成功
                            Bitmap bitmap = task.bitmap;
                            imageView.setImageBitmap(bitmap);
                        } else {
                            imageView.setImageResource(R.mipmap.ic_launcher);
                        }
                    }
                    break;
            }
        }
    };

    public static final int HANDLE_LOAD_IMAGE_SUCCESS = 1;

    public ImageLoader(Context context, ListView listView) {
        this.context = context;
        this.listView = listView;


    workThread = new Thread() {
            @Override
            public void run() {
                //不断轮循任务集合，发现对象后，取出对象并开始下载任务
                while (isLoop) {
                    if (!tasks.isEmpty()) {//任务集不为空
                        ImageLoadTask task = tasks.remove(0);
                        Bitmap bitmap = loadBitmap(task.path);
                        task.bitmap = bitmap;
                        //下载图片成功，给handler发信息
                        Message msg = new Message();
                        msg.what = HANDLE_LOAD_IMAGE_SUCCESS;
                        msg.obj = task;
                        handler.sendMessage(msg);
                    } else {//任务集合为空，工作线程就等待
                        try {
                            synchronized (workThread) {//同步锁
                                workThread.wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        workThread.start();
    }

    private Bitmap loadBitmap(String path) {
        try {
            InputStream is = HttpUtils.getInputStream(path);
            Bitmap bitmap = BitmapUtils.loadBitmap(is, 50, 50);
            //Bitmap bitmap = BitmapFactory.decodeStream(is);
            //吧图片存入内存缓存中
            SoftReference<Bitmap> reference =new SoftReference<Bitmap>(bitmap);
            cache.put(path,reference);
            //把图片存入文件缓存中
            //通过path 截取出文件名
            String fileName = path.substring(path.lastIndexOf("/"));
            File file = new File(context.getCacheDir(),"images"+fileName);
            BitmapUtils.save(bitmap,file);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //显示图片
    public void displayImage(ImageView imageView,String path){
        imageView.setTag(path);
        //先在内存缓存中查找有没有图片
        SoftReference<Bitmap> reference = cache.get(path);
        if(reference!=null){//原来存过
            Bitmap bitmap =  reference.get();
            if(bitmap!=null){//图片没有被删除
                Log.i("info", "图片从内存缓存中加载的");
                imageView.setImageBitmap(bitmap);
                return ;
            }
        }
        //如果内存缓存中没有，再在文件缓存中查找
        String fileName = path.substring(path.lastIndexOf("/"));
        File file = new File(context.getCacheDir(),"images"+fileName);
        Bitmap bitmap = BitmapUtils.loadBitmap(file);
        if(bitmap!=null){
            Log.i("info", "图片是从文件缓存中加载的 ");
            //向内存缓存中再存一次
            cache.put(path,new SoftReference<Bitmap>(bitmap));
            imageView.setImageBitmap(bitmap);
            return ;
        }
        //向任务集合中添加一个图片下载任务
        ImageLoadTask task = new ImageLoadTask();
        task.path = path;
        tasks.add(task);
        //集合中添加了任务，唤醒工作线程
        synchronized (workThread) {
            workThread.notify();
        }
    }
    /**
     * 用于描述一个下载任务
     */
    private class ImageLoadTask {
        String path;
        Bitmap bitmap;
    }

    public void stopThread() {
        isLoop = false;
        synchronized (workThread) {
            workThread.notify();
        }
    }
}
