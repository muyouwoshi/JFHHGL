package com.juxin.predestinate.module.local.location;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.module.logic.application.App;

/**
 * 定位功能管理类
 */
public class LocationMgr implements Handler.Callback {
    private volatile static LocationMgr mInstance;
    /**location ok*/
    private boolean isOk;

    private LocationMgr() {
        initHandler();
        initSDK();
    }

    public static LocationMgr getInstance() {
        if (mInstance == null) {
            synchronized (LocationMgr.class) {
                if (mInstance == null) {
                    mInstance = new LocationMgr();
                }
            }
        }
        return mInstance;
    }

    // ================================= 提供外部调用 =================================================

    /**
     * 启动定位
     */
    public synchronized void start() {
        if (locationClient != null && !locationClient.isStarted()) {
            PLogger.d("BDLocation ::::: Start");
            initSDK();
            locationClient.registerLocationListener(listener);
            locationClient.start();
            SELF_SCAN_TIMES = 5;
        }
    }

    /**
     * 停止定位
     */
    private synchronized void stop() {
        if (locationClient != null && locationClient.isStarted()) {
            PLogger.d("BDLocation ::::: Stop");
            locationClient.unRegisterLocationListener(listener);
            locationClient.stop();
        }
    }

    /**
     * 获取定位结果对象
     */
    public PointD getPointD() {
        if (pointD == null) {
            readCfg();
        }
        return useFixPlace ? fixPlace : pointD;
    }

    /**
     * 是否是固定位置数据
     */
    public boolean isUseFixPlace() {
        return useFixPlace;
    }

    /**
     * 是否固定位置信息
     */
    public void setUseFixPlace(boolean useFixPlace) {
        this.useFixPlace = useFixPlace;
        writeCfg();
    }

    /**
     * 指定一个固定位置。
     */
    public void setFixPlace(double longitude, double latitude) {
        fixPlace.longitude = longitude;
        fixPlace.latitude = latitude;
        useFixPlace = true;
        writeCfg();
    }

    // ================================= SDK初始化 ====================================

    private static LocationClient locationClient = null;
    private static PointD pointD = null;
    private static BDListener listener;

    private void initSDK() {
        if (locationClient == null) {
            locationClient = new LocationClient(App.context);
            locationClient.setLocOption(getLocOption());
        }

        if (listener == null) {
            listener = new BDListener();
        }
    }

    /**
     * 初始化定位配置
     */
    private LocationClientOption getLocOption() {
        LocationClientOption option = new LocationClientOption();
        // 设置定位模式：低功耗，只使用网络定位(WIFI与基站定位)
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        // 默认值gcj02，返回国测局经纬度坐标系
        option.setCoorType("bd09ll");
        // 设置发起定位请求的间隔时间
        option.setScanSpan(10 * 1000);
        //返回的定位结果包含地址信息
        option.setIsNeedAddress(true);

        return option;
    }

