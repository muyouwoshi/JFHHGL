package com.juxin.predestinate.bean.discover;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2017/9/4
 * 描述:首页banner
 * 作者:lc
 */
public class BannerConfig extends BaseData {

    private List<Banner> banner_config;

    @Override
    public void parseJson(String jsonStr) {
        String jsonData = getJsonObject(jsonStr).optString("res");
        JSONObject jsonObject = getJsonObject(jsonData);

        this.banner_config = (List<Banner>) getBaseDataList(jsonObject.optJSONArray("banner_config"), Banner.class);
    }

    public List<Banner> getBanner_config() {
        return banner_config == null ? new ArrayList<Banner>() : banner_config;
    }

    public void setBanner_config(List<Banner> banner_config) {
        this.banner_config = banner_config;
    }

    public static class Banner extends BaseData {
        private String h5_url;
        private String pic_url;

        public Banner() {
        }

        @Override
        public void parseJson(String jsonStr) {
            JSONObject jsonObject = getJsonObject(jsonStr);
            this.setH5_url(jsonObject.optString("h5_url"));
            this.setPic_url(jsonObject.optString("pic_url"));
        }

        public String getH5_url() {
            return h5_url;
        }

        public void setH5_url(String h5_url) {
            this.h5_url = h5_url;
        }

        public String getPic_url() {
            return pic_url;
        }

        public void setPic_url(String pic_url) {
            this.pic_url = pic_url;
        }
    }
}
