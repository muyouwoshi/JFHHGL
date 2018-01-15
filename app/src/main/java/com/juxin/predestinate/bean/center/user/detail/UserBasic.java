package com.juxin.predestinate.bean.center.user.detail;

import android.os.Parcel;
import android.os.Parcelable;

import com.juxin.predestinate.bean.net.BaseData;
import com.juxin.predestinate.module.logic.config.AreaConfig;
import com.juxin.predestinate.module.logic.config.InfoConfig;

import org.json.JSONObject;

/**
 * 用户信息基类
 */
public class UserBasic extends BaseData implements Parcelable {
    private int age;             // 年龄
    private String avatar;       // 头像
    private int avatar_status;   // 头像状态 0为未审核，1为通过，2为拒绝,3未上传 4 好 5 很好
    private String nickname;     // 昵称
    public long uid;             // 用户Id
    private int gender;          // 性别 1男2女
    private String weight;       // 体重
    private String edu;          // 学历  1:初中及以下 2：高中及中专 3，大专 4 ，本科 5：硕士及以上
    private String income;       // 收入情况
    private String job;          // 工作情况
    private String marry;        // 情感状态  2:未婚， 3:离异， 4:丧偶
    private int height;          // 身高
    private String star;         // 星座
    private String birthday;     // 生日

    // 地址
    private String province;      // 完整省份
    private String city;          // 完整城市
    private String provinceName;  // 展示省份
    private String cityName;      // 展示城市
    private int scity;            // 城市代码
    private int sprovince;        // 省份代码

    public static final Creator<UserBasic> CREATOR = new Creator<UserBasic>() {
        @Override
        public UserBasic createFromParcel(Parcel in) {
            return new UserBasic(in);
        }

        @Override
        public UserBasic[] newArray(int size) {
            return new UserBasic[size];
        }
    };

    @Override
    public void parseJson(String s) {
        InfoConfig infoConfig = InfoConfig.getInstance();
        JSONObject detailObject = getJsonObject(s);

        this.setAge(detailObject.optInt("age"));
        this.setAvatar(detailObject.optString("avatar"));
        this.setAvatar_status(detailObject.optInt("avatarstatus"));
        this.setUid(detailObject.optLong("uid"));
        this.setNickname(detailObject.optString("nickname"));
        this.setGender(detailObject.optInt("gender"));
        this.setBirthday(detailObject.optString("birthday"));
        this.setHeight(detailObject.optInt("height"));

        int marry = detailObject.optInt("marry");
        this.setWeight(infoConfig.getWeight().getShowWithSubmit(detailObject.optInt("weight")));
        this.setStar(infoConfig.getStar().getShowWithSubmit(detailObject.optInt("star")));
        this.setEdu(infoConfig.getEdu().getShowWithSubmit(detailObject.optInt("edu")));
        this.setJob(infoConfig.getJob().getShowWithSubmit(detailObject.optInt("job")));
        this.setIncome(infoConfig.getIncome().getShowWithSubmit(detailObject.optInt("income")));
        this.setMarry(infoConfig.getMarry().getShowWithSubmit(marry < 2 ? 2 : marry));

        int pid = detailObject.optInt("province");
        int cit = detailObject.optInt("city");
        this.setScity(cit);
        this.setSprovince(pid);
        this.setProvince(AreaConfig.getInstance().getProvinceByID(pid));
        this.setCity(AreaConfig.getInstance().getCityByID(cit));
        this.setProvinceName(AreaConfig.getInstance().getProvinceNameByID(pid));
        this.setCityName(AreaConfig.getInstance().getCityNameByID(cit));

    }

    // ------------ 工具方法 --------------

    /**
     * 判断用户是否为男性
     *
     * @return true 男性
     */
    public boolean isMan() {
        return getGender() == 1;
    }

    /**
     * 获取简略省市拼接地址
     */
    public String getAddressShow() {
        String _province = (provinceName == null || "不限".equals(provinceName)) ? "" : provinceName;
        String _city = (cityName == null || "不限".equals(cityName)) ? "" : " " + cityName;
        return _province + _city;
    }

    /**
     * 获取省市拼接地址
     */
    public String getAddress() {
        String _province = (province == null || "不限".equals(province)) ? "" : province;
        String _city = (city == null || "不限".equals(city)) ? "" : " " + city;
        return _province + _city;
    }

    // ------------ Getter  Setter --------------
    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getAvatar_status() {
        return avatar_status;
    }

    public void setAvatar_status(int avatar_status) {
        this.avatar_status = avatar_status;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getEdu() {
        return "不限".equals(edu) ? "" : edu;
    }

    public void setEdu(String edu) {
        this.edu = edu;
    }

    public String getIncome() {
        return "不限".equals(income) ? "" : income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getMarry() {
        return marry;
    }

    public void setMarry(String marry) {
        this.marry = marry;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getScity() {
        return scity;
    }

    public void setScity(int scity) {
        this.scity = scity;
    }

    public int getSprovince() {
        return sprovince;
    }

    public void setSprovince(int sprovince) {
        this.sprovince = sprovince;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.age);
        dest.writeString(this.avatar);
        dest.writeInt(this.avatar_status);
        dest.writeString(this.nickname);
        dest.writeLong(this.uid);
        dest.writeInt(this.gender);
        dest.writeString(this.weight);
        dest.writeString(this.edu);
        dest.writeString(this.income);
        dest.writeString(this.job);
        dest.writeString(this.marry);
        dest.writeInt(this.height);
        dest.writeString(this.star);
        dest.writeString(this.birthday);
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.provinceName);
        dest.writeString(this.cityName);
        dest.writeInt(this.scity);
        dest.writeInt(this.sprovince);
    }

    public UserBasic() {
    }

    protected UserBasic(Parcel in) {
        this.age = in.readInt();
        this.avatar = in.readString();
        this.avatar_status = in.readInt();
        this.nickname = in.readString();
        this.uid = in.readLong();
        this.gender = in.readInt();
        this.weight = in.readString();
        this.edu = in.readString();
        this.income = in.readString();
        this.job = in.readString();
        this.marry = in.readString();
        this.height = in.readInt();
        this.star = in.readString();
        this.birthday = in.readString();
        this.province = in.readString();
        this.city = in.readString();
        this.provinceName = in.readString();
        this.cityName = in.readString();
        this.scity = in.readInt();
        this.sprovince = in.readInt();
    }

}
