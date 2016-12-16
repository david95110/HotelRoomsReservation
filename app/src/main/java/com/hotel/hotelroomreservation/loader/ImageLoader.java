package com.hotel.hotelroomreservation.loader;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.hotel.hotelroomreservation.R;
import com.hotel.hotelroomreservation.http.HTTPClient;
import com.hotel.hotelroomreservation.utils.validations.ContextHolder;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private MemoryCache memoryCache = new MemoryCache();
    private FileCache fileCache;
    private ExecutorService executorService;
    private final Handler handler = new Handler();

    public ImageLoader() {
        fileCache = new FileCache();
        executorService = Executors.newCachedThreadPool();
    }

    public void displayImage(String url, ImageView imageView) {
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageDrawable(ContextCompat
                .getDrawable(ContextHolder.getInstance().getContext(), R.drawable.ic_photo_24dp));
        imageViews.put(imageView, url);

        Bitmap bitmap = memoryCache.getBitmap(url);

        if (bitmap != null) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageBitmap(bitmap);
        } else {
            queuePhoto(url, imageView);
        }
    }

    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    private Bitmap getBitmap(String url) {
        Bitmap b = fileCache.getBitmap(url);
        if (b != null) {
            return b;
        }

        try {
            b = HTTPClient.getPhoto(url);

            if (b != null) {
                fileCache.putBitmap(b, url);
            }

            return b;

        } catch (Throwable ex) {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError) {
                memoryCache.clear();
            }
            return null;
        }
    }

    private class PhotoToLoad {
        private String url;
        private ImageView imageView;

        public PhotoToLoad(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }
    }

    class PhotosLoader implements Runnable {
        private PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            try {
                if (imageViewReused(photoToLoad)) {
                    return;
                }
                Bitmap bmp = getBitmap(photoToLoad.url);
                if (bmp == null) {
                    return;
                }

                memoryCache.putBitmap(photoToLoad.url, bmp);

                if (imageViewReused(photoToLoad)) {
                    return;
                }

                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);

            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        return tag == null || !tag.equals(photoToLoad.url);
    }

    class BitmapDisplayer implements Runnable {
        private Bitmap bitmap;
        private PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad)) {
                return;
            }
            if (bitmap != null) {

                photoToLoad.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                photoToLoad.imageView.setImageBitmap(bitmap);
            }
        }
    }
}
