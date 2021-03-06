package com.dev.orium.reader;

import android.app.Application;
import android.util.Log;

import com.dev.orium.reader.utils.AppUtils;
import com.dev.orium.reader.utils.DateUtils;
import com.dev.orium.reader.utils.SharedUtils;
import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppUtils.init(this);
        SharedUtils.init(this);
        DateUtils.init(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree(this));
        }

        initImageLoader();
    }

    private void initImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub) // resource or drawable
                .showImageForEmptyUri(R.drawable.ic_stub) // resource or drawable
                .showImageOnFail(R.drawable.ic_stub) // resource or drawable
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(10)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCacheSize(10 * 1024 * 1024)
                .defaultDisplayImageOptions(options)
                .build();

        L.writeLogs(true);

        ImageLoader.getInstance().init(config);
    }

    private static class CrashReportingTree extends Timber.Tree {
        public CrashReportingTree(App app) {
            Fabric.with(app, new Crashlytics());
        }

        @Override protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            Crashlytics.log(priority, tag, message);

            if (t != null) {
                Crashlytics.logException(t);
            }
        }
    }

}


//todo
/*
favorites
save to disk?
settings
themes
search select several
auto sync
import/export

 */

