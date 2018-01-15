package com.juxin.predestinate.ui.live.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.ui.live.adapter.LiveUserRankAdapter;
import com.juxin.predestinate.ui.live.bean.EnterLiveBean;
import com.juxin.predestinate.ui.live.bean.LiveUserRankBean;
import com.juxin.predestinate.ui.live.event.LiveCmdBusEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by terry on 2017/9/13.
 */

public class LiveUserRankView extends RecyclerView{

    Context mContext;
    String roomid;                              //房间id
    int roomUserPage = 1;                       //加载观众列表page
    boolean isLoadMoreRoom = false;             //是否加载更多观众
    LiveUserRankAdapter mEnterRoomHeadAdapter;

    public LiveUserRankView(Context context) {
        super(context);
    }

    public LiveUserRankView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LiveUserRankView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initEnterRoomUserHeadPortrait(Context context, boolean isGirlLive,String roomid, EnterLiveBean liveBean){
        mContext = context;
        this.roomid = roomid;

        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext );
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        this.setLayoutManager(layoutManager);

        this.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalCount = layoutManager.getItemCount();
                int lastVisibleCount = layoutManager.findLastVisibleItemPosition();
                if (!isLoadMoreRoom &&  totalCount==lastVisibleCount+1){//倒数第二个开始加载下一页
                    getRoomUserInfo();
                    isLoadMoreRoom = true;
                }
            }
        });

        mEnterRoomHeadAdapter = new LiveUserRankAdapter(mContext,liveBean,isGirlLive);
        setAdapter(mEnterRoomHeadAdapter);
        getRoomUserList();


    }

    //获取房间观众列表
    private void getRoomUserList(){
        roomUserPage = 1;
        mEnterRoomHeadAdapter.clear();
        getRoomUserInfo();
    }

    /**
     * 收到cmd 消费信息后 开始刷新消费人员列表
     * @param data
     */
    public int appendData(LiveCmdBusEvent data){
        boolean isContain = false;
        int position = 0;
        //为初始化好 人员消费小于等于0的 不再加入到消费人员列表 修改 2017/9/24 有没有消费都在人员列表
        if (mEnterRoomHeadAdapter == null || mEnterRoomHeadAdapter.dataList == null)return 0;
        for (int i=0;i<mEnterRoomHeadAdapter.dataList.size();i++){
            if (mEnterRoomHeadAdapter.dataList.get(i).uid == data.uid){
                isContain = true;
                position = i;
                break;
            }
        }

        if (isContain){
            LiveUserRankBean bean = mEnterRoomHeadAdapter.dataList.get(position);
            bean.setDiamond(data.consume);
        }else{
            LiveUserRankBean bean = new LiveUserRankBean();
            bean.setAvatar(data.avatar);
            bean.setLevel(data.nobilityLevel);
            bean.setGender(data.gender);
            bean.setUid(data.uid);
            bean.setDiamond(data.consume);
            mEnterRoomHeadAdapter.dataList.add(bean);
        }
        Collections.sort(mEnterRoomHeadAdapter.dataList,new DiamondSort());
        mEnterRoomHeadAdapter.notifyDataSetChanged();
        return mEnterRoomHeadAdapter.dataList.size();
    }

    /**
     * 用户退出
     * @param data
     */
    public int userLeave(LiveCmdBusEvent data){
        boolean isContain = false;
        if (mEnterRoomHeadAdapter == null || mEnterRoomHeadAdapter.dataList == null)return 0;
        for (int i=0;i<mEnterRoomHeadAdapter.dataList.size();i++){
            if (mEnterRoomHeadAdapter.dataList.get(i).uid == data.uid){
                mEnterRoomHeadAdapter.dataList.remove(i);
                isContain = true;
                break;
            }
        }
        if (isContain){
            Collections.sort(mEnterRoomHeadAdapter.dataList,new DiamondSort());
            mEnterRoomHeadAdapter.notifyDataSetChanged();
        }
        return mEnterRoomHeadAdapter.dataList.size();
    }

    private void getRoomUserInfo(){
        HashMap<String,Object> postParam = new HashMap<>();
        postParam.put("roomid",roomid);//
        postParam.put("page",roomUserPage);//
        postParam.put("limit","20");//
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.roomConsumerList, postParam, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                isLoadMoreRoom = false;
                if(response.isOk()){
                    try {
                        if (response.getResponseJson().has("res") && !response.getResponseJson().isNull("res") ){
                            JSONObject resJo = response.getResponseJson().getJSONObject("res");
                            if (resJo.has("list") && !resJo.isNull("list") && resJo.getJSONArray("list").length()>0){
                                roomUserPage++;
                                ArrayList<LiveUserRankBean> liveRoomUserList = new ArrayList<>();
                                JSONArray dataArr = resJo.getJSONArray("list");
                                for (int i=0;i<dataArr.length();i++){
                                    LiveUserRankBean bean = new LiveUserRankBean();
                                    bean.setAvatar(dataArr.getJSONObject(i).optString("avatar"));
                                    bean.setLevel(dataArr.getJSONObject(i).optInt("nobility_level"));
                                    bean.setGender(dataArr.getJSONObject(i).optInt("gender"));
                                    bean.setUid(dataArr.getJSONObject(i).optLong("uid"));
                                    bean.setDiamond(dataArr.getJSONObject(i).optInt("diamond"));
                                    liveRoomUserList.add(bean);
                                }

                                if (!liveRoomUserList.isEmpty()){
                                    mEnterRoomHeadAdapter.addListData(liveRoomUserList);
                                    Collections.sort(mEnterRoomHeadAdapter.dataList,new DiamondSort());
                                    mEnterRoomHeadAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private class DiamondSort implements Comparator{

        @Override
        public int compare(Object o1, Object o2) {
            if (((LiveUserRankBean)o1).getDiamond() > ((LiveUserRankBean)o2).getDiamond()){
                return -1;
            }else if (((LiveUserRankBean)o1).getDiamond() == ((LiveUserRankBean)o2).getDiamond()){
                return 0;
            }else{
                return 1;
            }
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }
}
