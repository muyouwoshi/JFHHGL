package com.juxin.predestinate.bean.center.user.detail;

import android.os.Parcel;
import android.os.Parcelable;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

import java.util.List;

/**
 * 创建日期：2017/7/18
 * 描述:女号挣钱信息
 * 作者:lc
 */
public class MyEarnInfo extends BaseData implements Parcelable {
    private int basicincome;            //基础收益
    private int activebonus;            //活跃奖金
    private int tasksubsidy;            //任务补贴
    private int estimatedincome;        //预估收入
    private int activelevel;            //活跃等级
    private String activescore;         //活跃奖金计算公式
    private int videoincome;            //视频收入
    private int videofulfil;            //视频收益完成数,单位钻石
    private int videotask;              //完成视频任务需，单位钻石
    private int voiceincome;            //语音收入
    private int voicefulfil;            //语音收益完成数,单位钻石
    private int voicetask;              //完成语音任务数，单位钻石
    private int giftincome;             //礼物收入
    private int gifttask;               //完成礼物收入任务数，单位钻石
    private int giftfulfil;             //礼物收益完成数，单位钻石
    private int videotaskbonus;                 //视频任务补贴，单位分
    private int voicetaskbonus;                 //语音任务补贴，单位分
    private int gifttaskbonus;                  //礼物任务补贴，单位分
    private int videotaskcomplished;            //是否完成视频收益任务	1=完成 0=未完成
    private int voicetaskcomplished;            //是否完成语音任务		1=完成 0=未完成
    private int gifttaskcomplished;             //是否完成礼物任务		1=完成 0=未完成
    private List<LevelBonusrat> levellist;      //等级列表

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObj = getJsonObject(jsonStr);
        String status = jsonObj.optString("status");
        if ("ok".equals(status)) {
            String resStr = jsonObj.optString("res");
            JSONObject resJob = getJsonObject(resStr);
            setBasicincome(resJob.optInt("basicincome"));
            setActivebonus(resJob.optInt("activebonus"));
            setTasksubsidy(resJob.optInt("tasksubsidy"));
            setEstimatedincome(resJob.optInt("estimatedincome"));
            setActivelevel(resJob.optInt("activelevel"));
            setActivescore(resJob.optString("activescore"));
            setVideoincome(resJob.optInt("videoincome"));
            setVideofulfil(resJob.optInt("videofulfil"));
            setVideotask(resJob.optInt("videotask"));
            setVoiceincome(resJob.optInt("voiceincome"));
            setVoicefulfil(resJob.optInt("voicefulfil"));
            setVoicetask(resJob.optInt("voicetask"));
            setGiftincome(resJob.optInt("giftincome"));
            setGifttask(resJob.optInt("gifttask"));
            setGiftfulfil(resJob.optInt("giftfulfil"));
            setVideotaskbonus(resJob.optInt("videotaskbonus"));
            setVoicetaskbonus(resJob.optInt("voicetaskbonus"));
            setGifttaskbonus(resJob.optInt("gifttaskbonus"));
            setVideotaskcomplished(resJob.optInt("videotaskcomplished"));
            setVoicetaskcomplished(resJob.optInt("voicetaskcomplished"));
            setGifttaskcomplished(resJob.optInt("gifttaskcomplished"));

            levellist = (List<LevelBonusrat>) getBaseDataList(getJsonArray(resJob.optString("levellist")), LevelBonusrat.class);
        }
    }

    public LevelBonusrat getLevelBonusrat(int id) {
        if(levellist == null || levellist.size() == 0) return new LevelBonusrat();
        for (LevelBonusrat level : levellist) {
            if(id == level.getLevel()) {
                return level;
            }
        }
        return new LevelBonusrat();
    }

    public float getFEstimatedincome() {
        return estimatedincome/100f;
    }

    public float getFBasicincome() {
        return basicincome/100f;
    }

    public float getFActivebonus() {
        return activebonus/100f;
    }

    public float getFTasksubsidy() {
        return tasksubsidy/100f;
    }

    public int getBasicincome() {
        return basicincome;
    }

    public void setBasicincome(int basicincome) {
        this.basicincome = basicincome;
    }

    public int getActivebonus() {
        return activebonus;
    }

    public void setActivebonus(int activebonus) {
        this.activebonus = activebonus;
    }

    public int getTasksubsidy() {
        return tasksubsidy;
    }

    public void setTasksubsidy(int tasksubsidy) {
        this.tasksubsidy = tasksubsidy;
    }

    public int getEstimatedincome() {
        return estimatedincome;
    }

    public void setEstimatedincome(int estimatedincome) {
        this.estimatedincome = estimatedincome;
    }

    public int getActivelevel() {
        return activelevel;
    }

    public void setActivelevel(int activelevel) {
        this.activelevel = activelevel;
    }

    public String getActivescore() {
        return activescore;
    }

    public void setActivescore(String activescore) {
        this.activescore = activescore;
    }

    public int getVideoincome() {
        return videoincome;
    }

    public void setVideoincome(int videoincome) {
        this.videoincome = videoincome;
    }

    public int getVideofulfil() {
        return videofulfil;
    }

    public void setVideofulfil(int videofulfil) {
        this.videofulfil = videofulfil;
    }

    public int getVideotask() {
        return videotask;
    }

    public void setVideotask(int videotask) {
        this.videotask = videotask;
    }

    public int getVoiceincome() {
        return voiceincome;
    }

    public void setVoiceincome(int voiceincome) {
        this.voiceincome = voiceincome;
    }

    public int getVoicefulfil() {
        return voicefulfil;
    }

    public void setVoicefulfil(int voicefulfil) {
        this.voicefulfil = voicefulfil;
    }

    public int getVoicetask() {
        return voicetask;
    }

    public void setVoicetask(int voicetask) {
        this.voicetask = voicetask;
    }

    public int getGiftincome() {
        return giftincome;
    }

    public void setGiftincome(int giftincome) {
        this.giftincome = giftincome;
    }

    public int getGifttask() {
        return gifttask;
    }

    public void setGifttask(int gifttask) {
        this.gifttask = gifttask;
    }

    public int getGiftfulfil() {
        return giftfulfil;
    }

    public void setGiftfulfil(int giftfulfil) {
        this.giftfulfil = giftfulfil;
    }

    public int getVideotaskbonus() {
        return videotaskbonus;
    }

    public void setVideotaskbonus(int videotaskbonus) {
        this.videotaskbonus = videotaskbonus;
    }

    public int getVoicetaskbonus() {
        return voicetaskbonus;
    }

    public void setVoicetaskbonus(int voicetaskbonus) {
        this.voicetaskbonus = voicetaskbonus;
    }

    public int getGifttaskbonus() {
        return gifttaskbonus;
    }

    public void setGifttaskbonus(int gifttaskbonus) {
        this.gifttaskbonus = gifttaskbonus;
    }

    public int getVideotaskcomplished() {
        return videotaskcomplished;
    }

    public void setVideotaskcomplished(int videotaskcomplished) {
        this.videotaskcomplished = videotaskcomplished;
    }

    public int getVoicetaskcomplished() {
        return voicetaskcomplished;
    }

    public void setVoicetaskcomplished(int voicetaskcomplished) {
        this.voicetaskcomplished = voicetaskcomplished;
    }

    public int getGifttaskcomplished() {
        return gifttaskcomplished;
    }

    public void setGifttaskcomplished(int gifttaskcomplished) {
        this.gifttaskcomplished = gifttaskcomplished;
    }

    public List<LevelBonusrat> getLevellist() {
        return levellist;
    }

    public void setLevellist(List<LevelBonusrat> levellist) {
        this.levellist = levellist;
    }

    public static Creator<MyEarnInfo> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.basicincome);
        dest.writeInt(this.activebonus);
        dest.writeInt(this.tasksubsidy);
        dest.writeInt(this.estimatedincome);
        dest.writeInt(this.activelevel);
        dest.writeString(this.activescore);
        dest.writeInt(this.videoincome);
        dest.writeInt(this.videofulfil);
        dest.writeInt(this.videotask);
        dest.writeInt(this.voiceincome);
        dest.writeInt(this.voicefulfil);
        dest.writeInt(this.voicetask);
        dest.writeInt(this.giftincome);
        dest.writeInt(this.gifttask);
        dest.writeInt(this.giftfulfil);
        dest.writeInt(this.videotaskbonus);
        dest.writeInt(this.voicetaskbonus);
        dest.writeInt(this.gifttaskbonus);
        dest.writeInt(this.videotaskcomplished);
        dest.writeInt(this.voicetaskcomplished);
        dest.writeInt(this.gifttaskcomplished);
        dest.writeTypedList(this.levellist);
    }

    public MyEarnInfo() {
    }

    protected MyEarnInfo(Parcel in) {
        this.basicincome = in.readInt();
        this.activebonus = in.readInt();
        this.tasksubsidy = in.readInt();
        this.estimatedincome = in.readInt();
        this.activelevel = in.readInt();
        this.activescore = in.readString();
        this.videoincome = in.readInt();
        this.videofulfil = in.readInt();
        this.videotask = in.readInt();
        this.voiceincome = in.readInt();
        this.voicefulfil = in.readInt();
        this.voicetask = in.readInt();
        this.giftincome = in.readInt();
        this.gifttask = in.readInt();
        this.giftfulfil = in.readInt();
        this.videotaskbonus = in.readInt();
        this.voicetaskbonus = in.readInt();
        this.gifttaskbonus = in.readInt();
        this.videotaskcomplished = in.readInt();
        this.voicetaskcomplished = in.readInt();
        this.gifttaskcomplished = in.readInt();
        this.levellist = in.createTypedArrayList(LevelBonusrat.CREATOR);
    }

    public static final Creator<MyEarnInfo> CREATOR = new Creator<MyEarnInfo>() {
        @Override
        public MyEarnInfo createFromParcel(Parcel source) {
            return new MyEarnInfo(source);
        }

        @Override
        public MyEarnInfo[] newArray(int size) {
            return new MyEarnInfo[size];
        }
    };
}
