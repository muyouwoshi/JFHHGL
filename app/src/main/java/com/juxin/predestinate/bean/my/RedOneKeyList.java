package com.juxin.predestinate.bean.my;


import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

import java.util.List;

/**
 * 标签列表
 * Created by zm on 17/3/20.
 */
public class RedOneKeyList extends BaseData {

    private List redbagFailLists;//失败红包列表
    private int sucnum;//成功入袋数
    private double sum;//用户红包总额（分）

    public List getRedbagFailLists() {
        return redbagFailLists;
    }

    public int getSucnum() {
        return sucnum;
    }

    public void setSucnum(int sucnum) {
        this.sucnum = sucnum;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum/100f;
    }

    @Override
    public void parseJson(String s) {
        RedbagList.oldDate = "";
        RedbagList.info = null;
        JSONObject jsonObject = getJsonObject(s).optJSONObject("res");
        if (jsonObject != null){
            if (jsonObject.has("sucnum")){
                this.setSucnum(jsonObject.optInt("sucnum"));
            }
            if (jsonObject.has("sum")){
                this.setSum(jsonObject.optDouble("sum"));
            }
            redbagFailLists = getBaseDataList(jsonObject.optJSONArray("fail"), RedbagList.RedbagInfo.class);
        }
    }
}
