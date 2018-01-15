package com.juxin.predestinate.bean.db;

import com.juxin.predestinate.module.local.chat.ChatListMgr;
import com.juxin.predestinate.module.local.chat.ChatMgr;
import com.juxin.predestinate.module.logic.model.impl.UnreadMgrImpl;
import javax.inject.Singleton;
import dagger.Component;

/**
 * 配表
 * Created by Kind on 2017/3/29.
 */
@Singleton
@Component( modules = { AppModule.class})
public interface AppComponent {

    void inject(ChatListMgr chatListMgr);

    void inject(ChatMgr chatMgr);

    void inject(UnreadMgrImpl unreadMgr);

    void inject(OldDBModule oldDBModule);
}
