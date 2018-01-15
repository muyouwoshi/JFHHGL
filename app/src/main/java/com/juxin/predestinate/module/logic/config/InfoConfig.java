package com.juxin.predestinate.module.logic.config;

import com.juxin.library.utils.FileUtil;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 个人资料通用配置
 */
public class InfoConfig extends BaseData {

    private static InfoConfig infoConfig;

    public synchronized static InfoConfig getInstance() {
        if (infoConfig == null) {
            infoConfig = new InfoConfig();
            infoConfig.parseJson(FileUtil.getFromAssets(App.getActivity(), "info_config.json"));
        }
        return infoConfig;
    }

    private SimpleConfig height,//身高
            income, //月收入
            marry,//情感状态
            edu, //学历
            job,//工作
            weight,//体重
            star,  //星座
            constellation,// 生日与星座关联配表
            age;//年龄

    @Override
    public void parseJson(String s) {
        JSONObject jsonObject = getJsonObject(s);

        height = new SimpleConfig();
        height.setShow(new ArrayList<String>());
        height.setSubmit(new ArrayList<String>());
        height.setSubmitMap(new HashMap<String, String>());
        height.setShowMap(new HashMap<String, String>());
        for (int i = 149; i <= 200; i++) {
            height.getShow().add(i == 149 ? "不限" : i + "cm");
            height.getSubmit().add(i == 149 ? 0 + "" : i + "");
            height.getSubmitMap().put(i == 149 ? "不限" : i + "cm", i == 149 ? 0 + "" : i + "");
            height.getShowMap().put(i == 149 ? 0 + "" : i + "", i == 149 ? "不限" : i + "cm");
        }
        age = new SimpleConfig();
        age.setShow(new ArrayList<String>());
        age.setSubmit(new ArrayList<String>());
        age.setSubmitMap(new HashMap<String, String>());
        age.setShowMap(new HashMap<String, String>());
        for (int i = 17; i <= 60; i++) {
            age.getShow().add(i == 17 ? "不限" : i + "岁");
            age.getSubmit().add(i == 17 ? 0 + "" : i + "");
            age.getSubmitMap().put(i == 17 ? "不限" : i + "岁", i == 17 ? 0 + "" : i + "");
            age.getShowMap().put(i == 17 ? 0 + "" : i + "", i == 17 ? "不限" : i + "岁");
        }

        income = new SimpleConfig();
        if (jsonObject.has("income")) income.parseJson(jsonObject.optString("income"));

        marry = new SimpleConfig();
        if (jsonObject.has("marry")) marry.parseJson(jsonObject.optString("marry"));

        edu = new SimpleConfig();
        if (jsonObject.has("edu")) edu.parseJson(jsonObject.optString("edu"));

        job = new SimpleConfig();
        if (jsonObject.has("job")) job.parseJson(jsonObject.optString("job"));

        weight = new SimpleConfig();
        if (jsonObject.has("weight")) weight.parseJson(jsonObject.optString("weight"));

        star = new SimpleConfig();
        if (jsonObject.has("star")) star.parseJson(jsonObject.optString("star"));

        constellation = new SimpleConfig();
        if (jsonObject.has("constellation"))
            constellation.parseJson(jsonObject.optString("constellation"));
    }

    /**
     * @return 获取带不限的征友身高值
     */
    public SimpleConfig getHeight() {
        return height;
    }

    /**
     * @return 获取不带不限的正常身高值
     */
    public SimpleConfig getHeightN() {
        return removeLimitIndex(height);
    }

    /**
     * @return 获取带不限的征友月收入值
     */
    public SimpleConfig getIncome() {
        return income;
    }

    /**
     * @return 获取不带不限的正常月收入值
     */
    public SimpleConfig getIncomeN() {
        return removeLimitIndex(income);
    }

    /**
     * @return 获取带不限的征友学历值
     */
    public SimpleConfig getEdu() {
        return edu;
    }

    /**
     * @return 获取不带不限的正常学历值
     */
    public SimpleConfig getEduN() {
        return removeLimitIndex(edu);
    }

