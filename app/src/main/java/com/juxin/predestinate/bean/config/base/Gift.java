package com.juxin.predestinate.bean.config.base;

import android.util.Log;

import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.bean.net.BaseData;
import com.juxin.predestinate.module.logic.application.ModuleMgr;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * config/GetSet#gift_config节点，各情况下送礼物选择列表
 * Created by ZRP on 2017/7/19.
 */

public class Gift extends BaseData {

    private List<Integer> nobility_levelup_ids;       // 爵位进度还差100 弹窗的礼物ID列表
    private List<Integer> close_friends_ask_ids;      // 女性加密友索要的礼物ID列表
    private List<Integer> close_friends_send_ids;     // 男性加密友赠送的礼物ID列表
    private List<Integer> intimacy_100_levelup_ids;   // 密友进度还差100 弹窗的礼物ID列表
    private List<Integer> normal_friends_add_ids;     // 普通加好友礼物列表，视频版和直播版用
    private List<Integer> unlock_photo_ids;           // 私密照片的礼物ID列表
    private List<Integer> unlock_text_ids;            // 私密文字/语音的礼物ID列表
    private List<Integer> unlock_video_ids;           // 私密视频的礼物ID列表

    private List<Integer> secret_video_ids;           // 私密视频价格礼物
    private List<Integer> chat_ids;                   // 聊天礼物
    private List<Integer> live_ids;                   // 直播礼物
    private List<Integer> video_call_ids;             // 视频语音时候可选礼物

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObject(jsonStr);

        close_friends_ask_ids = new ArrayList<>();
        JSONArray friends_ask_array = jsonObject.optJSONArray("close_friends_ask_ids");
        for (int i = 0; friends_ask_array != null && i < friends_ask_array.length(); i++) {
            close_friends_ask_ids.add(friends_ask_array.optInt(i));
        }

        close_friends_send_ids = new ArrayList<>();
        JSONArray friends_send_array = jsonObject.optJSONArray("close_friends_send_ids");
        for (int i = 0; friends_send_array != null && i < friends_send_array.length(); i++) {
            close_friends_send_ids.add(friends_send_array.optInt(i));
        }

        intimacy_100_levelup_ids = new ArrayList<>();
        JSONArray intimacy_levelup_array = jsonObject.optJSONArray("intimacy_100_levelup_ids");
        for (int i = 0; intimacy_levelup_array != null && i < intimacy_levelup_array.length(); i++) {
            intimacy_100_levelup_ids.add(intimacy_levelup_array.optInt(i));
        }

        nobility_levelup_ids = new ArrayList<>();
        JSONArray nobility_levelup_array = jsonObject.optJSONArray("nobility_levelup_ids");
        for (int i = 0; nobility_levelup_array != null && i < nobility_levelup_array.length(); i++) {
            nobility_levelup_ids.add(nobility_levelup_array.optInt(i));
        }

        normal_friends_add_ids = new ArrayList<>();
        JSONArray friends_add_array = jsonObject.optJSONArray("normal_friends_add_ids");
        for (int i = 0; friends_add_array != null && i < friends_add_array.length(); i++) {
            normal_friends_add_ids.add(friends_add_array.optInt(i));
        }

        unlock_photo_ids = new ArrayList<>();
        JSONArray unlock_photo_array = jsonObject.optJSONArray("unlock_photo_ids");
        for (int i = 0; unlock_photo_array != null && i < unlock_photo_array.length(); i++) {
            unlock_photo_ids.add(unlock_photo_array.optInt(i));
        }

        unlock_text_ids = new ArrayList<>();
        JSONArray unlock_text_array = jsonObject.optJSONArray("unlock_text_ids");
        for (int i = 0; unlock_text_array != null && i < unlock_text_array.length(); i++) {
            unlock_text_ids.add(unlock_text_array.optInt(i));
        }

        unlock_video_ids = new ArrayList<>();
        JSONArray unlock_video_array = jsonObject.optJSONArray("unlock_video_ids");
        for (int i = 0; unlock_video_array != null && i < unlock_video_array.length(); i++) {
            unlock_video_ids.add(unlock_video_array.optInt(i));
        }

