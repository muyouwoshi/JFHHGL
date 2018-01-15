package com.juxin.predestinate.module.local.msgview.smile;

import com.juxin.predestinate.bean.net.BaseData;
import org.json.JSONObject;

/**
 * 表情实体类
 * Created by Kind on 2017/3/31.
 */
public class SmileItem extends BaseData {

    private int id = 0;
    private String name = null;
    private String icon = null;
    private String pic = null;
    private int gender = 0; //分组所属性别1男 2女 0通用


    public SmileItem() {}

    public SmileItem(String pic) {
        this.pic = pic;
    }

    @Override
    public void parseJson(String s) {
        JSONObject json = getJsonObject(s);

        id = json.optInt("id");
        name = json.optString("name");
        icon = json.optString("ico");
        pic = json.optString("pic");
        gender = json.optInt("gender");

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

}