    /**
     * @return 获取带不限的年龄值
     */
    public SimpleConfig getAge() {
        return age;
    }

    /**
     * @return 获取不带不限的正常年龄值
     */
    public SimpleConfig getAgeN() {
        return removeLimitIndex(age);
    }

    //==================以下为非单独处理内容======================

    public SimpleConfig getMarry() {
        return marry;
    }

    public SimpleConfig getJob() {
        return job;
    }

    public SimpleConfig getWeight() {
        return weight;
    }

    public SimpleConfig getStar() {
        return star;
    }

    public SimpleConfig getConstellation() {
        return constellation;
    }

    /**
     * 移除数据源中的不限值
     *
     * @param simpleConfig 数据源
     * @return 移除不限之后的值
     */
    private SimpleConfig removeLimitIndex(SimpleConfig simpleConfig) {
        if (simpleConfig == null) return new SimpleConfig();

        //SubmitMap与其他集合同期添加，只判断SubmitMap即可
        if (simpleConfig.getSubmitMap() != null && simpleConfig.getSubmitMap().containsKey("不限")) {
            simpleConfig.getShow().remove(0);
            simpleConfig.getSubmit().remove(0);
            simpleConfig.getSubmitMap().remove("不限");
            simpleConfig.getShowMap().remove("0");
        }
        return simpleConfig;
    }

    /**
     * 单独配置解析
     */
    public class SimpleConfig extends BaseData {

        private List<String> show;
        private List<String> submit;
        private Map<String, String> submitMap;//提交数据的时候使用的map
        private Map<String, String> showMap;//服务器返回数据的时候展示使用

        public SimpleConfig() {
        }

        @Override
        public void parseJson(String s) {
            JSONObject jsonObject = getJsonObject(s);
            JSONArray showArray = jsonObject.optJSONArray("show");
            JSONArray submitArray = jsonObject.optJSONArray("submit");

            show = new ArrayList<>();
            submit = new ArrayList<>();
            submitMap = new HashMap<>();
            showMap = new HashMap<>();

            for (int i = 0; showArray != null && submitArray != null && i < showArray.length(); i++) {
                show.add(showArray.optString(i));
                submit.add(submitArray.optString(i));
                submitMap.put(showArray.optString(i), submitArray.optString(i));
                showMap.put(submitArray.optString(i), showArray.optString(i));
            }
        }

        public void setShow(List<String> show) {
            this.show = show;
        }

        public void setSubmit(List<String> submit) {
            this.submit = submit;
        }

        public void setSubmitMap(Map<String, String> submitMap) {
            this.submitMap = submitMap;
        }

        public void setShowMap(Map<String, String> showMap) {
            this.showMap = showMap;
        }

        public List<String> getShow() {
            return show;
        }

        public List<String> getSubmit() {
            return submit;
        }

        /**
         * 提交数据的时候使用，封装在提交数据的dialog中。外部用户无需关心
         *
         * @return 提交的key-value
         */
        public Map<String, String> getSubmitMap() {
            return submitMap;
        }

        /**
         * 显示数据的时候使用
         *
         * @return 以服务器返回的数据为key的map，可按照key获得显示的value
         */
        public Map<String, String> getShowMap() {
            return showMap;
        }

        /**
         * 根据数组中的index获取list的展示值
         *
         * @param index list中的index
         * @return 展示的value值
         */
        public String getStringWithIndex(int index) {
            return show == null || index < 0 || show.size() < index - 1 ? "" : show.get(index);
        }

        /**
         * 根据服务器返回的配置值获取展示的值
         *
         * @param value 服务器返回的配置值
         * @return 该配置项显示的内容
         */
        public String getShowWithSubmit(int value) {
            return showMap == null || !showMap.containsKey(String.valueOf(value)) ? "" : showMap.get(String.valueOf(value));
        }

        /**
         * 根据显示的值获取提交的值
         *
         * @param show 显示的值
         * @return 提交的integer值
         */
        public int getSubmitWithShow(String show) {
            return submitMap == null || !submitMap.containsKey(show) ? -1 : Integer.parseInt(submitMap.get(show));
        }
    }
}
