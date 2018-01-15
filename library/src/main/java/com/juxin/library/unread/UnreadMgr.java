/*
 * Copyright (C) 2016 sanchi3 Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.juxin.library.unread;

import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.juxin.library.log.PLogger;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 未读角标管理manager
 * Created by sanchi3 on 2016/10/18.
 */
public class UnreadMgr {

    private volatile static UnreadMgr instance = null;

    public static UnreadMgr getInstance() {
        if (instance == null) {
            synchronized (UnreadMgr.class) {
                if (instance == null) {
                    instance = new UnreadMgr();
                }
            }
        }
        return instance;
    }

    // =================================== init ===================================

    private static final String TAG = "UnreadMgr";

    /* ======从MT_Unread_change消息类型getData或得到的Map中获取值的key====== */
    public static final String Msg_Name_Key = "name";       //角标消息中角标名称的获取key
    public static final String Msg_Status_Key = "status";   //角标消息中角标增删状态的获取key

    /* 未读消息的级联关系，每次添加新的层级角标之后在此进行配置 */
    private Map<String, String[]> parentMap = new HashMap<String, String[]>();

    /* 对应未读消息的存储对象 */
    private Map<String, Unread> unreadMap = new HashMap<String, Unread>();

    /**
     * 初始化角标系统
     *
     * @param storeString 存储的角标信息json字符串，可由外部调用选择存储至数据库或SP等
     * @param parentMap   子级和父级的级联关系。只关心最小元素的子级，如：消息tab中有一个最近消息栏目，最近消息中又包括好友和陌生人，
     *                    这时候好友和陌生人就是最小的元素，由这两个最小元素的添加引起了最近消息和消息总数的添加。<p>
     *                    Map<String, String[]> parentMap = new HashMap<String, String[]>();<br>
     *                    parentMap.put("friends",new String[]{"message","recentMessage"});<br>
     *                    parentMap.put("strangers",new String[]{"message","recentMessage"});<p>
     *                    以上演示了一个三级标示消息的结构，二级的结构如下：<br>
     *                    parentMap.put("friends",new String[]{"message","lookedMe"});<p>
     *                    只有一级的标示消息无需添加父级层联关系的map。
     */
    public void init(String storeString, Map<String, String[]> parentMap) {
        this.parentMap = (parentMap == null ? new HashMap<String, String[]>() : parentMap);
        unreadMap.clear();//应用未杀死时切换用户，清除上个用户的信息

        PLogger.d("getUnreadMessage: --------->stored string：" + storeString);
        if (TextUtils.isEmpty(storeString)) return;

        try {
            unreadMap = JSON.parseObject(storeString, new TypeReference<Map<String, Unread>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (unreadMap == null) unreadMap = new HashMap<String, Unread>();
        }
    }

    // =================================== 外部调用 ===================================

    /**
     * 设置未读角标变动监听
     */
    public void setUnreadListener(UnreadListener unreadListener) {
        this.unreadListener = unreadListener;
    }

    /**
     * 通过角标的key获取存储的角标对象的显示数目。只针对数值类角标
     *
     * @param key 角标key值
     * @return 存储的单个角标对象
     */
    public int getUnreadNumByKey(String key) {
        Unread unread = unreadMap.get(key);
        return unread == null ? 0 : unread.getNum();
    }

    /**
     * 注册角标view，写在页面resume的位置，保证每次用户可见都会更新角标
     *
     * @param badge   注册角标的view
     * @param isPoint 是否只显示角标点
     * @param key     注册角标的key
     */
    public void registerBadge(BadgeView badge, boolean isPoint, String key) {
        if (badge == null || TextUtils.isEmpty(key)) return;

        Unread unread = unreadMap.get(key);
        PLogger.d("registerView：key：" + key + "，isPoint：" + isPoint + "，unread：" + unread);

        if (unread == null) {
            badge.setVisibility(View.GONE);
        } else {
            int num = unread.getNum();
            String show = unread.getShow();
            if (num < 1 && TextUtils.isEmpty(show)) {//空内容
                badge.setVisibility(View.GONE);
            } else {
                badge.setVisibility(View.VISIBLE);
                if (isPoint) {
                    badge.setPoint();
                } else {
                    if (isNum(unread)) {
                        badge.setText(num > 99 ? "99+" : String.valueOf(num));
                    } else {
                        badge.setText(show);
                    }
                }
            }
        }
    }

    /**
     * 外部添加一个未读消息类型
     *
     * @param unread 拼接的未读消息体
     */
    public void addUnread(Unread unread) {
        if (unread == null) return;
        unreadMap.put(unread.getKey(), unread);

        addParent(unread.getKey());
        PLogger.d("addUnread: --------->unread：" + unread.toString() + "，unreadMap：" + unreadMap.toString());

        castUnreadMsg(unread.getKey(), true);
    }

    /**
     * 添加数字角标，每次单独+1
     *
     * @param key 角标的key
     */
    public void addNumUnread(String key) {
        if (TextUtils.isEmpty(key)) return;

        Unread unread = unreadMap.get(key);
        if (unread == null) unread = new Unread();
        unread.setKey(key);
        unread.setNum(unread.getNum() + 1);
        unreadMap.put(key, unread);//存储子角标

        addParent(key);
        PLogger.d("addNumUnread: --------->key：" + key + "，unreadMap：" + unreadMap.toString());

        castUnreadMsg(key, true);
    }

    /**
     * 添加数字角标，每次单独+1。只有数字型子级角标才可调用该方法
     *
     * @param key      角标的key
     * @param indicate 角标的唯一标识，只有子级角标才拥有该字段
     */
    public void addUnreadIndicant(String key, String indicate) {
        if (TextUtils.isEmpty(key)) return;

        Unread unread = unreadMap.get(key);
        if (unread == null) unread = new Unread();

        List<String> indicates = unread.getIndicates();
        if (indicates.contains(indicate)) return;//如果该子级角标中已经存在该标识的角标，就不再进行添加

        unread.setKey(key);
        unread.setNum(unread.getNum() + 1);
        if (!TextUtils.isEmpty(indicate)) {
            indicates.add(indicate);
            unread.setIndicates(indicates);
        }
        unreadMap.put(key, unread);//存储子角标

        addParent(key);
        PLogger.d("addNumUnread: --------->key：" + key + "，unreadMap：" + unreadMap.toString());

        castUnreadMsg(key, true);
    }

    /**
     * 添加字符串角标，该类型的服级角标只添加一次
     *
     * @param key 角标的key
     */
    public void addStringUnread(String key, String show) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(show)) return;

