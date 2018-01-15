package com.juxin.predestinate.module.logic.tips;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.juxin.library.observe.ModuleBase;
import com.juxin.library.observe.Msg;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.predestinate.ui.tips.TipsBarBasePanel;

import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.juxin.library.observe.MsgType.MT_APP_Suspension_Notice;
import static com.juxin.library.observe.MsgType.MT_Inner_Suspension_Notice;

/**
 * 处理黄色小标题
 * (仅有网络提示的 二级界面或更低级界面 可以使用BaseActivity 下的getNetErrorPanel)
 * Created by Kind on 2016/11/16.
 */

public class TipsBarMgr implements ModuleBase, PObserver {
    //提示条事件标记key
    public static final String TipsMgrTag = "TipsMgrTag";
    //提示条事件标记 -- 显示提示条
    public static final String TipsMgr_Order = "TipsMgr_Order";
    //提示条事件标记 -- 提示条回返内容
    public static final String TipsMgr_Result = "TipsMgr_Result";

    //Msg事件中 提示条类型key
    public static final String TipsMgrType = "TipsMgrType";

    //Msg事件中 提示条显示状态命令key
    public static final String TipsMgrIsShow = "TipsMgrIsShow";
    //Msg事件中 提示条显示状态命令---显示
    public static final String TipsMgrIsShow_true = "TipsMgrIsShow_true";
    //Msg事件中 提示条显示状态命令---不显示
    public static final String TipsMgrIsShow_false = "TipsMgrIsShow_false";
    //Msg事件中 提示条显示状态命令---没有提示条要显示了
    public static final String TipsMgrIsShow_none = "TipsMgrIsShow_none";

    //Msg 传递在提示条中用到的Json对象的 key
    public static final String TipsMgrJson = "TipsMgrJson";

    //当前页面所用到的提示条数据
    TipsBarData tipsBarData = new TipsBarData();

    //提示条载体
    private ViewGroup viewGroup;

    //提示条交互数据
    private Map<String, Object> parms;

    @Override
    public void init() {
        MsgMgr.getInstance().attach(this);
    }

    @Override
    public void release() {
    }

    public TipsBarData getTipsBarData() {
        return tipsBarData;
    }

    public void setTipsBarData(TipsBarData tipsBarData) {
        this.tipsBarData = tipsBarData;
    }

    /**
     * 绑定页面控件和该控件关心的类型，在页面onResume的时候调用
     *
     * @param tipsBarMsg 要显示的页面
     * @param jsonObject 要传的参数
     * @param viewGroup  自定义view
     */
    public void attach(@NonNull TipsBarMsg tipsBarMsg, @NonNull ViewGroup viewGroup, JSONObject jsonObject) {
        execution(tipsBarMsg, viewGroup, jsonObject);
    }

    /**
     * 解绑控件，在页面onPause的时候调用
     */
    public void detach() {
        getTipsBarData().clear();
        if (viewGroup != null) {
            viewGroup.removeAllViews();
        }
    }

    /**
     * 初始化一些数据
     *
     * @param tipsBarMsg
     * @param viewGroup
     * @param jsonObject
     */
    private void execution(TipsBarMsg tipsBarMsg, ViewGroup viewGroup, JSONObject jsonObject) {
        TipsBarType[] tipsBarTypes = tipsBarMsg.getTipsBarType();

        Map<TipsBarType, Integer> typeIntegerMap = new HashMap<>();
        Map<TipsBarType, Boolean> typeNeedShow = new HashMap<>();
        for (TipsBarType barType : tipsBarTypes) {
            typeIntegerMap.put(barType, TipsBarData.Show_No);
            typeNeedShow.put(barType, true);
        }


        if (typeIntegerMap.size() > 0) {
            getTipsBarData().setObject(jsonObject);
            getTipsBarData().setIntegerMap(typeIntegerMap);
            getTipsBarData().setTypeNeedShow(typeNeedShow);
        }
        if (this.viewGroup != null) {
            this.viewGroup.removeAllViews();
        }
        this.viewGroup = viewGroup;

        onProcessCenter(" init");
    }


    /**
     * 处理中心 执行提示条的状态触发等指令
     */
    private void onProcessCenter(String fromTag) {
        Map<TipsBarType, Integer> typeIntegerMap = getTipsBarData().getIntegerMap();
        Iterator iterator = typeIntegerMap.entrySet().iterator();
        Log.d("_test", "onProcessCenter  fromTag == " + fromTag);
        TipsBarType key = getShowTip(iterator);
        if (key != TipsBarType.none) {
            executSpecial(key, "onProcessCenter");
        } else {
            Map<String, Object> parms = new HashMap<>();
            parms.put(TipsBarMgr.TipsMgrIsShow, TipsBarMgr.TipsMgrIsShow_none);
            Msg msg = new Msg();
            msg.setData(parms);
            MsgMgr.getInstance().sendMsg(MT_APP_Suspension_Notice, msg);
        }

    }