    /**
     * 实现 BDLocationListener 接口，返回定位结果数据
     */
    private class BDListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null) {
                return;
            }
            printLog(bdLocation);

            // 定位失败
            if (bdLocation.getLocType() != BDLocation.TypeNetWorkLocation) {
                SELF_SCAN_TIMES--;
                if (SELF_SCAN_TIMES <= 0) {
                    stop();
                    startTime();  // 启动定位重启
                }
                return;
            }
            isOk = true;
            PointD pointDTemp = new PointD();
            pointDTemp.longitude = bdLocation.getLongitude();
            pointDTemp.latitude = bdLocation.getLatitude();
            pointDTemp.countryCode = bdLocation.getCountryCode();
            pointDTemp.province = bdLocation.getProvince();
            pointDTemp.city = bdLocation.getCity();
            pointDTemp.cityCode = bdLocation.getCityCode();
            pointDTemp.district = bdLocation.getDistrict();
            pointDTemp.addr = bdLocation.getAddrStr();
            pointDTemp.streetNum = bdLocation.getStreetNumber();
            pointDTemp.buildID = bdLocation.getBuildingID();

            // 暂时未提供获取API, 也未提供邮政编码API
            //pointDTemp.proCode = bdLocation.get
            //pointDTemp.distCode = bdLocation.get

            setPointD(pointDTemp);
            stop();       // 定位成功后关闭定位扫描，否则每隔10s扫描一次
            startTime();  // 启动定位重启

            // 发送定位成功消息
            MsgMgr.getInstance().sendMsg(MsgType.MT_Located, pointDTemp);
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }
    }

    /**
     * 定位结果数据
     * <p>
     * 返回码
     *
     * @see 'http://lbsyun.baidu.com/index.php?title=android-locsdk/guide/ermsg'
     */
    private void printLog(BDLocation location) {
        StringBuffer sb = new StringBuffer(256);
        sb.append("time : ");
        sb.append(location.getTime());
        sb.append("\ncode : ");
        sb.append(location.getLocType());
        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());
        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());
        sb.append("\npro-city-district : ");
        sb.append(location.getProvince());
        sb.append("-" + location.getCity());
        sb.append("-" + location.getDistrict());
        sb.append("\ncountryCode : ");
        sb.append(location.getCountryCode());
        sb.append("\ncityCode : ");
        sb.append(location.getCityCode());
        sb.append("\nstreetNum : ");
        sb.append(location.getStreetNumber());
        sb.append("\nbuildingID : ");
        sb.append(location.getBuildingID());
        if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            sb.append("\ndescribe : ");
            sb.append("网络定位成功");
        } else if (location.getLocType() >= 501) {
            sb.append("\ndescribe : ");
            sb.append("AK不存在或者非法，请按照说明文档重新申请AK.");
        } else if (location.getLocType() == BDLocation.TypeServerError) {
            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可反馈IMEI号和大体定位时间到loc-bugs@baidu.com");
        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");
        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
        }
        PLogger.d("BDLocation: " + sb.toString());
    }


    // ================================= 定位重启 =================================================
    private static final int LOCATION_EVENT_START = 0;
    private static int SELF_SCAN_TIMES = 5;  // 自扫描次数，当某种原因导致定位失败后，自扫描5次后关闭定位服务
    private Handler handler = null;          // 定位重启控制器

    /**
     * 初始化定位重启控制器
     */
    private void initHandler() {
        Looper looper;
        if ((looper = Looper.myLooper()) != null) {
            handler = new Handler(looper, this);
        } else if ((looper = Looper.getMainLooper()) != null) {
            handler = new Handler(looper, this);
        } else {
            handler = null;
        }
    }

    private void startTime() {
        stopTime();
        if (handler != null) {
            handler.sendEmptyMessageDelayed(LOCATION_EVENT_START, 20 * 60 * 1000);
        }
    }

    private void stopTime() {
        if (handler != null) {
            handler.removeMessages(LOCATION_EVENT_START);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case LOCATION_EVENT_START:
                start();
                break;
        }
        return false;
    }

    // ================================= 定位数据持久化 =================================================
    private boolean useFixPlace = false;     // 是否固定GPS位置
    private PointD fixPlace = new PointD();  // 固定GPS位置对象

    /**
     * 将定位结果存储到本地SP
     */
    private synchronized void setPointD(PointD pointD) {
        this.pointD = pointD;
        writeCfg();
    }

    private void readCfg() {
        if (pointD != null) return;

        pointD = new PointD();
        try {
            pointD.longitude = Double.valueOf(PSP.getInstance().getString("LM_longitude", "0"));
            pointD.latitude = Double.valueOf(PSP.getInstance().getString("LM_latitude", "0"));
            pointD.countryCode = PSP.getInstance().getString("LM_countryCode", "");
            pointD.province = PSP.getInstance().getString("LM_province", "");
            pointD.proCode = PSP.getInstance().getString("LM_proCode", "");
            pointD.city = PSP.getInstance().getString("LM_city", "");
            pointD.cityCode = PSP.getInstance().getString("LM_cityCode", "");
            pointD.district = PSP.getInstance().getString("LM_district", "");
            pointD.distCode = PSP.getInstance().getString("LM_distCode", "");
            pointD.addr = PSP.getInstance().getString("LM_addr", "");
            pointD.streetNum = PSP.getInstance().getString("LM_streetNum", "");
            pointD.buildID = PSP.getInstance().getString("LM_buildID", "");
            useFixPlace = PSP.getInstance().getBoolean("LM_Fix_use", false);
            fixPlace.longitude = Double.valueOf(PSP.getInstance().getString("LM_Fix_latitude", "0"));
            fixPlace.latitude = Double.valueOf(PSP.getInstance().getString("LM_Fix_longitude", "0"));
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }
    }

    private void writeCfg() {
        if (pointD == null) return;
        try {
            PSP.getInstance().put("LM_longitude", String.valueOf(pointD.longitude));
            PSP.getInstance().put("LM_latitude", String.valueOf(pointD.latitude));
            PSP.getInstance().put("LM_countryCode", pointD.countryCode);
            PSP.getInstance().put("LM_province", pointD.province);
            PSP.getInstance().put("LM_proCode", pointD.proCode);
            PSP.getInstance().put("LM_city", pointD.city);
            PSP.getInstance().put("LM_cityCode", pointD.cityCode);
            PSP.getInstance().put("LM_district", pointD.district);
            PSP.getInstance().put("LM_distCode", pointD.distCode);
            PSP.getInstance().put("LM_addr", pointD.addr);
            PSP.getInstance().put("LM_streetNum", pointD.streetNum);
            PSP.getInstance().put("LM_buildID", pointD.buildID);
            PSP.getInstance().put("LM_Fix_use", useFixPlace);
            PSP.getInstance().put("LM_Fix_latitude", String.valueOf(fixPlace.latitude));
            PSP.getInstance().put("LM_Fix_longitude", String.valueOf(fixPlace.longitude));
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }
    }

    public boolean isOk() {
        return isOk;
    }

    public class PointD {
        public double longitude = 0;   // 经度
        public double latitude = 0;    // 纬度
        public String countryCode = "";// 国家代码
        public String province = "";   // 省份信息
        public String proCode = "";    // 省份代码
        public String city = "";       // 城市信息
        public String cityCode = "";   // 城市代码
        public String district = "";   // 区县信息
        public String distCode = "";    // 区县代码
        public String addr = "";       // 详细地址
        public String streetNum = "";  // 街道号
        public String buildID = "";    // 建筑号
    }
}
