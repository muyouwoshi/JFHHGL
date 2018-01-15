package com.juxin.predestinate.bean.db;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.juxin.library.log.PLogger;
import com.juxin.predestinate.bean.db.cache.DBCacheCenter;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.VideoMessage;
import com.juxin.predestinate.module.local.chat.utils.MessageConstant;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * DB处理中心
 * Created by Kind on 2017/3/28.
 */

public class DBCenter {

    private BriteDatabase mDatabase;
    private DBCenterFLetter centerFLetter;
    private DBCenterFMessage centerFmessage;
    private DBCenterFUnRead centerFUnRead;
    private DBCacheCenter cacheCenter;
    private DBCenterFMark centerFMark;

    private HandlerThread workerThread = new HandlerThread("LightTaskThread");
    private DBHandler handler = null;

    public static void makeDBCallback(DBCallback callback, long result) {
        if (callback != null) {
            callback.OnDBExecuted(result);
        }
    }

    public DBCenter(BriteDatabase database) {
        this.mDatabase = database;

        workerThread.start();
        handler = new DBHandler(workerThread.getLooper());

        centerFLetter = new DBCenterFLetter(database, handler);
        centerFmessage = new DBCenterFMessage(database, handler);
        centerFUnRead = new DBCenterFUnRead(database, handler);
        cacheCenter = new DBCacheCenter(database, handler);
        centerFMark = new DBCenterFMark(database, handler);
    }

    /******************** DBCacheCenter **************************/

    public DBCacheCenter getCacheCenter() {
        return cacheCenter;
    }


    /******************** FUnRead **************************/
    public DBCenterFUnRead getCenterFUnRead() {
        return centerFUnRead;
    }

    public void insertUnRead(final String key, final String content, DBCallback callback) {
        if (TextUtils.isEmpty(key)) {
            makeDBCallback(callback, MessageConstant.ERROR);
            return;
        }

        centerFUnRead.storageData(key, content, callback);
    }

