package com.juxin.predestinate.module.local.msgview.smile;

import com.juxin.predestinate.bean.net.BaseData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kind on 2017/3/31.
 */
public class SmilePackage extends BaseData {

    private int id = 0;
    private String type = null;//表情类型 smallface gift 暂时有四种 smallface的列表需要从资源中另外填充
    private String name = null;
    private String icon = null;
    private String pic = null;
    private int gender = 0; //分组所属性别1男 2女 0通用
    private List<SmileItem> items = null;

    @Override
    public void parseJson(String s) {
        JSONObject json = getJsonObject(s);

        id = json.optInt("id");
        type = json.optString("Type");
        name = json.optString("name");
        icon = json.optString("ico");
        pic = json.optString("pic");
        gender = json.optInt("gender");

        items = (List<SmileItem>) getBaseDataList(json.optJSONArray("list"), SmileItem.class);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 特殊情况下，返回默认图片。
     *
     * @return
     */
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

    public List<SmileItem> getItems() {
        return items;
    }

    public void setItems(List<SmileItem> items) {
        this.items = items;
    }

    public SmilePackage clone(boolean sex) {
        if (gender != 0) {
            if (sex && gender != 1) {
                return null;
            } else if (!sex && gender != 2) {
                return null;
            }
        }

        List<SmileItem> items = cloneItems(sex);

        if (!"smallface".equals(getType()) && !"gift".equals(getType())) {
            if (items == null || items.isEmpty()) {
                return null;
            }
        }

        SmilePackage smilePackage = new SmilePackage();

        smilePackage.id = id;
        smilePackage.type = type;
        smilePackage.name = name;

        if (sex) {
            smilePackage.icon = icon;
            smilePackage.pic = pic;
        } else {
            smilePackage.icon = pic;
            smilePackage.pic = icon;
        }

        smilePackage.gender = gender;
        smilePackage.items = items;

        return smilePackage;
    }

    private List<SmileItem> cloneItems(boolean sex) {
        if (items == null) {
            return null;
        }


        List<SmileItem> temp = new ArrayList<SmileItem>();

        for (SmileItem item : items) {
            if (item.getGender() != 0) {
                if (sex && item.getGender() != 1) {
                    continue;
                } else if (!sex && item.getGender() != 2) {
                    continue;
                }
            }

            temp.add(item);
        }

        return temp;
    }

    public void parseJsonSmileItem(String s)throws JSONException {
        JSONObject json = getJsonObject(s);
        JSONObject jsonObject = json.optJSONObject("res");
        if(items == null){
            items = new ArrayList<>();
        }else {
            items.clear();
        }
        if (!jsonObject.isNull("list")) {
            JSONArray jsonArray = jsonObject.optJSONArray("list");
            if (null != jsonArray) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    SmileItem smileItem = new SmileItem();
                    smileItem.setPic(jsonArray.getString(i));
                    items.add(smileItem);
                }
            }
        }
    }
}