        Unread unread = unreadMap.get(key);
        if (unread == null) {
            addParent(key);
            unread = new Unread();
        }
        unread.setKey(key);
        unread.setShow(show);
        unreadMap.put(key, unread);//存储子角标

        PLogger.d("addStringUnread: --------->key：" + key + "，unreadMap：" + unreadMap.toString());

        castUnreadMsg(key, true);
    }

    /**
     * 从存储中移除指定key值的一个未读值
     *
     * @param key 未读key
     */
    public void reduceUnreadByKey(String key) {
        Unread unread = unreadMap.get(key);
        if (TextUtils.isEmpty(key) || unread == null) return;

        reduceParent(key);//父级角标递减

        //子级角标递减
        int num = unread.getNum();
        if (isNum(unread)) {//数字角标
            if (num <= 1) {
                unreadMap.remove(key);
            } else {
                unread.setNum(num - 1);
                unreadMap.put(key, unread);
            }
        } else {//文字角标
            unreadMap.remove(key);
        }
        PLogger.d("reduceUnreadByKey: --------->key：" + key + "，unreadMap：" + unreadMap.toString());

        castUnreadMsg(key, false);
    }

    /**
     * 从存储中移除指定key值的一个未读值。只有数字型子级角标才可调用该方法
     *
     * @param key      角标的key
     * @param indicate 角标的唯一标识，只有子级角标才拥有该字段
     */
    public void reduceUnreadIndicate(String key, String indicate) {
        Unread unread = unreadMap.get(key);
        if (TextUtils.isEmpty(key) || unread == null) return;

        List<String> indicates = unread.getIndicates();
        if (indicates.isEmpty() || (!TextUtils.isEmpty(indicate) && !indicates.contains(indicate))) {
            PLogger.d("reduceUnreadIndicate: --------->" + key + "类型角标不含" + indicate + "标识");
            return;//如果该子级角标中不存在该标识的角标，就不进行角标消除
        }

        reduceParent(key);//父级角标递减

        //子级角标递减
        int num = unread.getNum();
        if (isNum(unread)) {//数字角标
            if (num <= 1) {
                unreadMap.remove(key);
            } else {
                unread.setNum(num - 1);
                indicates.remove(indicate);
                unread.setIndicates(indicates);
                unreadMap.put(key, unread);
            }
        } else {//文字角标
            unreadMap.remove(key);
        }
        PLogger.d("reduceUnreadIndicate: --------->key：" + key + "，unreadMap：" + unreadMap.toString());

        castUnreadMsg(key, false);
    }

    /**
     * 从存储中移除指定key值的所有未读信息。
     *
     * @param key 未读key
     */
    public void resetUnreadByKey(String key) {
        if (TextUtils.isEmpty(key)) return;

        clearChildInParent(key);//先移除父级再移除子级，因为父级的计算要依赖于子级
        unreadMap.remove(key);
        PLogger.d("resetUnreadByKey: --------->key：" + key + "，unreadMap：" + unreadMap.toString());

        castUnreadMsg(key, false);
    }

    /**
     * 清除所有存储的未读角标
     */
    public void resetAllUnread() {
        unreadMap.clear();
        PLogger.d("resetAllUnread: --------->" + unreadMap.toString());
        castUnreadMsg(null, false);
    }


    // =================================== 内部调用 ===================================

    private UnreadListener unreadListener;

    /**
     * 抛出角标变动消息的监听
     */
    public interface UnreadListener {
        /**
         * 角标变动
         *
         * @param key         角标类型的key值
         * @param isAdd       是否为角标添加消息：true[添加]，false[减少]
         * @param storeString 需要存储的角标信息json字符串，可由外部调用选择存储至数据库或SP等
         */
        void onUnreadChange(String key, boolean isAdd, String storeString);
    }

    /**
     * 抛出角标变动消息
     *
     * @param key   角标类型的key值
     * @param isAdd 是否为角标添加消息：true[添加]，false[减少]
     */
    private void castUnreadMsg(String key, boolean isAdd) {
        if (unreadListener != null)
            unreadListener.onUnreadChange(key, isAdd, JSON.toJSONString(unreadMap));

        Map<String, Object> msgMap = new HashMap<String, Object>();
        msgMap.put(Msg_Name_Key, key);
        msgMap.put(Msg_Status_Key, isAdd);
        MsgMgr.getInstance().sendMsg(MsgType.MT_Unread_change, msgMap);
    }

    /**
     * 判断该角标是否是数字角标
     *
     * @param unread 角标实体
     * @return 数字角标：true，文字角标：false
     */
    private boolean isNum(Unread unread) {
        return TextUtils.isEmpty(unread.getShow());
    }

    /**
     * 根据子级角标的key递增父级角标。父级角标只关心num，不关心其他字段，isPoint默认为false，show默认为""
     *
     * @param key 子级角标的key
     */
    private void addParent(String key) {
        if (TextUtils.isEmpty(key) || parentMap == null) return;
        String[] parent = parentMap.get(key);
        if (parent == null) return;//如果没有父级角标，就返回

        for (String s : parent) {
            Unread parentUnread = unreadMap.get(s);
            if (parentUnread == null) parentUnread = new Unread();//如果父级角标为空，就创建
            parentUnread.setKey(s);
            parentUnread.setNum(parentUnread.getNum() + 1);
            unreadMap.put(s, parentUnread);//存储父角标
        }
    }

    /**
     * 根据子级角标的key递减父级角标.。父级角标只关心num，不关心其他字段，isPoint默认为false，show默认为""
     *
     * @param key 子级角标的key
     */
    private void reduceParent(String key) {
        Unread unread = unreadMap.get(key);
        String[] parent = parentMap.get(key);
        if (unread == null || parent == null) return;//如果没有该类型角标或者没有该类型的父级角标，就返回

        for (String s : parent) {
            Unread parentUnread = unreadMap.get(s);
            if (parentUnread != null) {
                //不管是数字还是文字角标，如果父级角标的个数<=1，就从集合中移除该父级角标。否则角标-1
                if (parentUnread.getNum() <= 1) {
                    unreadMap.remove(s);
                } else {
                    parentUnread.setNum(parentUnread.getNum() - 1);
                    unreadMap.put(s, parentUnread);
                }
            }
        }
    }

    /**
     * 在父级角标中清除指定的子级角标
     *
     * @param key 子级角标的key
     */
    private void clearChildInParent(String key) {
        Unread unread = unreadMap.get(key);
        String[] parent = parentMap.get(key);
        if (unread == null || parent == null) return;//如果没有该类型角标或者没有该类型的父级角标，就返回

        int num = unread.getNum();
        for (String s : parent) {
            Unread parentUnread = unreadMap.get(s);
            if (parentUnread != null) {
                int parentNum = parentUnread.getNum();
                if (isNum(unread)) {//数字角标
                    if (num >= parentNum) {//如果子级角标的个数>=父级角标的个数，就从集合中移除该父级角标
                        unreadMap.remove(s);
                    } else {
                        parentUnread.setNum(parentUnread.getNum() - num);
                        unreadMap.put(s, parentUnread);
                    }
                } else {//如果是文字角标，父级角标个数-1
                    if (parentNum <= 1) {
                        unreadMap.remove(s);
                    } else {
                        parentUnread.setNum(parentUnread.getNum() - 1);
                        unreadMap.put(s, parentUnread);
                    }
                }
            }
        }
    }
}