    /**
     * 执行提示条状态修改
     *
     * @param tipsBarType
     * @param fromTag
     */
    private void executSpecial(TipsBarType tipsBarType, String fromTag) {
        Map<TipsBarType, Integer> panelConfig = getTipsBarData().getIntegerMap();
        //如果正在显示没网状态 其他提示条 一律不执行
        if (panelConfig.containsKey(TipsBarType.Show_Network_Status_Change)
                && panelConfig.get(TipsBarType.Show_Network_Status_Change)
                == getTipsBarData().Show_Already) {
            return;
        }
        //判断当前提示是否需要显示
        boolean isNeedShow = isNeedSHow(tipsBarType);
        Log.d("_test", "isNeedShow = " + isNeedShow + " fromTag = " + fromTag + " tipsBarType == " + tipsBarType);
        if (!isNeedShow) {
            return;
        }
        //发出更改提示条状态的指令
        if (panelConfig.containsKey(tipsBarType)) {
            if (panelConfig.get(tipsBarType) == TipsBarData.Show_No) {
                if (viewGroup != null) {
                    View view = getTipsView(tipsBarType);
                    if (view != null) {
                        viewGroup.removeAllViews();
                        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        viewGroup.addView(view, params);
                        Log.d("_test", "viewGroup size == " + viewGroup.getChildCount());
                        Msg msg = new Msg();
                        Map<String, Object> parms = new HashMap<>();
                        parms.put(TipsMgrTag, TipsMgr_Order);
                        parms.put(TipsMgrType, tipsBarType);
                        parms.put(TipsMgrJson, getTipsBarData().getObject());
                        msg.setData(parms);
                        MsgMgr.getInstance().sendMsg(MT_Inner_Suspension_Notice, msg);
                    }
                }
            }
        }
    }


    @Override
    public void onMessage(String key, Object value) {
        if (TextUtils.isEmpty(key)) return;
        switch (key) {
            case MsgType.MT_APP_Suspension_Notice:
                Map<String, Object> parms = (Map<String, Object>) value;
                if (parms.containsKey(TipsMgrTag)) {
                    if (parms.get(TipsMgrTag).equals(TipsMgr_Result)) {
                        if (parms.containsKey(TipsMgrType)) {
                            TipsBarType type = (TipsBarType) parms.get(TipsMgrType);
                            if (parms.containsKey(TipsMgrIsShow)) {
                                String isShow = (String) parms.get(TipsMgrIsShow);

                                Log.d("_test", "type name = " + type.name() + " isSHow == " + isShow);
                                //如果可以显示 设置显示状态
                                if (isShow.equals(TipsMgrIsShow_true)) {
                                    setIsSHowType(type);
                                } else if (isShow.equals(TipsMgrIsShow_false)) {
                                    //如果不能显示则执行下一条提示条
                                    setNeedShow(type, false);
                                    onProcessCenter("MT_APP_Suspension_Notice");
                                }
                            }
                        }
                    }
                    //直接执行某一个提示条
                    if (parms.get(TipsMgrTag).equals(TipsMgr_Order)) {
                        if (parms.containsKey(TipsMgrType)) {
                            TipsBarType type = (TipsBarType) parms.get(TipsMgrType);
                            if (getTipsBarData().getIntegerMap().containsKey(type)) {
                                executSpecial(type, " MT_APP_Suspension_Notice");
                            }
                        }
                    }
                }
                break;
            case MsgType.MT_Network_Status_Change:
                //网络变换提示条
                if (getTipsBarData().getIntegerMap().containsKey(TipsBarType.Show_Network_Status_Change)) {
                    if (!(boolean) value) {
                        executSpecial(TipsBarType.Show_Network_Status_Change, "MT_Network_Status_Change");
                    } else {
                        if (viewGroup.getChildCount() != 0) {
                            viewGroup.removeAllViews();
                        }
                        getTipsBarData().getIntegerMap().put(TipsBarType.Show_Network_Status_Change, getTipsBarData().Show_No);
                        onProcessCenter(" MT_Network_Status_Change");
                    }
                }
                break;
            case MsgType.MT_MyInfo_Change:
                if (getTipsBarData().getIntegerMap().containsKey(TipsBarType.Show_Update_Portrait)) {
                    executSpecial(TipsBarType.Show_Update_Portrait, " MT_MyInfo_Change");
                }
                break;
        }
    }

