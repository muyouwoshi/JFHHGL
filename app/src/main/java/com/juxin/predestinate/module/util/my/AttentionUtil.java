package com.juxin.predestinate.module.util.my;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.juxin.library.log.PSP;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.my.AttentionList;
import com.juxin.predestinate.bean.my.AttentionUserDetail;
import com.juxin.predestinate.bean.my.AttentionUserDetailList;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zm on 2017/5/4
 */
public class AttentionUtil {

    private static final String USERSKEY = "USERSKEY";
    private static final String USERSIDKEY = "USERSIDKEY";
    private static Map<Long, AttentionUserDetail> userInfos = new HashMap<>();
    private static List<AttentionUserDetail> userDetails = new ArrayList<>();
    private static List<AttentionUserDetail> userMyDetails = new ArrayList<>();
    private static String strUserIds = "";

    //初始化
    public static void initUserDetails() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (userInfos.size() <= 0) {
                    strUserIds = PSP.getInstance().getString(USERSIDKEY + App.uid, "");
                    if (TextUtils.isEmpty(strUserIds))
                        ModuleMgr.getCommonMgr().getFollowing(new RequestComplete() {
                            @Override
                            public void onRequestComplete(HttpResponse response) {
                                AttentionList lists = new AttentionList();
                                lists.parseJson(response.getResponseString());
                                List<AttentionList.AttentionInfo> infos = lists.getArr_lists();
                                saveUserIdsById(infos);
                            }
                        });

                    String jsonStr = PSP.getInstance().getString(USERSKEY + App.uid, "");
                    AttentionUserDetailList list = new AttentionUserDetailList();
                    list.parseJson(jsonStr);
                    List<AttentionUserDetail> userDetails = list.getAttentionUserDetailList();
                    for (AttentionUserDetail detail : userDetails) {
                        userInfos.put(detail.getUid(), detail);
                    }
                }
            }
        }).start();
    }

    public static void saveUserDetails() {
        List<AttentionUserDetail> userDetails = new ArrayList<>();
        for (Long uid : userInfos.keySet()) {
            userDetails.add(userInfos.get(uid));
        }
        String jsonStr = JSON.toJSONString(userDetails);
        PSP.getInstance().put(USERSKEY + ModuleMgr.getCenterMgr().getMyInfo().getUid(), jsonStr);
    }

    public synchronized static void saveUserIdsById(List<AttentionList.AttentionInfo> userIds) {
        if (userIds == null)
            return;
        strUserIds = "";
        for (int i = 0; i < userIds.size(); i++) {
            strUserIds = strUserIds + userIds.get(i).getUid() + "LL";
        }
        PSP.getInstance().put(USERSIDKEY + App.uid, strUserIds);
    }

    public synchronized static void saveUserIds(AttentionUserDetail userId) {
        if (userId == null)
            return;
        strUserIds = strUserIds + userId.getUid() + "LL";
        PSP.getInstance().put(USERSIDKEY + App.uid, strUserIds);
    }

    public synchronized static void deleteUserId(AttentionUserDetail userId) {
        if (userId == null)
            return;
        strUserIds = strUserIds.replace(userId.getUid() + "LL","");
        PSP.getInstance().put(USERSIDKEY + App.uid, strUserIds);
    }

    public static boolean isContainsUid(Long uid) {
        return strUserIds.contains(uid+"LL");
    }

    /**
     * 返回内存中存储的用户信息
     *
     * @return
     */
    public static void updateUserDetails(String jsonStr) {
        UserDetail detail = new UserDetail();
        AttentionUserDetail userDetail = new AttentionUserDetail();
        detail.parseJson(jsonStr);
        userDetail.parse(detail);
        if (userInfos.containsKey(userDetail.getUid())) {
            userInfos.put(userDetail.getUid(), userDetail);
            saveUserDetails();
        }
    }

    public static void updateUserDetails(UserDetail detail) {
        AttentionUserDetail userDetail = new AttentionUserDetail();
        userDetail.parse(detail);
        if (userInfos.containsKey(userDetail.getUid())) {
            userInfos.put(userDetail.getUid(), userDetail);
            saveUserDetails();
        }
    }
}