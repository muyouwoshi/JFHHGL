package com.juxin.predestinate.ui.mail.unlock;

import android.os.Parcel;
import android.os.Parcelable;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 创建日期：2017/11/7
 * 描述:分享渠道
 *
 * @author :lc
 */
public class ShareChanelData extends BaseData implements Parcelable {
    /**
     * channel : 1:qq好友 2:qq空间 3:微信好友 4:微信朋友圈
     * text : ’1.0万人分享’
     * rest_time : 1
     */
    public static final int CHANEL_QQ = 1;
    public static final int CHANEL_QQK = 2;
    public static final int CHANEL_WX = 3;
    public static final int CHANEL_WXQ = 4;

    private int channel;
    private String text;
    private int rest_time;

    private Set<Integer> hashSet = new HashSet<>();
    private List<String> contentList = new ArrayList<>();
    private List<ShareChanelData> dataList = new ArrayList<>();

    @Override
    public void parseJson(String jsonStr) {
        try {
            JSONObject resJob = getJsonObjectRes(jsonStr);
            JSONArray dataArr = resJob.optJSONArray("counts");
            JSONArray desArr = resJob.optJSONArray("desc_text");
            for (int i = 0; i < dataArr.length(); i++) {
                ShareChanelData shareChanelData = new ShareChanelData();
                JSONObject job = dataArr.getJSONObject(i);
                shareChanelData.setChannel(job.optInt("channel"));
                shareChanelData.setText(job.optString("text"));
                shareChanelData.setRest_time(job.optInt("rest_time"));
                dataList.add(shareChanelData);
                hashSet.add(job.optInt("channel"));
            }
            contentList.add(desArr.optString(0));
            contentList.add(desArr.optString(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否有QQ
     */
    public int qq() {
        return hashSet != null && hashSet.contains(CHANEL_QQ) ? 1 : 0;
    }

    /**
     * 是否有QQZQone
     */
    public int qqK() {
        return hashSet != null && hashSet.contains(CHANEL_QQK) ? 1 : 0;
    }

    /**
     * 仅有微信和微信圈
     */
    public boolean wxAll() {
        return hashSet != null && hashSet.contains(CHANEL_WX) && hashSet.contains(CHANEL_WXQ);
    }

    /**
     * 是否有微信
     */
    public int wxFirend() {
        return hashSet != null && hashSet.contains(CHANEL_WX) ? 1 : 0;
    }

    /**
     * 是否有微信圈
     */
    public int wxCircle() {
        return hashSet != null && hashSet.contains(CHANEL_WXQ) ? 1 : 0;
    }

    /**
     * 通过渠道id 获取对应数据
     * @param channelId 渠道id
     */
    public ShareChanelData getChanelData(int channelId) {
        if (dataList != null && !dataList.isEmpty()) {
            for (ShareChanelData chanelData : dataList) {
                if (chanelData.getChannel() == channelId) {
                    return chanelData;
                }
            }
        }
        return new ShareChanelData();
    }

    public List<ShareChanelData> getDataList() {
        return dataList == null ? new ArrayList<ShareChanelData>() : dataList;
    }

    public void setDataList(List<ShareChanelData> dataList) {
        this.dataList = dataList;
    }

    public List<String> getContentList() {
        return contentList;
    }

    public void setContentList(List<String> contentList) {
        this.contentList = contentList;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRest_time() {
        return rest_time;
    }

    public void setRest_time(int rest_time) {
        this.rest_time = rest_time;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.channel);
        dest.writeString(this.text);
        dest.writeInt(this.rest_time);
    }

    public ShareChanelData() {
    }

    protected ShareChanelData(Parcel in) {
        this.channel = in.readInt();
        this.text = in.readString();
        this.rest_time = in.readInt();
    }

    public static final Creator<ShareChanelData> CREATOR = new Creator<ShareChanelData>() {
        @Override
        public ShareChanelData createFromParcel(Parcel source) {
            return new ShareChanelData(source);
        }

        @Override
        public ShareChanelData[] newArray(int size) {
            return new ShareChanelData[size];
        }
    };
}
