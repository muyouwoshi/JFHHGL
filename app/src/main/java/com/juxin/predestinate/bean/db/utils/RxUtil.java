package com.juxin.predestinate.bean.db.utils;

import android.text.TextUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 数据库rx操作处理工具类，提供常用rx操作实例
 * Created by Kind on 2017/5/31.
 */
public class RxUtil {

    public static ObservableTransformer IO_TRANSFORMER = new ObservableTransformer() {
        @Override
        public ObservableSource apply(@NonNull Observable upstream) {
            return upstream.subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io());
        }
    };

    public static ObservableTransformer IO_ON_UI_TRANSFORMER = new ObservableTransformer() {
        @Override
        public ObservableSource apply(@NonNull Observable upstream) {
            return upstream.subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    public static Consumer<Throwable> ERROR_ACTION = new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
            if (null != throwable && !TextUtils.isEmpty(throwable.getMessage())) {
                throwable.printStackTrace();
            }
        }
    };

    public static <T> ObservableTransformer<T, T> applySchedulers(ObservableTransformer transformer) {
        //noinspection unchecked
        return (ObservableTransformer<T, T>) transformer;
    }
}