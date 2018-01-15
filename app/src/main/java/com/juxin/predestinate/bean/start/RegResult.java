package com.juxin.predestinate.bean.start;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 注册解析类
 * Created YAO on 2017/4/25.
 */

public class RegResult extends BaseData {
    private String username;//账号
    private String password;//密码

    private long promotionuid; //注册返回的推荐用户的Uid

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getPromotionuid() {
        return promotionuid;
    }

    public void setPromotionuid(long promotionuid) {
        this.promotionuid = promotionuid;
    }

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonFirst = getJsonObject(jsonStr);
        JSONObject jsonNext = jsonFirst.optJSONObject("user_account");
        this.username = jsonNext.optString("username");
        this.password = jsonNext.optString("password");
        this.setPromotionuid(jsonNext.optLong("promotionuid"));
    }
}
