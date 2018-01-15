package com.juxin.predestinate.bean.db;

import android.app.Application;
import com.juxin.library.observe.RxBus;

import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Kind on 2017/3/27.
 */
//@Module(includes = {DBModule.class, DBCache.class})
@Module(includes = {DBModule.class})
public class AppModule {
    private final Application mApplication;

    public AppModule(Application application) {
        this.mApplication = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    RxBus provideRxBus() {
        return RxBus.getInstance();
    }
}
