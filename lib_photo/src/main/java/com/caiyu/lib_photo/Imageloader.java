package com.caiyu.lib_photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 图片加载类
 *
 * @author wangjing
 */
public class Imageloader {
    private static Imageloader mInstance;
    /**
     * 图片缓存核心对象
     */
    private LruCache<String, Bitmap> mLruCache;
    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
    //默认线程池数量
    private static final int DEFAULT_THREAD_COUNT = 1;
    /**
     * 队列调度方式
     */
    private Type mType = Type.LIFO;
    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTaskQueue;
    /**
     * 后台轮询线程
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHandler;
    /**
     * UI线程中的Handler
     */
    private Handler mUIdHandler;

    private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);
    private Semaphore mSemaphoreThreadPool = new Semaphore(0);

    public enum Type {
        FIFO, LIFO
    }


    private Imageloader(int mThreadCount, Type type) {
        init(mThreadCount, type);
    }

    /**
     * 初始化操作
     *
     * @param type
     */
    private void init(int mThreadCount, Type type) {

        //后台轮询线程
        mPoolThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mPoolThreadHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        //线程池去取出一个任务执行
                        mThreadPool.execute(getTask());
                        try {
                            mSemaphoreThreadPool.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                };
                //释放一个信号量
                mSemaphorePoolThreadHandler.release();
                Looper.loop();//轮询
            }
        };
        mPoolThread.start();
        //获取我们应用的最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;

        mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };

        //创建线程池
        mThreadPool = Executors.newFixedThreadPool(mThreadCount);
        mTaskQueue = new LinkedList<Runnable>();
        mType = type;

        mSemaphoreThreadPool = new Semaphore(mThreadCount);
    }


    /**
     * 从任务队列取出一个方法
     *
     * @return
     */
    private Runnable getTask() {
        try {
            if (mType == Type.FIFO) {
                return mTaskQueue.removeFirst();
            } else if (mType == Type.LIFO) {
                return mTaskQueue.removeLast();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Imageloader getmInstance() {
        if (mInstance == null) {
            synchronized (Imageloader.class) {
                if (mInstance == null) {
                    mInstance = new Imageloader(DEFAULT_THREAD_COUNT, Type.LIFO);
                }
            }
        }
        return mInstance;
    }

    public static Imageloader getmInstance(int threadCount, Type type) {
        if (mInstance == null) {
            synchronized (Imageloader.class) {
                if (mInstance == null) {
                    mInstance = new Imageloader(threadCount, type);
                }
            }
        }
        return mInstance;
    }

    /**
     * 根据path为ImageView设置图片
     *
     * @param path
     * @param imageview
     */
    public void loadImage(final String path, final ImageView imageview) {
        imageview.setTag(path);
        if (mUIdHandler == null) {
            mUIdHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    //获取的得到的图片，为Imageview回调设置图片
                    ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
                    Bitmap bm = holder.bitmap;
                    ImageView imageView = holder.imageView;
                    String path = holder.path;
                    //将path与getTag存储路径进行比较
                    if (imageView.getTag()!= null && imageView.getTag().toString().equals(path)) {
                        imageView.setImageBitmap(bm);
                    }

                }
            };
        }
        //根据path在缓存中获取bitmap
        Bitmap bm = getBitmapFromLrucache(path);
        if (bm != null) {
            refreshBitmap(path, imageview, bm);
        } else {
            addTask(new Runnable() {
                @Override
                public void run() {
                    //加载图片
                    //图片压缩
                    //1获得图片需要显示的大小
                    ImageSize imageSize = getImageViewSize(imageview);
                    //2、压缩图片
                    Bitmap bm = decodeSampledBitmapFromPath(path, imageSize.width, imageSize.height);

                    //3、把图片加入到缓存
                    addBitmapToLruCache(path, bm);
                    refreshBitmap(path, imageview, bm);
                    mSemaphoreThreadPool.release();
                }
            });
        }
    }

    private void refreshBitmap(String path, ImageView imageview, Bitmap bm) {
        Message message = Message.obtain();
        ImgBeanHolder holder = new ImgBeanHolder();
        holder.bitmap = bm;
        holder.path = path;
        holder.imageView = imageview;
        message.obj = holder;
        mUIdHandler.sendMessage(message);
    }

    /**
     * 将图片加入LruChche
     *
     * @param path
     * @param bm
     */
    private void addBitmapToLruCache(String path, Bitmap bm) {
        if (getBitmapFromLrucache(path) == null) {
            if (bm != null) {
                mLruCache.put(path, bm);
            }
        }
    }

    /**
     * 根据图片需要显示的宽和高对图片进行压缩
     *
     * @param path
     * @param width
     * @param height
     * @return
     */
    private Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {

        //获取图片的宽和高，并不把图片加载到内存中
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = caculateInSampleSize(options, width, height);

        //使用获取到的inSamleSize再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }


    /**
     * 根据图片需求的宽和高以及图片实际的宽和高计算SampleSize
     *
     * @param options
     * @param reqwidth
     * @param reqheight
     * @return
     */
    private int caculateInSampleSize(BitmapFactory.Options options, int reqwidth, int reqheight) {

        int witdh = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (witdh > reqwidth || height > reqheight) {
            int widthRadio = Math.round(witdh * 1.0f / reqwidth);
            int heightRadio = Math.round(height * 1.0f / reqheight);
            inSampleSize = Math.max(widthRadio, heightRadio);
        }
        return inSampleSize;
    }

    /**
     * 根据ImageView获取适当的压缩的宽和高
     *
     * @param imageview
     * @return
     */
    private ImageSize getImageViewSize(ImageView imageview) {
        ImageSize imageSize = new ImageSize();
        DisplayMetrics displayMetrics = imageview.getContext().getResources().getDisplayMetrics();


        ViewGroup.LayoutParams lp = imageview.getLayoutParams();

        int width = imageview.getWidth();//获取imageview的实际宽度
        if (width <= 0) {
            width = lp.width;//获取imageview在layout中声明的宽度
        }
        if (width <= 0) {
//            width = imageview.getMaxWidth();//检查最大值
            width = getImageViewFiledValue(imageview, "mMaxWidth");//检查最大值
        }

        if (width <= 0) {
            width = displayMetrics.widthPixels;//如果还为空 就获取屏幕宽度
        }


        int height = imageview.getHeight();//获取imageview的实际高度
        if (height <= 0) {
            height = lp.height;//获取imageview在layout中声明的高度
        }
        if (height <= 0) {
//            height = imageview.getMaxHeight();//检查最大值高度
            height = getImageViewFiledValue(imageview, "mMaxHeight");//检查最大值高度
        }

        if (height <= 0) {
            height = displayMetrics.heightPixels;//如果还为空 就获取屏幕高度
        }
        imageSize.width = width;
        imageSize.height = height;

        return imageSize;
    }

    /**
     * 通过反射获取ImageView的某个属性值
     *
     * @param object
     * @return
     */
    private static int getImageViewFiledValue(Object object, String fieleName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieleName);
            field.setAccessible(true);

            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }


    private synchronized void addTask(Runnable runnable) {
        mTaskQueue.add(runnable);
        try {
            if (mPoolThreadHandler == null) {
                mSemaphorePoolThreadHandler.acquire();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mPoolThreadHandler.sendEmptyMessage(0x110);

    }

    /**
     * 根据path在缓存中获取bitmap
     *
     * @param key
     * @return
     */
    private Bitmap getBitmapFromLrucache(String key) {
        if (key != null) {
            return mLruCache.get(key);
        }
        return null;
    }

    private class ImageSize {
        int width;
        int height;
    }

    private class ImgBeanHolder {
        Bitmap bitmap;
        ImageView imageView;
        String path;
    }
}
