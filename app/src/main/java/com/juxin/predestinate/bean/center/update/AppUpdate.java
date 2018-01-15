package com.juxin.predestinate.bean.center.update;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 软件更新接口返回数据结构体
 */
public class AppUpdate extends BaseData {

    private String title;       //标题，显示软件的版本
    private String summary;     //升级描述信息
    private String url;         //升级包下载地址
    private String package_name;//下载包的包名
    private int version;        //软件版本号
    private int force;          //是否为强制升级：1-强制升级，2-可选

    // 2017.11.3新增参数

    private String top_img_url; // 升级弹窗顶部图片地址
    private String mid_img_url; // 升级包icon图片地址
    private String foot_text;   // 弹窗底部提示文字

    @Override
    public void parseJson(String s) {
        JSONObject jsonObject = getJsonObject(s);
        this.setTitle(jsonObject.optString("title"));
        this.setSummary(jsonObject.optString("summary"));
        this.setPackage_name(jsonObject.optString("newpackname"));
        this.setUrl(jsonObject.optString("url"));
        this.setVersion(jsonObject.optInt("ver"));
        this.setForce(jsonObject.optInt("force"));

        this.setTop_img_url(jsonObject.optString("top_img_url"));
        this.setMid_img_url(jsonObject.optString("mid_img_url"));
        this.setFoot_text(jsonObject.optString("foot_text"));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getForce() {
        return force;
    }

    public void setForce(int force) {
        this.force = force;
    }

    public String getTop_img_url() {
        return top_img_url;
    }

    public void setTop_img_url(String top_img_url) {
        this.top_img_url = top_img_url;
    }

    public String getMid_img_url() {
        return mid_img_url;
    }

    public void setMid_img_url(String mid_img_url) {
        this.mid_img_url = mid_img_url;
    }

    public String getFoot_text() {
        return foot_text;
    }

    public void setFoot_text(String foot_text) {
        this.foot_text = foot_text;
    }

    @Override
    public String toString() {
        return "AppUpdate{" +
                "title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", url='" + url + '\'' +
                ", package_name='" + package_name + '\'' +
                ", version=" + version +
                ", force=" + force +
                ", top_img_url='" + top_img_url + '\'' +
                ", mid_img_url='" + mid_img_url + '\'' +
                ", foot_text='" + foot_text + '\'' +
                '}';
    }
}
