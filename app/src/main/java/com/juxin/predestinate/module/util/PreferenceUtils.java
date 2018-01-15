package com.juxin.predestinate.module.util;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.juxin.library.log.PSP;
import com.juxin.predestinate.module.logic.application.App;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.Huang
 * @date 2017/9/13
 */
public class PreferenceUtils {

    public static void putFreeVideoInfo(int count, int videochatLen) {
        Map<String, Object> map = new ArrayMap<>();
        map.put("count", count);
        map.put("videochat_len", videochatLen);
        PSP.getInstance().put("show_get_video_card_dialog_" + App.uid, JSON.toJSONString(map));
    }

    public static boolean isFreeVideInfo() {
        return PSP.getInstance().getString("show_get_video_card_dialog_" + App.uid, null) != null;
    }

    public static void remoteFreeVideInfo() {
        PSP.getInstance().remove("show_get_video_card_dialog_" + App.uid);
    }

    // ------------------聊天解锁本地缓存处理 start--------------------

    private static HashMap<Long, Boolean> getLockCacheMap() {
        return JSON.parseObject(
                PSP.getInstance().getString("USER_CHAT_LOCK_CACHE_" + App.uid, "{}"),
                new TypeReference<HashMap<Long, Boolean>>() {
                });
    }

    public static void cacheUnlockUser(long uid, boolean isUnlock) {
        HashMap<Long, Boolean> lockCacheMap = getLockCacheMap();
        if (lockCacheMap == null) lockCacheMap = new HashMap<>();
        lockCacheMap.put(uid, isUnlock);
        PSP.getInstance().put("USER_CHAT_LOCK_CACHE_" + App.uid, JSON.toJSONString(lockCacheMap));
    }

    public static boolean isUserUnlock(long uid) {
        HashMap<Long, Boolean> lockCacheMap = getLockCacheMap();
        return lockCacheMap != null && lockCacheMap.containsKey(uid) && lockCacheMap.get(uid);
    }
    // ------------------聊天解锁本地缓存处理 end--------------------
}