        secret_video_ids = new ArrayList<>();
        JSONArray secret_video_array = jsonObject.optJSONArray("secret_video_ids");
        for (int i = 0; secret_video_array != null && i < secret_video_array.length(); i++) {
            secret_video_ids.add(secret_video_array.optInt(i));
        }

        chat_ids = new ArrayList<>();
        JSONArray chat_array = jsonObject.optJSONArray("chat_ids");
        for (int i = 0; chat_array != null && i < chat_array.length(); i++) {
            chat_ids.add(chat_array.optInt(i));
        }

        live_ids = new ArrayList<>();
        JSONArray live_array = jsonObject.optJSONArray("live_ids");
        for (int i = 0; live_array != null && i < live_array.length(); i++) {
            live_ids.add(live_array.optInt(i));
        }

        video_call_ids = new ArrayList<>();
        JSONArray video_call_array = jsonObject.optJSONArray("video_call_ids");
        for (int i = 0; video_call_array != null && i < video_call_array.length(); i++) {
            video_call_ids.add(video_call_array.optInt(i));
        }
    }

    /**
     * @return 获取女性加密友索要的礼物ID列表
     */
    public List<Integer> getClose_friends_ask_ids() {
        return close_friends_ask_ids == null ? new ArrayList<Integer>() : close_friends_ask_ids;
    }

    /**
     * @return 获取女性加密友索要的礼物列表
     */
    public List<GiftsList.GiftInfo> getCloseFriendsAskGifts() {
        return getGiftsFromIds(getClose_friends_ask_ids());
    }

    /**
     * @return 获取男性加密友赠送的礼物ID列表
     */
    public List<Integer> getClose_friends_send_ids() {
        return close_friends_send_ids == null ? new ArrayList<Integer>() : close_friends_send_ids;
    }

    /**
     * @return 私密视频价格的礼物ID列表
     */
    public List<Integer> getSecret_video_ids() {
        return secret_video_ids == null ? new ArrayList<Integer>() : secret_video_ids;
    }
    /**
     * @return 聊天礼物ID列表
     */
    public List<Integer> getChat_ids() {
        return chat_ids == null ? new ArrayList<Integer>() : chat_ids;
    }
    /**
     * @return 视频语音时候可选礼物ID列表
     */
    public List<Integer> getLive_ids() {
        return live_ids == null ? new ArrayList<Integer>() : live_ids;
    }
    /**
     * @return 直播礼物ID列表
     */
    public List<Integer> getVideo_call_ids() {
        return video_call_ids == null ? new ArrayList<Integer>() : video_call_ids;
    }

    /**
     * @return 获取男性加密友赠送的礼物列表
     */
    public List<GiftsList.GiftInfo> getCloseFriendsSendGifts() {
        return getGiftsFromIds(getClose_friends_send_ids());
    }

    /**
     * @return 获取密友进度还差100 弹窗的礼物ID列表
     */
    public List<Integer> getIntimacy_100_levelup_ids() {
        return intimacy_100_levelup_ids == null ? new ArrayList<Integer>() : intimacy_100_levelup_ids;
    }

    /**
     * @return 获取爵位进度还差100 弹窗的礼物ID列表
     */
    public List<Integer> getNobility_levelup_ids() {
        return nobility_levelup_ids == null ? new ArrayList<Integer>() : nobility_levelup_ids;
    }

    /**
     * @return 获取密友进度还差100 弹窗的礼物列表
     */
    public List<GiftsList.GiftInfo> getIntimacyLevelupGifts() {
        return getGiftsFromIds(getIntimacy_100_levelup_ids());
    }

    /**
     * @return 获取密友进度还差100 弹窗的礼物列表
     */
    public List<GiftsList.GiftInfo> getNobilityLevelupGifts() {
        return getGiftsFromIds(getNobility_levelup_ids());
    }

    /**
     * @return 获取私密视频价格礼物列表
     */
    public List<GiftsList.GiftInfo> getSecretVideoGifts() {
        return getGiftsFromIds(getSecret_video_ids());
    }

    /**
     * @return 聊天礼物列表
     */
    public List<GiftsList.GiftInfo> getChatGifts() {
        return getGiftsFromIds(getChat_ids());
    }

    /**
     * @return 视频语音时候可选礼物列表
     */
    public List<GiftsList.GiftInfo> getVideoCallGifts() {
        return getGiftsFromIds(getVideo_call_ids());
    }

    /**
     * @return 直播礼物列表
     */
    public List<GiftsList.GiftInfo> getLiveGifts() {
        return getGiftsFromIds(getLive_ids());
    }

    /**
     * @return 获取普通加好友礼物列表，视频版和直播版用
     */
    public List<Integer> getNormal_friends_add_ids() {
        return normal_friends_add_ids == null ? new ArrayList<Integer>() : normal_friends_add_ids;
    }

    /**
     * @return 获取普通加好友礼物列表，视频版和直播版用
     */
    public List<GiftsList.GiftInfo> getNormalFriendsAddGifts() {
        return getGiftsFromIds(getNormal_friends_add_ids());
    }

    /**
     * @return 获取私密照片的礼物ID列表
     */
    public List<Integer> getUnlock_photo_ids() {
        return unlock_photo_ids == null ? new ArrayList<Integer>() : unlock_photo_ids;
    }

    /**
     * @return 获取私密照片的礼物列表
     */
    public List<GiftsList.GiftInfo> getUnlockPhotoGifts() {
        return getGiftsFromIds(getUnlock_photo_ids());
    }

    /**
     * @return 获取私密文字/语音的礼物ID列表
     */
    public List<Integer> getUnlock_text_ids() {
        return unlock_text_ids == null ? new ArrayList<Integer>() : unlock_text_ids;
    }

    /**
     * @return 获取私密文字/语音的礼物列表
     */
    public List<GiftsList.GiftInfo> getUnlockTextGifts() {
        return getGiftsFromIds(getUnlock_text_ids());
    }

    /**
     * @return 获取私密视频的礼物ID列表
     */
    public List<Integer> getUnlock_video_ids() {
        return unlock_video_ids == null ? new ArrayList<Integer>() : unlock_video_ids;
    }

    /**
     * @return 获取私密视频的礼物列表
     */
    public List<GiftsList.GiftInfo> getUnlockVideoGifts() {
        return getGiftsFromIds(getUnlock_video_ids());
    }

    /**
     * 根据礼物id列表获取礼物信息列表<p>
     * 在线配置请求回来之后有可能礼物信息还未请求返回[礼物接口需登录才能进行请求]，故公开此方法，外部在使用时单独进行查询
     *
     * @param ids 礼物id列表
     * @return 礼物信息列表
     */
    public List<GiftsList.GiftInfo> getGiftsFromIds(List<Integer> ids) {
        List<GiftsList.GiftInfo> giftInfos = new ArrayList<>();
        if (ids == null || ids.isEmpty()) return giftInfos;

        GiftsList giftLists = ModuleMgr.getCommonMgr().getGiftLists();
        GiftsList.GiftInfo info;
        for (Integer i : ids) {
            info = giftLists.getGiftInfo(i);
            if (info != null) giftInfos.add(info);
        }
        return giftInfos;
    }

    @Override
    public String toString() {
        return "Gift{" +
                "nobility_levelup_ids=" + nobility_levelup_ids +
                ", close_friends_ask_ids=" + close_friends_ask_ids +
                ", close_friends_send_ids=" + close_friends_send_ids +
                ", intimacy_100_levelup_ids=" + intimacy_100_levelup_ids +
                ", normal_friends_add_ids=" + normal_friends_add_ids +
                ", unlock_photo_ids=" + unlock_photo_ids +
                ", unlock_text_ids=" + unlock_text_ids +
                ", unlock_video_ids=" + unlock_video_ids +
                '}';
    }
}
