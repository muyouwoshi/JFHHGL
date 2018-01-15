package com.juxin.predestinate.bean.center.area;


import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * The type Province.
 * Created by @author ZRP on 2016/11/21.
 */
public class Province extends BaseData {

    private String province;
    private String provinceName;
    private int provinceID;
    private ArrayList<City> cities;

    public Province() {
    }

    public Province(String province, int provinceID, ArrayList<City> cities) {
        this.province = province;
        this.provinceName = province;
        this.provinceID = provinceID;
        this.cities = cities;
    }

    @Override
    public void parseJson(String s) {
        JSONObject jsonObject = getJsonObject(s);
        this.setProvince(jsonObject.optString("province"));
        this.setProvinceName(jsonObject.optString("provinceName"));
        this.setProvinceID(jsonObject.optInt("provinceID"));
        this.setCities((ArrayList<City>) getBaseDataList(jsonObject.optJSONArray("cities"), City.class));
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

    public ArrayList<City> getCities() {
        return cities;
    }

    public void setCities(ArrayList<City> cities) {
        this.cities = cities;
    }
}
