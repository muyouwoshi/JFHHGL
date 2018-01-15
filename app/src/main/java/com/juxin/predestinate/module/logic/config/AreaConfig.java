package com.juxin.predestinate.module.logic.config;

import android.text.TextUtils;

import com.juxin.library.utils.FileUtil;
import com.juxin.predestinate.bean.center.area.City;
import com.juxin.predestinate.bean.center.area.Province;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.bean.net.BaseData;

import java.util.ArrayList;

/**
 * 地区管理类
 */
public class AreaConfig extends BaseData {

    private ArrayList<Province> provinces = new ArrayList<>();
    private ArrayList<Province> temp = new ArrayList<>();

    private volatile static AreaConfig instance = null;

    private AreaConfig() {
    }

    public static AreaConfig getInstance() {
        if (instance == null) {
            synchronized (AreaConfig.class) {
                if (instance == null) {
                    instance = new AreaConfig();
                    instance.parseJson(FileUtil.getFromAssets(App.getActivity(), "info_cities.json"));
                }
            }
        }
        return instance;
    }

    // =================初始化完成==================

    /**
     * @return 获取json原始串，测试时调用，正常使用请勿调用
     */
    public ArrayList<Province> getProvinces() {
        return provinces;
    }

    /**
     * 获取默认显示的省市列表，如果某省下无市级展示，就填充不限
     *
     * @return 省市列表
     */
    public ArrayList<Province> getCommonProvince() {
        temp.clear();
        temp.addAll(provinces);
        for (Province pro : temp) {
            ArrayList<City> cityList = pro.getCities();
            if (cityList == null || cityList.isEmpty()) {
                cityList = new ArrayList<>();
                cityList.add(new City("不限", 0, pro.getProvince(), pro.getProvinceID()));
            } else if (cityList.size() > 1 && "不限".equals(cityList.get(0).getCity())) {
                cityList.remove(0);
            }
            pro.setCities(cityList);
        }
        return temp;
    }

    /**
     * 获取带限制的省市列表，省市列表前都添加不限
     *
     * @return 省市列表
     */
    public ArrayList<Province> getLimitProvince() {
        temp.clear();
        temp.addAll(provinces);
        //填充省级列表
        temp.add(0, new Province("不限", 0, null));
        //填充市级列表
        for (Province pro : temp) {
            ArrayList<City> cityList = pro.getCities();
            if (cityList == null || cityList.isEmpty()) {
                cityList = new ArrayList<>();
                cityList.add(0, new City("不限", 0, pro.getProvince(), pro.getProvinceID()));
            } else if (!"不限".equals(cityList.get(0).getCity())) {
                cityList.add(0, new City("不限", 0, pro.getProvince(), pro.getProvinceID()));
            }
            pro.setCities(cityList);
        }
        return temp;
    }

    /**
     * 根据省份id获取当前省份
     *
     * @param provinceID 省份id
     * @return 省份名称
     */
    public String getProvinceByID(int provinceID) {
        provinceID = correctedID(provinceID);

        String province = "";
        for (Province pro : provinces) {
            if (provinceID == pro.getProvinceID()) {
                province = pro.getProvince();
                break;
            }
        }
        return province;
    }

    /**
     * 根据省份id获取当前省份展示名字
     *
     * @param provinceID 省份id
     * @return 省份名称
     */
    public String getProvinceNameByID(int provinceID) {
        provinceID = correctedID(provinceID);

        String province = "";
        for (Province pro : provinces) {
            if (provinceID == pro.getProvinceID()) {
                province = pro.getProvinceName();
                break;
            }
        }
        return province;
    }

    /**
     * 根据城市id获取当前省份
     *
     * @param cityID 城市id
     * @return 城市名称
     */
    public String getCityByID(int cityID) {
        cityID = correctedID(cityID);

        String city = "";
        for (Province pro : provinces) {
            ArrayList<City> cityList = pro.getCities();
            for (City cit : cityList) {
                if (cityID == cit.getCityID()) {
                    city = cit.getCity();
                    break;
                }
            }
        }
        return city;
    }

    /**
     * 根据城市id获取当前城市Name
     *
     * @param cityID 城市id
     * @return 城市名称
     */
    public String getCityNameByID(int cityID) {
        cityID = correctedID(cityID);

        String city = "";
        for (Province pro : provinces) {
            ArrayList<City> cityList = pro.getCities();
            for (City cit : cityList) {
                if (cityID == cit.getCityID()) {
                    city = cit.getCityName();
                    break;
                }
            }
        }
        return city;
    }

    /**
     * 根据省份id和城市id获取
     *
     * @param provinceID 省份id
     * @param cityID     城市id
     * @return 拼接完成的省市用于显示
     */
    public String getArea(int provinceID, int cityID) {
        provinceID = correctedID(provinceID);
        cityID = correctedID(cityID);

        String province = "", city = "";
        for (Province pro : provinces) {
            if (provinceID == pro.getProvinceID()) {
                province = pro.getProvinceName();
                ArrayList<City> cityList = pro.getCities();
                for (City cit : cityList) {
                    if (cityID == cit.getCityID()) {
                        city = cit.getCityName();
                        break;
                    }
                }
                break;
            }
        }
        return province + city;
    }

    /**
     * 根据省市id获取当前的city
     *
     * @param provinceID 省份id
     * @param cityID     城市id
     * @return City
     */
    public City getCity(int provinceID, int cityID) {
        provinceID = correctedID(provinceID);
        cityID = correctedID(cityID);

        City city = new City();
        for (Province pro : provinces) {
            if (provinceID == pro.getProvinceID()) {
                ArrayList<City> cityList = pro.getCities();
                for (City cit : cityList) {
                    if (cityID == cit.getCityID()) {
                        city = cit;
                        break;
                    }
                }
                break;
            }
        }
        return city;
    }

    /**
     * 根据省市id获取当前的city
     *
     * @param provinceName 省份名称
     * @param cityName     城市名称
     * @return City
     */
    public City getCity(String provinceName, String cityName) {
        City city = new City();
        for (Province pro : provinces) {
            if (pro.getProvinceName().equals(provinceName)) {
                ArrayList<City> cityList = pro.getCities();
                for (City cit : cityList) {
                    if (cityName.equals(cit.getCityName())) {
                        city = cit;
                        break;
                    }
                }
            }
        }
        return city;
    }

    /**
     * 根据省市全称获取当前的city
     *
     * @param province 省份名称
     * @param city     城市名称
     * @return City
     */
    public City getCityInfo(String province, String city) {
        City cityInfo = new City();
        for (Province pro : provinces) {
            if (pro.getProvince().equals(province)) {
                ArrayList<City> cityList = pro.getCities();
                for (City cit : cityList) {
                    if (city.equals(cit.getCity())) {
                        cityInfo = cit;
                        break;
                    }
                }
            }
        }
        return cityInfo;
    }

    @Override
    public void parseJson(String s) {
        if (TextUtils.isEmpty(s)) return;
        try {
            provinces = (ArrayList<Province>) getBaseDataList(getJsonArray(s), Province.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修正省市获取的时候城市id的位数差
     *
     * @param id 省市id
     * @return 修正后的省市id
     */
    private int correctedID(int id) {
        try {
            int targetLength = 6;//正确为6位数
            int length = String.valueOf(id).length();
            int differLength = targetLength - length;
            if (differLength > 0) {
                StringBuilder builder = new StringBuilder();
                builder.append(id);
                for (int i = 0; i < differLength; i++) {
                    builder.append(0);
                }
                return Integer.parseInt(builder.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
}