    /******************** FLetter **************************/
    public void insertMsg(final BaseMessage baseMessage, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (BaseMessage.BaseMessageType.hint.getMsgType() == baseMessage.getType() ||
                        BaseMessage.BaseMessageType.html.getMsgType() == baseMessage.getType()) {
                    baseMessage.setStatus(MessageConstant.READ_STATUS);
                }

                long result = centerFmessage.insertOneMsg(baseMessage);
                if (result != MessageConstant.OK) {
                    DBCenter.makeDBCallback(callback, MessageConstant.ERROR);
                    return;
                }

                if (BaseMessage.BaseMessageType.hint.getMsgType() != baseMessage.getType() &&
                        BaseMessage.BaseMessageType.html.getMsgType() != baseMessage.getType()) {
                    result = centerFLetter.storageData(baseMessage);
                    DBCenter.makeDBCallback(callback, (result >= 0 ? MessageConstant.OK : MessageConstant.ERROR));
                } else {
                    DBCenter.makeDBCallback(callback, MessageConstant.OK);
                }
            }
        });
    }

    public void insertMsgVideo(BaseMessage baseMessage, DBCallback callback) {
        insertMsgVideo(baseMessage, true, callback);
    }

    /**
     *
     * @param baseMessage
     * @param isSave  判断如果message==null的时候，要不要保存消息 true是要保存的  centerFmessage.storageDataVideo点进去可以看说明
     * @param callback
     */
    public void insertMsgVideo(final BaseMessage baseMessage, final boolean isSave, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                long result = centerFmessage.storageDataVideo(baseMessage, isSave);

                if(result != MessageConstant.OK ){
                    DBCenter.makeDBCallback(callback, MessageConstant.ERROR);
                    return;
                }

                result = centerFLetter.storageData(baseMessage);
                long ret = result >= 0 ? MessageConstant.OK : MessageConstant.ERROR;
                DBCenter.makeDBCallback(callback, ret);
            }
        });
    }

    public void insertMsgLocalVideo(final VideoMessage videoMessage, final DBCallback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                long result = centerFLetter.storageData(videoMessage);
                if (result != MessageConstant.OK) {
                    DBCenter.makeDBCallback(callback, MessageConstant.ERROR);
                    return;
                }

                DBCenter.makeDBCallback(callback, centerFmessage.insertOneMsg(videoMessage));
            }
        });
    }

    /**
     * 更新
     *
     * @param message
     * @return
     */
    public void updateMsg(final BaseMessage message, final DBCallback callback) {
        final String userID = message.getWhisperID();
        if (TextUtils.isEmpty(userID)) {
            DBCenter.makeDBCallback(callback, MessageConstant.ERROR);
            return;
        }

        if (BaseMessage.BaseMessageType.hint.getMsgType() != message.getType() &&
                BaseMessage.BaseMessageType.html.getMsgType() != message.getType()) {
            centerFLetter.updateMsgStatus(message, new DBCallback() {
                @Override
                public void OnDBExecuted(long result) {
                    if (result != MessageConstant.OK) {
                        DBCenter.makeDBCallback(callback, MessageConstant.ERROR);
                        return;
                    }

                    centerFmessage.updateMsg(message, callback);
                }
            });
        } else {
            DBCenter.makeDBCallback(callback, MessageConstant.OK);
        }
    }

    public DBCenterFLetter getCenterFLetter() {
        return centerFLetter;
    }

    /**
     * 删除消息列表及内容表的消息
     *
     * @param userID
     * @return
     */
    public void deleteMessage(long userID, final DBCallback callback) {
        centerFLetter.delete(userID, new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                if (callback != null) {
                    callback.OnDBExecuted(result);
                }
            }
        });
        centerFmessage.delete(userID, null);
    }

    public void deleteMessageList(final List<Long> list, final DBCallback callback) {
        centerFLetter.deleteList(list, new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                if (callback != null) {
                    callback.OnDBExecuted(result);
                }
            }
        });

        centerFmessage.deleteList(list, null);
    }

    /**
     * 删除多少个小时以前的消息
     *
     * @param hour
     * @return
     */
    public void deleteMessageHour(int hour) {
        final long delTime = ModuleMgr.getAppMgr().getTime() - (hour * 60 * 60 * 1000);

        Observable<List<BaseMessage>> observable = centerFLetter.deleteCommon(delTime);
        observable.subscribe(new Observer<List<BaseMessage>>() {
            private Disposable deleteDisposable;

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                deleteDisposable = d;
            }

            @Override
            public void onNext(@NonNull List<BaseMessage> baseMessages) {
                deleteDisposable.dispose();
                if (baseMessages == null || baseMessages.size() <= 0) {
                    return;
                }
                for (BaseMessage temp : baseMessages) {
                    if (temp.getTime() < delTime) {
                        centerFLetter.updateContent(temp.getWhisperID(), null);
                    }
                    centerFmessage.delete(temp.getLWhisperID(), delTime, null);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    /**
     * 删除机器人
     *
     * @param hour
     * @return
     */
    public void deleteMessageKFIDHour(int hour) {
        final long delTime = ModuleMgr.getAppMgr().getTime() - (hour * 60 * 60 * 1000);
        Observable<List<BaseMessage>> observable = centerFLetter.deleteKFID();
        observable.subscribe(new Observer<List<BaseMessage>>() {
            private Disposable deleteDisposable;

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                deleteDisposable = d;
            }

            @Override
            public void onNext(@NonNull List<BaseMessage> baseMessages) {
                deleteDisposable.dispose();
                if (baseMessages == null || baseMessages.size() <= 0) {
                    return;
                }
                for (BaseMessage temp : baseMessages) {
                    if (temp.getTime() < delTime) {
                        centerFLetter.updateContent(temp.getWhisperID(), null);
                    }
                    centerFmessage.delete(temp.getLWhisperID(), null);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    /******************** FMessage **************************/

    public DBCenterFMessage getCenterFMessage() {
        return centerFmessage;
    }

    /**
     * 更新fmessage
     *
     * @param baseMessage
     * @return
     */
    public void updateFmessage(final BaseMessage baseMessage, DBCallback callback) {
        centerFmessage.updateMsg(baseMessage, callback);
    }

    public void updateToReadAll(DBCallback callback) {
        centerFmessage.updateToReadAll(callback);
    }

    private class DBHandler extends Handler {
        public DBHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void dispatchMessage(Message msg) {
            // catch any Exception
            try {
                super.dispatchMessage(msg);
            } catch (Exception e) {
                PLogger.e("db dispatchMessage " + e.getMessage());
            }
        }
    }

    public DBCenterFMark getCenterFMark() {
        return centerFMark;
    }

}
