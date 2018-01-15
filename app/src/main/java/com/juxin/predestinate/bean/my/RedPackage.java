package com.juxin.predestinate.bean.my;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RedPackage {

    public int id;
    public int fuid;//拥有红包男用户的ID
    public int tuid;//解锁女用户的ID
    public int amount;//红包金额(单位分)
    public String avatar;
    public String nickname;
    public int status = 1;//红包状态, 1: 未领取， 2：已领取

    /**
     * 转成元
     *
     * @return
     */
    public String getAmountToString() {
        return String.format("%.2f", (amount / 100f));
    }

    public static RedPackage getRedPackage(String imJson) {
        if (TextUtils.isEmpty(imJson) || imJson.trim().length() == 0) {
            return null;
        }
        JSONObject object = JSON.parseObject(imJson);
        RedPackage p = new RedPackage();
        p.id = object.getIntValue("red_id");
        p.amount = object.getIntValue("red_money");
        return p;
    }

    public static List<RedPackage> getRedPackageList(String json) {
        List<RedPackage> list = new ArrayList<>();
        try {
            if (TextUtils.isEmpty(json) || json.trim().length() == 0) {
                return list;
            }
            JSONObject object = JSON.parseObject(json);
            if (object != null && object.get("res") != null) {
                String hongBaoArray = object.getJSONObject("res").getString("hongbaos");
                List<RedPackage> l = JSON.parseArray(hongBaoArray, RedPackage.class);
                return l != null ? l : list;
            }
//            JSONObject object = new JSONObject(json);
//            JSONArray array = object.getJSONObject("res").getJSONArray("hongbaos");
//            int size = array.length();
//            for(int i=0; i<size; i++){
//                JSONObject o = array.getJSONObject(i);
//                RedPackageMessage p = new RedPackageMessage();
//                p.id = o.getInt("id");
//                p.fuid = o.getInt("fuid");
//                p.tuid = o.getInt("tuid");
//                p.amount = o.getInt("amount");
//                p.nickname = o.getString("nickname");
//                p.avatar = o.getString("avatar");
//                p.status = o.getInt("status");
//                list.add(p);
//            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
    }
}
