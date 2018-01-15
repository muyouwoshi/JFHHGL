package com.juxin.predestinate.module.logic.model.impl;

import com.juxin.library.observe.ModuleBase;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.library.unread.UnreadMgr;
import com.juxin.predestinate.bean.db.DBCenter;
import com.juxin.predestinate.module.local.chat.ChatSpecialMgr;
import com.juxin.predestinate.module.local.chat.inter.ChatMsgInterface;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.logic.application.ModuleMgr;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * 未读角标实现类
 * Created by ZRP on 2017/5/17.
 */
public class UnreadMgrImpl implements ModuleBase, ChatMsgInterface.UnreadReceiveMsgListener, PObserver {

    // =========================未读角标配置（添加新的角标类型后在此进行配置）============================

    /* --------一级角标：最顶层父级-------- */
    public static final String MAIL = "MAIL";                           //信箱
    public static final String CENTER = "CENTER";                       //个人中心
    /* --------二级角标-------- */
    public static final String FOLLOW_ME = "FOLLOW_ME";                 //谁关注我
    public static final String MY_WALLET = "MY_WALLET";                 //我的钱包
    public static final String WOMAN_AUTOREPLY = "WOMAN_AUTOREPLY";     //自动回复

    /* --------三级角标-------- */
    // 暂无
    /* --------游离角标-------- */
    // 暂无

    /* --------未读消息的级联关系，每次添加新的层级角标之后在此进行配置-------- */
    private static final Map<String, String[]> parentMap = new HashMap<String, String[]>() {
        {
            put(FOLLOW_ME, new String[]{CENTER});
            put(MY_WALLET, new String[]{CENTER});
            put(WOMAN_AUTOREPLY, new String[]{CENTER});
        }
    };

    // ====================================未读角标配置 end============================================

    @Inject
    DBCenter dbCenter;

    @Override
    public void init() {
        MsgMgr.getInstance().attach(this);//监听登录消息
        ChatSpecialMgr.getChatSpecialMgr().attachUnreadMsgListener(this);//监听抛出的角标消息
    }

    @Override
    public void release() {
        MsgMgr.getInstance().detach(this);
        ChatSpecialMgr.getChatSpecialMgr().detachUnreadMsgListener(this);
    }

    /**
     * @return 获取未读角标管理类
     */
    public UnreadMgr getUnreadMgr() {
        return UnreadMgr.getInstance();
    }

    @Override
    public void onUpdateUnread(BaseMessage message) {
        switch (message.getType()) {//比对抛出的未读类型消息，进行角标的添加
            case BaseMessage.Follow_MsgType://谁关注我消息
                // [谁关注我消息文档](http://doc.dev.yuanfenba.net/pkg/yuanfen/common/msg_data/#MSG_TYPE_FOCUS)
                JSONObject jsonObject = message.getJsonObj();
                int gz = jsonObject.optInt("gz");//关注状态1为关注2为取消关注
                if (gz == 1) {
                    getUnreadMgr().addNumUnread(FOLLOW_ME);
                } else if (gz == 2) {
                    getUnreadMgr().reduceUnreadByKey(FOLLOW_ME);
                }
                break;
            case BaseMessage.TalkRed_MsgType://聊天随机红包
            case BaseMessage.RedEnvelopesBalance_MsgType://钱包余额变更消息
                getUnreadMgr().addNumUnread(MY_WALLET);

                // 更新个人资料中红包数额
                MsgMgr.getInstance().sendMsg(MsgType.MT_Update_MyInfo, null);
                break;
            default:
                break;
        }
    }

    /**
     * 获取角标数据存储的key，以uid进行存储，便于用户切换帐号处理
     *
     * @return 获取最终存储用户角标信息的标签
     */
    private String getStoreTag() {
        return "unread_" + String.valueOf(ModuleMgr.getLoginMgr().getUid());
    }

    @Override
    public void onMessage(String key, Object value) {
        switch (key) {
            case MsgType.MT_DB_Init_Ok:
                // 注入dagger组件
                ModuleMgr.getChatListMgr().getAppComponent().inject(this);

                // 初始化角标数据
                Observable<String> observable = dbCenter.getCenterFUnRead().queryUnRead(getStoreTag());
                observable.subscribe(new Observer<String>() {
                    private Disposable queryDisposable;

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        queryDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull String storeString) {
                        queryDisposable.dispose();
                        getUnreadMgr().init(storeString, parentMap);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
                // 角标变更监听，每次变更之后更新数据库
                getUnreadMgr().setUnreadListener(new UnreadMgr.UnreadListener() {
                    @Override
                    public void onUnreadChange(String key, boolean isAdd, String storeString) {
                        dbCenter.insertUnRead(getStoreTag(), storeString, null);
                    }
                });
            default:
                break;
        }
    }
}