    /**
     * 获取要显示的提示条
     *
     * @param iterator
     * @return
     */
    private TipsBarType getShowTip(Iterator iterator) {
        TipsBarType tipsBarType = TipsBarType.none;
        if (iterator != null) {
            // 根据优先级取到要显示的提示条
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                TipsBarType key = (TipsBarType) entry.getKey();
                if (getTipsBarData().getTypeNeedShow().get(key)) {
                    if (key.getWeight() > tipsBarType.getWeight()) {
                        tipsBarType = key;
                    }
                }
            }
        }
        return tipsBarType;
    }


    /**
     * 获取最高权限提示条类型
     *
     * @param iterator
     * @return
     */
    private TipsBarType getMaxWeight(Iterator iterator) {
        TipsBarType tipsBarType = TipsBarType.none;
        if (iterator != null) {
            //
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                TipsBarType key = (TipsBarType) entry.getKey();

                if (key.getWeight() > tipsBarType.getWeight()) {
                    tipsBarType = key;
                }
            }
        }
        Log.d("_test", "getMaxWeight ---- name = " + tipsBarType.name());
        return tipsBarType;
    }

    /**
     * 设置要显示的提示条状态
     *
     * @param type
     */
    public void setIsSHowType(TipsBarType type) {
        Map<TipsBarType, Integer> typeIntegerMap = getTipsBarData().getIntegerMap();
        Iterator iterator = typeIntegerMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            TipsBarType key = (TipsBarType) entry.getKey();
            //把提示条状态设置为已经显示
            if (key == type) {
                getTipsBarData().getIntegerMap().put(key, getTipsBarData().Show_Already);
            } else {
                //其他的不要当前显示的提示条状态设置为未显示
                getTipsBarData().getIntegerMap().put(key, getTipsBarData().Show_No);
            }
        }
    }

    /**
     * 判断当前提示条是否需要显示
     *
     * @param type 要显示的提示条类型
     * @return 返回true代表要显示的提示条需要显示 返回false 则不需要显示
     */
    private boolean isNeedSHow(TipsBarType type) {
        Map<TipsBarType, Integer> typeIntegerMap = getTipsBarData().getIntegerMap();
        Iterator iterator = typeIntegerMap.entrySet().iterator();
        TipsBarType tipsBarType = getMaxWeight(iterator); //当前最高权限的提示条
        //判断要显示的提示条是不是最高权限的
        if (type == tipsBarType) {
            //如果没有显示就需要去显示 则返回false
            if (typeIntegerMap.get(tipsBarType) == getTipsBarData().Show_Already) {
                return false;
            } else {
                return true;
            }
        }
        //要显示的提示条不是最高权限的时候就判断 有没有显示的提示条
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            TipsBarType key = (TipsBarType) entry.getKey();
            //有显示的提示条的时候 判断要显示的提示条两者之间的权限大小
            if (key.getTime() == -1
                    && typeIntegerMap.get(key) == getTipsBarData().Show_Already) {
                //要显示的提示条权限高就需要去显示
                if (type.getWeight() > key.getWeight()) {
                    return true;
                }
                return false;
            }
        }
        return true;
    }

    /**
     * 设置是否需要显示
     *
     * @param tipsBarType
     * @param isNeedShow
     */
    private void setNeedShow(TipsBarType tipsBarType, boolean isNeedShow) {
        getTipsBarData().getTypeNeedShow().put(tipsBarType, isNeedShow);
    }


    public Map<Integer, TipsBarBasePanel> barBasePanelMap = new HashMap<>();

    /**
     * 获取要显示的提示条view
     *
     * @param tipsBarType
     * @return
     */
    private View getTipsView(TipsBarType tipsBarType) {
        TipsBarBasePanel barBasePanel = barBasePanelMap.get(tipsBarType.getBarType());

        if (barBasePanel != null) {
            if (getTipsBarData().getObject() != null) {
                Log.d("_test", "getTipsBarData().getObject() -- " + getTipsBarData().getObject().toString());
            } else {
                Log.d("_test", "getTipsBarData().getObject() -- " + (getTipsBarData().getObject() == null));
            }
            barBasePanel.init(getTipsBarData().getObject());
            return barBasePanel.getContentView();
        } else {
            Class<? extends TipsBarBasePanel> panelClass = tipsBarType.getBaseViewPanel();
            if (panelClass == null) {
                return null;
            }

            try {
                Constructor c = panelClass.getDeclaredConstructor();
                TipsBarBasePanel chatPanel = (TipsBarBasePanel) c.newInstance();
                chatPanel.init(getTipsBarData().getObject());
                barBasePanelMap.put(tipsBarType.getBarType(), chatPanel);
                return chatPanel.getContentView();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}