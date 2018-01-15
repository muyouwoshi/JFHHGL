package com.juxin.predestinate.bean.config.base;

import android.util.Log;

import com.juxin.predestinate.bean.net.BaseData;
import com.juxin.predestinate.ui.user.util.CenterConstant;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * config/GetSet#nobility_config#nobility_titles节点，爵位配表
 * Created by ZRP on 2017/7/19.
 */

public class NobilityList extends BaseData {

    private List<Nobility> nobilityList;
    private List<CloseFriend> closeFriendList;

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObject(jsonStr);
        nobilityList = (List<Nobility>) getBaseDataList(jsonObject.optJSONArray("nobility_titles"), Nobility.class);
        closeFriendList = (List<CloseFriend>) getBaseDataList(jsonObject.optJSONArray("close_friend_config"), CloseFriend.class);
    }

    /**
     * 获取第二个爵位
     * ( 每个爵位分 5 级 )
     */
    public Nobility queryLv2Nobility(int gender) {
        return queryNobility(CenterConstant.TITLE_LEVEL_LOW + 1, gender);
    }

    /**
     * 根据爵位 level 获取当前爵位的信息
     *
     * @param level 爵位级别level 从1开始
     * @return 当前爵位的信息
     */
    public Nobility queryNobility(int level, int gender) {
        if (nobilityList == null || nobilityList.isEmpty()) {
            return new NobilityList.Nobility();
        }
        // 兼容越界查询，返回最高级别爵位信息
        if (level > nobilityList.size()/2) level = nobilityList.size()/2;
        for (Nobility temp : nobilityList) {
            if (level == temp.getLevel() && gender == temp.getGender()) return temp;
        }
        return new Nobility();
    }

    /**
     * 获取Nobility 小于5级的是空数据对象
     *
     * @param level 爵位level 从1开始
     * @return 当前爵位的信息
     */
    public Nobility queryNobility2(int level, int gender) {
        if (nobilityList == null || nobilityList.isEmpty()) {
            return new Nobility();
        }
        // 兼容越界查询，返回最高级别爵位信息
        if (level > nobilityList.size()/2) level = nobilityList.size()/2;
        if (level <= CenterConstant.TITLE_LEVEL_LOW) {
            return new Nobility();
        }
        for (Nobility temp : nobilityList) {
            if (level == temp.getLevel() && gender == temp.getGender()) return temp;
        }
        return new Nobility();
    }

    /**
     * 根据level获取当前密友的信息
     *
     * @param level 等级 从1开始
     * @return 当前密友的信息
     */
    public CloseFriend queryCloseFriend(int level) {
        if (closeFriendList == null || closeFriendList.isEmpty()) {
            return new CloseFriend();
        }
        // 兼容越界查询，返回最高级别爵位信息
        if (level > closeFriendList.size()) level = closeFriendList.size();
        for (CloseFriend temp : closeFriendList) {
            if (level == temp.getLevel()) return temp;
        }
        return new CloseFriend();
    }

    public List<CloseFriend> getCloseFriendList() {
        return closeFriendList == null ? new ArrayList<CloseFriend>() : closeFriendList;
    }

    public List<Nobility> getNobilityList() {
        return nobilityList == null ? new ArrayList<Nobility>() : nobilityList;
    }

    @Override
    public String toString() {
        return "NobilityList{" +
                "nobilityList=" + nobilityList +
                '}';
    }

    /**
     * 爵位信息
     */
    public static class Nobility extends BaseData {
        private int id;                         //主键自增
        private int level;                      //爵位等级
        private String car_name;                //座驾名
        private String car_pic;                 //爵位座驾
        private int gender;                     //性别 1男，2女
        private String guard_name;              //爵位守护图腾名字
        private String guard_pic;               //爵位守护图腾
        private String list_bg;                 //列表背景
        private String main_page_bg;            //主页背景
        private String ranking_list_bg;         //排行榜背景
        private int star;                       //爵位星级 如：1星
        private String star_name;               //爵位星级 如：1星
        private String title_badge;             //爵位等级 如：民、贵、子、男
        private String title_icon;              //爵位标签图标 如：lv1平民 带有级别
        private String title_name;              //爵位名称 如：平民
        private String title_name_pic;          //爵位勋章 如：平民（两字图片）
        private String update_title_name_pic;   //荣升文字
        private int upgrade_condition;          //升级条件 经验（1钻石=1经验）

        @Override
        public void parseJson(String jsonStr) {
            JSONObject jsonObject = getJsonObject(jsonStr);
            id = jsonObject.optInt("id");
            level = jsonObject.optInt("level");
            car_name = jsonObject.optString("car_name");
            car_pic = jsonObject.optString("car_pic");
            gender = jsonObject.optInt("gender");
            guard_name = jsonObject.optString("guard_name");
            guard_pic = jsonObject.optString("guard_pic");
            list_bg = jsonObject.optString("list_bg");
            main_page_bg = jsonObject.optString("main_page_bg");
            ranking_list_bg = jsonObject.optString("ranking_list_bg");
            star = jsonObject.optInt("star");
            star_name = jsonObject.optString("star_name");
            title_badge = jsonObject.optString("title_badge");
            title_icon = jsonObject.optString("title_icon");
            title_name = jsonObject.optString("title_name");
            title_name_pic = jsonObject.optString("title_name_pic");
            update_title_name_pic = jsonObject.optString("update_title_name_pic");
            upgrade_condition = jsonObject.optInt("upgrade_condition");
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getCar_name() {
            return car_name;
        }

        public void setCar_name(String car_name) {
            this.car_name = car_name;
        }

        public String getCar_pic() {
            return car_pic;
        }

        public void setCar_pic(String car_pic) {
            this.car_pic = car_pic;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public String getGuard_name() {
            return guard_name;
        }

        public void setGuard_name(String guard_name) {
            this.guard_name = guard_name;
        }

        public String getGuard_pic() {
            return guard_pic;
        }

        public void setGuard_pic(String guard_pic) {
            this.guard_pic = guard_pic;
        }

        public String getList_bg() {
            return list_bg;
        }

        public void setList_bg(String list_bg) {
            this.list_bg = list_bg;
        }

        public String getMain_page_bg() {
            return main_page_bg;
        }

        public void setMain_page_bg(String main_page_bg) {
            this.main_page_bg = main_page_bg;
        }

        public String getRanking_list_bg() {
            return ranking_list_bg;
        }

        public void setRanking_list_bg(String ranking_list_bg) {
            this.ranking_list_bg = ranking_list_bg;
        }

        public int getStar() {
            return star;
        }

        public void setStar(int star) {
            this.star = star;
        }

        public String getStar_name() {
            return star_name;
        }

        public void setStar_name(String star_name) {
            this.star_name = star_name;
        }

        public String getTitle_badge() {
            return title_badge;
        }

        public void setTitle_badge(String title_badge) {
            this.title_badge = title_badge;
        }

        public String getTitle_icon() {
            return title_icon;
        }

        public void setTitle_icon(String title_icon) {
            this.title_icon = title_icon;
        }

        public String getTitle_name() {
            return title_name;
        }

        public void setTitle_name(String title_name) {
            this.title_name = title_name;
        }

        public String getTitle_name_pic() {
            return title_name_pic;
        }

        public void setTitle_name_pic(String title_name_pic) {
            this.title_name_pic = title_name_pic;
        }

        public String getUpdate_title_name_pic() {
            return update_title_name_pic;
        }

        public void setUpdate_title_name_pic(String update_title_name_pic) {
            this.update_title_name_pic = update_title_name_pic;
        }

        public int getUpgrade_condition() {
            return upgrade_condition;
        }

        public void setUpgrade_condition(int upgrade_condition) {
            this.upgrade_condition = upgrade_condition;
        }
    }

    //密友信息
    public static class CloseFriend extends BaseData {
        private int discount;            //视频语音打折
        private int experience;            //升级经验值
        private String icon;               //图标
        private int level;                 //密友等级
        private String title;              //密友称号
        private String up_level_icon;      //升级弹窗图标

        @Override
        public void parseJson(String jsonStr) {
            JSONObject jsonObject = getJsonObject(jsonStr);
            this.setDiscount(jsonObject.optInt("discount"));
            experience = jsonObject.optInt("experience");
            icon = jsonObject.optString("icon");
            level = jsonObject.optInt("level");
            title = jsonObject.optString("title");
            up_level_icon = jsonObject.optString("up_level_icon");
        }

        public int getDiscount() {
            return discount;
        }

        public void setDiscount(int discount) {
//            while (discount > 10){
//                discount /= 10;
//            }
//            this.discount = discount;
            this.discount = 10;
        }

        public int getExperience() {
            return experience;
        }

        public void setExperience(int experience) {
            this.experience = experience;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUp_level_icon() {
            return up_level_icon;
        }

        public void setUp_level_icon(String up_level_icon) {
            this.up_level_icon = up_level_icon;
        }
    }
}
