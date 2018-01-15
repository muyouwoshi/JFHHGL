package com.juxin.predestinate.ui.mail.unlock;

import com.juxin.predestinate.bean.net.BaseData;
import com.juxin.predestinate.module.util.ShareUtil;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 类描述：
 * 创建时间：2017/11/3 18:59
 * 修改时间：2017/11/3 18:59
 * Created by zhoujie on 2017/11/3
 * 修改备注：
 */

public class ShareCountData extends BaseData {
    public String wxShareCount = "0人分享";
    public String wxPCricleShareCount = "0人分享";
    public String qqShareCount = "0人分享";
    public String qqZoneShareCount = "0人分享";

    @Override
    public void parseJson(String jsonStr) {
        try {
            JSONObject object = getJsonObject(jsonStr).getJSONObject("res");
            JSONArray array = object.optJSONArray("counts");
            if (null == array || array.length() == 0) {
                return;
            }

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                int channel  = obj.optInt("channel");
                String count =  obj.optString("count");
                if(channel == ShareUtil.CHANNEL_WX_FRIEND){
                    wxShareCount = count;
                }
                else if(channel == ShareUtil.CHANNEL_WX_FRIEND_CRICLE){
                    wxPCricleShareCount = count;
                }
                else if(channel== ShareUtil.CHANNEL_QQ_FRIEND){
                    qqShareCount = count;
                }
                else if(channel == ShareUtil.CHANNEL_QQ_ZONE){
                    qqZoneShareCount = count;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
