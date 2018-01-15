package com.juxin.predestinate.module.local.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.module.GlideModule;
import com.juxin.library.dir.DirType;

import java.io.File;

/**
 * 图片加载框架的配置初始化 ，见androidmanifest.xml
 *
 * Glide modules是一个全局改变Glide行为的抽象的方式。
 * Created by @autohr  oumin on 2016/9/1.
 */
public class GlideSetting implements GlideModule {
    @Override
    public void applyOptions(final Context context, GlideBuilder builder) {
//        MemorySizeCalculator calculator = new MemorySizeCalculator(context);
        builder.setDiskCache(new DiskCache.Factory() {
            @Override
            public DiskCache build() {
                // Careful: the external cache directory doesn't enforce permissions
                File cacheFile = new File(DirType.getGliceCacheDir(), "");
                cacheFile.mkdirs();
                //104857600 == 100M
                return DiskLruCacheWrapper.get(cacheFile, 104857600);
            }
        });
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
