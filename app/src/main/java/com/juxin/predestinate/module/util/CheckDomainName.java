package com.juxin.predestinate.module.util;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.utils.EncryptUtil;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.predestinate.bean.config.CheckDomainList;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.FinalKey;
import com.juxin.predestinate.module.logic.config.Hosts;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.logic.socket.IMProxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 域名灾备处理
 * Created by duanzheng on 2017/6/27.
 */

public class CheckDomainName {

    //private String [] checkUrlArray=new String[]{"","","",""};
    private static final String DOMAINNAME_KEY = "domainName_" + Hosts.SERVER_TYPE;
    public volatile static int domainNameArrIndex = 0;//当前访问 的域名列表的索引  域名灾备
    //public static final String[] DOMAINNAME_DEFAULT=new String[]{TCP_HOST[SERVER_TYPE],PHP_HOST[SERVER_TYPE],GO_HOST[SERVER_TYPE],UPLOAD_HOST[SERVER_TYPE],PAY_HOST[SERVER_TYPE],TCP_PORT[SERVER_TYPE]};//默认地址
    public static final Map<String, String> mappingTable = Collections.synchronizedMap(new HashMap<String, String>());

    private static List<CheckDomainList.DomainsBean> getLocalDomain() {
        List<CheckDomainList.DomainsBean> domainNameNArray = new ArrayList<CheckDomainList.DomainsBean>();
        String doaminNameJsonStr = PSP.getInstance().getString(DOMAINNAME_KEY, "");
        if (TextUtils.isEmpty(doaminNameJsonStr)) {
            return domainNameNArray;
        }

        try {
            doaminNameJsonStr = EncryptUtil.decryptDES(doaminNameJsonStr, FinalKey.UP_DES_KEY);
//            PLogger.i("domainNameJson decryptDES:"+doaminNameJsonStr);
            if (TextUtils.isEmpty(doaminNameJsonStr)) {
                return domainNameNArray;
            } else {
                List<CheckDomainList.DomainsBean> localDoaminNameArr = JSON.parseObject(doaminNameJsonStr, new TypeReference<List<CheckDomainList.DomainsBean>>() {
                });
                if (localDoaminNameArr != null && localDoaminNameArr.size() > 0) {
                    return localDoaminNameArr;
                } else {
                    return domainNameNArray;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return domainNameNArray;
        }
    }

    //app第一次启动调用
    private static void initDomainName() {
        String tcp = addSeparator(Hosts.FATE_IT_TCP);
        mappingTable.put(tcp, tcp);

        String top_port = String.valueOf(Hosts.FATE_IT_TCP_PORT);
        mappingTable.put(top_port, top_port);

        String php = addSeparator(Hosts.FATE_IT_HTTP);
        mappingTable.put(php, php);

        String go = addSeparator(Hosts.FATE_IT_GO);
        mappingTable.put(go, go);

        String pic = addSeparator(Hosts.FATE_IT_HTTP_PIC);
        mappingTable.put(pic, pic);

        String pay = addSeparator(Hosts.FATE_IT_PROTOCOL);
        mappingTable.put(pay, pay);

        String config = addSeparator(Hosts.FATE_CONFIG);
        mappingTable.put(config, config);

        String live = addSeparator(Hosts.FATE_IT_LIVE);
        mappingTable.put(live, live);

        String material = addSeparator(Hosts.FATE_IT_MATERIAL);
        mappingTable.put(material, material);
    }

    public static void checkLocalDomainName() {
        loadLocalDomainName();
        initDomainName();
        // getDomainNameList();
    }

    public static String getNewDomainName(String url) {
        if (mappingTable != null && mappingTable.size() > 0) {
            String s = addSeparator(url);
            if (mappingTable.containsKey(s)) {
                return mappingTable.get(s);
            }
        }
        return "";
    }

    private static void loadLocalDomainName() {
        try {
            List<CheckDomainList.DomainsBean> domainNameArr = getLocalDomain();
            if (domainNameArr != null && domainNameArr.size() > 0) {
                setDomainName(domainNameArr.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getDomainNameList() {
        //请求网络
        checkDomainArr(domainNameArrIndex);
    }

    public static int index = -1;
    public static boolean isAutoSwitch = false;

    private static void reSet() {
        index = -1;
        isAutoSwitch = false;
    }

    public static void checkDomainArr(final int position) {
        //PLogger.i("checkDomainArr:"+position);
        if (App.getActivity() == null || (App.getActivity() != null && !NetworkUtils.isConnected(App.getActivity()))) {
            PLogger.i("no intent");
            return;
        }
        ModuleMgr.getCommonMgr().checkEffectiveDomainArr(position, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (!isAutoSwitch) {
                    isAutoSwitch = true;
                    index = position;//
                }

                if (position < (Hosts.DOMAINTEST_ONLINE_ARR.length - 1)) {
                    domainNameArrIndex = position + 1;
                } else {
                    domainNameArrIndex = 0;
                }
                if (response != null && response.isOk()) {

                    if (response.getBaseData() != null && response.getBaseData() instanceof CheckDomainList) {
                        List<CheckDomainList.DomainsBean> list = ((CheckDomainList) response.getBaseData()).getDomains();
                        if (list != null && list.size() > 0) {
                            try {
                                String domainNameJson = JSON.toJSONString(list);

                                String encrypStr = EncryptUtil.encryptDES(domainNameJson, FinalKey.UP_DES_KEY);
                                //PLogger.i("domainNameJson encrypStr:"+encrypStr);
                                if (!TextUtils.isEmpty(encrypStr) && list != null && list.size() > 0) {
                                    PSP.getInstance().put(DOMAINNAME_KEY, encrypStr);
                                    setDomainName(list.get(0));
                                    reSet();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                isLoad(domainNameArrIndex);
                            }
                        } else {
                            isLoad(domainNameArrIndex);
                        }
                    } else {
                        isLoad(domainNameArrIndex);
                    }

                } else {
                    isLoad(domainNameArrIndex);
                }
            }


        });
    }

    private static void isLoad(int position) {
        if (position == index && isAutoSwitch) {
            reSet();
        } else {
            checkDomainArr(domainNameArrIndex);
        }
    }

    public static void setDomainName(CheckDomainList.DomainsBean doaminNameArr) {
        if (doaminNameArr == null) {
            PLogger.d("设置域名失败,请检查数据");
            return;
        }
        if (!TextUtils.isEmpty(doaminNameArr.getTcp_addr())) {
            Hosts.FATE_IT_TCP = doaminNameArr.getTcp_addr();

            String key_tcp = addSeparator(mappingTable.get(Hosts.TCP_HOST[Hosts.SERVER_TYPE]));
            if (!TextUtils.isEmpty(key_tcp)) {
                if (mappingTable.containsKey(key_tcp)) {
                    mappingTable.remove(key_tcp);
                }
                mappingTable.put(key_tcp, Hosts.FATE_IT_TCP);
            }
        }
        if (!TextUtils.isEmpty(doaminNameArr.getTcp_port()) && BaseUtil.isNumeric(doaminNameArr.getTcp_port())) {
            Hosts.FATE_IT_TCP_PORT = Integer.valueOf(doaminNameArr.getTcp_port());

            String key_tcp_port = mappingTable.get(Hosts.TCP_PORT[Hosts.SERVER_TYPE]);
            if (!TextUtils.isEmpty(key_tcp_port)) {
                if (mappingTable.containsKey(key_tcp_port)) {
                    mappingTable.remove(key_tcp_port);
                }
                mappingTable.put(key_tcp_port, String.valueOf(Hosts.FATE_IT_TCP_PORT));
            }

        }
        if (!TextUtils.isEmpty(doaminNameArr.getService_addr())) {
            Hosts.FATE_IT_HTTP = addSeparator(doaminNameArr.getService_addr());
            Hosts.HOST_URL = Hosts.FATE_IT_HTTP;


            String key_php = addSeparator(Hosts.PHP_HOST[Hosts.SERVER_TYPE]);//mappingTable.get(Hosts.PHP_HOST[Hosts.SERVER_TYPE]);
            if (!TextUtils.isEmpty(key_php)) {
                if (mappingTable.containsKey(key_php)) {
                    mappingTable.remove(key_php);
                }
                mappingTable.put(key_php, Hosts.FATE_IT_HTTP);
            }

        }
        if (!TextUtils.isEmpty(doaminNameArr.getNew_service_addr())) {
            Hosts.FATE_IT_GO = addSeparator(doaminNameArr.getNew_service_addr());

            String key_go = addSeparator(Hosts.GO_HOST[Hosts.SERVER_TYPE]);//mappingTable.get(Hosts.GO_HOST[Hosts.SERVER_TYPE]);
            if (!TextUtils.isEmpty(key_go)) {
                if (mappingTable.containsKey(key_go)) {
                    mappingTable.remove(key_go);
                }
                mappingTable.put(key_go, Hosts.FATE_IT_GO);
            }

        }

        if (!TextUtils.isEmpty(doaminNameArr.getConfigure_addr())) {
            Hosts.FATE_CONFIG = addSeparator(doaminNameArr.getConfigure_addr());

            String key_config = addSeparator(Hosts.CONFIG_HOST[Hosts.SERVER_TYPE]);//mappingTable.get(Hosts.CONFIG_HOST[Hosts.SERVER_TYPE]);

            if (!TextUtils.isEmpty(key_config)) {
                if (mappingTable.containsKey(key_config)) {
                    mappingTable.remove(key_config);
                }
                mappingTable.put(key_config, Hosts.FATE_CONFIG);//
            }

        }

        if (!TextUtils.isEmpty(doaminNameArr.getUpload_addr())) {
            Hosts.FATE_IT_HTTP_PIC = addSeparator(doaminNameArr.getUpload_addr());


            String key_pic = addSeparator(Hosts.UPLOAD_HOST[Hosts.SERVER_TYPE]);// mappingTable.get(Hosts.UPLOAD_HOST[Hosts.SERVER_TYPE]);
            if (!TextUtils.isEmpty(key_pic)) {
                if (mappingTable.containsKey(key_pic)) {
                    mappingTable.remove(key_pic);
                }

                mappingTable.put(key_pic, Hosts.FATE_IT_HTTP_PIC);
            }

        }

        if (!TextUtils.isEmpty(doaminNameArr.getPay_addr())) {
            Hosts.FATE_IT_PROTOCOL = addSeparator(doaminNameArr.getPay_addr());

            String key_pay = addSeparator(Hosts.PAY_HOST[Hosts.SERVER_TYPE]);//mappingTable.get(Hosts.PAY_HOST[Hosts.SERVER_TYPE]);
            if (!TextUtils.isEmpty(key_pay)) {
                if (mappingTable.containsKey(key_pay)) {
                    mappingTable.remove(key_pay);
                }
                mappingTable.put(key_pay, Hosts.FATE_IT_PROTOCOL);
            }

        }
        //gwz 直播服务器灾备
        if(!TextUtils.isEmpty(doaminNameArr.getLive_service_addr())){
            Hosts.FATE_IT_LIVE = addSeparator(doaminNameArr.getLive_service_addr());

            String key_live = addSeparator(Hosts.LIVE_HOST[Hosts.SERVER_TYPE]);//mappingTable.get(Hosts.LIVE_HOST[Hosts.SERVER_TYPE]);
            if (!TextUtils.isEmpty(key_live)) {
                if (mappingTable.containsKey(key_live)) {
                    mappingTable.remove(key_live);
                }
                mappingTable.put(key_live, Hosts.FATE_IT_LIVE);
            }
        }
        //分享素材
        if(!TextUtils.isEmpty(doaminNameArr.getShare_addr())){
            Hosts.FATE_IT_MATERIAL = addSeparator(doaminNameArr.getShare_addr());

            String key_material = addSeparator(Hosts.MATERIAL_HOST[Hosts.SERVER_TYPE]);
            if (!TextUtils.isEmpty(key_material)) {
                if (mappingTable.containsKey(key_material)) {
                    mappingTable.remove(key_material);
                }
                mappingTable.put(key_material, Hosts.FATE_IT_MATERIAL);
            }
        }
        PLogger.i("reSetHostPort:" + mappingTable.size());
        IMProxy.getInstance().reSetHostPort(Hosts.FATE_IT_TCP, Hosts.FATE_IT_TCP_PORT);
    }

    public static String addSeparator(String url) {
        url = addHttp(url);
        if (!TextUtils.isEmpty(url) && url.length() > 0 && !url.endsWith("/")) {
            return url + "/";
        }
        return url;
    }

    public static String addHttp(String url) {
        if (!TextUtils.isEmpty(url) && url.length() > 0 && !url.startsWith("http://")) {
            return "http://" + url;
        }
        return url;
    }
}
