package com.juxin.predestinate.bean.config;

import android.text.TextUtils;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务器域名灾备列表
 * Created by duanzheng on 2017/6/29.
 */

public class CheckDomainList extends BaseData {

    private List<DomainsBean> domains = new ArrayList<DomainsBean>();

    public List<DomainsBean> getDomains() {
        return domains;
    }

    public void setDomains(List<DomainsBean> domains) {
        this.domains = domains;
    }

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObject(jsonStr);
        if (jsonObject != null) {
            String jsonData = jsonObject.optString("res");
            if (!TextUtils.isEmpty(jsonData)) {
                JSONObject resJsonObject = getJsonObject(jsonData);
                if (!resJsonObject.isNull("domains")) {
                    domains = (List<DomainsBean>) getBaseDataList(resJsonObject.optJSONArray("domains"), DomainsBean.class);
                }
            }
        }
    }

    /**
     * 单个域名灾备数据信息
     */
    public static class DomainsBean extends BaseData {

        private String service_addr;
        private String new_service_addr;
        private String pay_addr;
        private String upload_addr;
        private String tcp_addr;
        private String tcp_port;
        private String configure_addr;
        private String live_service_addr;
        private String share_addr;

        public String getConfigure_addr() {
            return configure_addr;
        }

        public void setConfigure_addr(String configure_addr) {
            this.configure_addr = configure_addr;
        }

        @Override
        public void parseJson(String jsonStr) {
            JSONObject jsonObject = getJsonObject(jsonStr);
            //json串解析
            this.setService_addr(jsonObject.optString("service_addr"));
            this.setNew_service_addr(jsonObject.optString("new_service_addr"));
            this.setPay_addr(jsonObject.optString("pay_addr"));
            this.setUpload_addr(jsonObject.optString("upload_addr"));
            this.setTcp_addr(jsonObject.optString("tcp_addr"));
            this.setTcp_port(jsonObject.optString("tcp_port"));
            setConfigure_addr(jsonObject.optString("configure_addr"));
            live_service_addr = jsonObject.optString("live_service_addr");
            share_addr = jsonObject.optString("share_addr");
        }

        public String getService_addr() {
            return service_addr;
        }

        public void setService_addr(String service_addr) {
            this.service_addr = service_addr;
        }

        public String getNew_service_addr() {
            return new_service_addr;
        }

        public void setNew_service_addr(String new_service_addr) {
            this.new_service_addr = new_service_addr;
        }

        public String getPay_addr() {
            return pay_addr;
        }

        public void setPay_addr(String pay_addr) {
            this.pay_addr = pay_addr;
        }

        public String getUpload_addr() {
            return upload_addr;
        }

        public void setUpload_addr(String upload_addr) {
            this.upload_addr = upload_addr;
        }

        public String getTcp_addr() {
            return tcp_addr;
        }

        public void setTcp_addr(String tcp_addr) {
            this.tcp_addr = tcp_addr;
        }

        public String getTcp_port() {
            return tcp_port;
        }

        public void setTcp_port(String tcp_port) {
            this.tcp_port = tcp_port;
        }

        public String getLive_service_addr() {
            return live_service_addr;
        }

        public String getShare_addr() {
            return share_addr;
        }

        @Override
        public String toString() {
            return "DomainsBean{" +
                    "service_addr='" + service_addr + '\'' +
                    ", new_service_addr='" + new_service_addr + '\'' +
                    ", pay_addr='" + pay_addr + '\'' +
                    ", upload_addr='" + upload_addr + '\'' +
                    ", tcp_addr='" + tcp_addr + '\'' +
                    ", tcp_port='" + tcp_port + '\'' +
                    ", configure_addr='" + configure_addr + '\'' +
                    ", live_service_addr='" + live_service_addr + '\'' +
                    ", share_addr='" + share_addr + '\'' +
                    '}';
        }
    }
}
