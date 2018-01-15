package com.juxin.library.observe;

import com.juxin.library.log.PLogger;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 消息处理中枢
 * Created by ZRP on 2016/12/27.
 */
public class MsgMgr {

    private static MsgMgr instance = new MsgMgr();

    public static MsgMgr getInstance() {
        return instance;
    }

    // ------------------------------事件发送及监听--------------------------------

    private CompositeDisposable rxDisposable = new CompositeDisposable();//订阅中心
    private ConcurrentHashMap<PObserver, Disposable> observerDisposableMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Class, PObserver> classObserverMap = new ConcurrentHashMap<>();

    /**
     * 抛出事件到主线程
     *
     * @param key   事件的key
     * @param value 事件的value
     */
    public void sendMsg(String key, Object value) {
        Msg msg = new Msg(key, value);
        PLogger.d("------>" + msg.toString());
        RxBus.getInstance().post(msg);
    }

    /**
     * 绑定所有类型的消息通知，具体类型根据PObserver#onMessage的key进行区分
     *
     * @param observer 事件回调
     */
    public void attach(final PObserver observer) {
        attach(observer, false);
    }

    public void attach(final PObserver observer, boolean isReLoad) {
        if (observer == null) {
            PLogger.e("------>PObserver is null.");
            return;
        }

        if (!isReLoad) {//是否可以重复添加同一个类 ，在检测网络状态时，在添加时不需要移除之前的类
            //移除前一个添加的相同的类
            PObserver preObserver = classObserverMap.get(observer.getClass());
            if (preObserver != null) {
                detach(preObserver);
            }
        }


        Disposable disposable = RxBus.getInstance().toFlowable(Msg.class)
                .onBackpressureBuffer().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Msg>() {
                    @Override
                    public void accept(Msg msg) throws Exception {
                        observer.onMessage(msg.getKey(), msg.getData());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        PLogger.printThrowable(throwable);
                    }
                });

        if (rxDisposable.add(disposable)) {
            classObserverMap.put(observer.getClass(), observer);
            observerDisposableMap.put(observer, disposable);
            PLogger.d("------>attach[" + observer.getClass() + "], attached-size[" + rxDisposable.size() + "]");
        }
    }

    /**
     * 解除绑定消息通知
     *
     * @param observer 监听
     */
    public void detach(PObserver observer) {
        if (observer == null) return;

        Disposable disposable = observerDisposableMap.remove(observer);
        if (disposable != null) {
            rxDisposable.remove(disposable);
            PLogger.d("------>detach[" + observer.getClass() + "], attached-size[" + rxDisposable.size() + "]");
        }
        //@author Mr.Huang
        //@date 2017-08-07
        //删除释放注册的observer, 防止内存溢出
        classObserverMap.remove(observer.getClass());
    }

    /**
     * 清除所有已经attach的监听，防止内存溢出[谨慎调用]
     */
    public void clear() {
        if (!rxDisposable.isDisposed()) {
            PLogger.d("------>clear");
            rxDisposable.clear();
        }
        //@author Mr.Huang
        //@date 2017-08-07
        //清除所有
        classObserverMap.clear();
    }

    // -----------------------------线程切换及任务处理--------------------------------

    /**
     * 抛出事件到ui线程并执行
     *
     * @param runnable 事件
     */
    public void runOnUiThread(final Runnable runnable) {
        Flowable.empty().observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Object>() {
            @Override
            public void onSubscribe(Subscription s) {
            }

            @Override
            public void onNext(Object o) {
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
                if (runnable != null) runnable.run();
            }
        });
    }

    /**
     * 抛出事件到子线程并执行
     *
     * @param runnable 事件
     */
    public void runOnChildThread(final Runnable runnable) {
        Flowable.empty().observeOn(Schedulers.io()).subscribe(new Subscriber<Object>() {
            @Override
            public void onSubscribe(Subscription s) {
            }

            @Override
            public void onNext(Object o) {
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
                if (runnable != null) runnable.run();
            }
        });
    }

    /**
     * 延迟执行任务，默认在主进程执行
     *
     * @param runnable  延迟执行的任务
     * @param delayTime 延迟时间，ms级
     */
    public void delay(Runnable runnable, long delayTime) {
        delay(runnable, delayTime, true);
    }

    /**
     * 延迟执行任务
     *
     * @param runnable   延迟执行的任务
     * @param delayTime  延迟时间，ms级
     * @param mainThread 是否在主进程执行
     */
    public void delay(final Runnable runnable, long delayTime, boolean mainThread) {
        Flowable.timer(delayTime, TimeUnit.MILLISECONDS)
                .observeOn(mainThread ? AndroidSchedulers.mainThread() : Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (runnable != null) runnable.run();
                    }
                });
    }

}
