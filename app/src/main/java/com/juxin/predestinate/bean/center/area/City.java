package com.juxin.predestinate.bean.center.area;


import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * The type City.
 * Created by ZRP on 2016/11/21.
 */
public class City extends BaseData {

    private String city;
    private String cityName;
    private int cityID;
    private String province;
    private String provinceName;
    private int provinceID;

    public City() {
    }

    public City(String city, int cityID, String province, int provinceID) {
        this.city = city;
        this.cityName = city;
        this.cityID = cityID;
        this.province = province;
        this.provinceName = province;
        this.provinceID = provinceID;
    }

    @Override
    public void parseJson(String s) {
        JSONObject jsonObject = getJsonObject(s);
        this.setCity(jsonObject.optString("city"));
        this.setCityName(jsonObject.optString("cityName"));
        this.setCityID(jsonObject.optInt("cityID"));
        this.setProvince(jsonObject.optString("province"));
        this.setProvinceName(jsonObject.optString("provinceName"));
        this.setProvinceID(jsonObject.optInt("provinceID"));
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(int provinceID) {
        this.provinceID = provinceID;
    }

    @Override
    public String toString() {
        return "City{" +
                "city='" + city + '\'' +
                ", cityName='" + cityName + '\'' +
                ", cityID=" + cityID +
                ", province='" + province + '\'' +
                ", provinceName='" + provinceName + '\'' +
                ", provinceID=" + provinceID +
                '}';
    }
}
