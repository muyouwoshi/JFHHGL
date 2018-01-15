package com.juxin.predestinate.ui.agora;

import com.juxin.library.observe.ModuleBase;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.ui.agora.model.EngineConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 音视频通信管理类[dev主分支处理，合并到全量分支时直接使用全量分支的代码]
 * <p>
 * Created by Su on 2017/3/17.
 */
public class RtcEngineMgr implements ModuleBase {

    private static EngineConfig mEngineConfig;        // 配置信息

    @Override
    public void init() {
    }

    @Override
    public void release() {
    }

    public RtcEngineMgr() {
        mEngineConfig = new EngineConfig();
    }

    /**
     * 获取引擎配置
     */
    public EngineConfig getEngineConfig() {
        if (mEngineConfig == null) mEngineConfig = new EngineConfig();
        return mEngineConfig;
    }

    // ====================== 音视频通信相关请求 =========================

    /**
     * 邀请对方视频聊天
     *
     * @param chatType 聊天类型 1 视频  2 音频
     * @param toUid    对方uid
     */
    public void reqInviteChat(int chatType, long toUid, int usecard, String source, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("vtype", chatType);
        postParams.put("tuid", toUid);
        postParams.put("usecard", usecard);
        if(source != null){
            postParams.put("source", source);
        }
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqInviteChat, postParams, complete);
    }

    /**
     * 拒绝视频聊天请求
     *
     * @param vcid 视频聊天id
     */
    public void reqRejectVideoChat(long vcid, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("vc_id", vcid);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqRejectChat, postParams, complete);
    }

    // ------------------------------ 女性任务版 ----------------------------------

    /**
     * 女性版： 发起单邀
     *
     * @param dstUid 目标id
     */
    public void reqGirlSingleInvite(long dstUid, int type, String source, RequestComplete complete) {
        HashMap<String, Object> postParams = new HashMap<>();
        postParams.put("tuid", dstUid);
        postParams.put("vtype", type);
        if(source != null){
            postParams.put("source", source);
        }
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.girlSingleInviteVa, postParams, complete);
    }

    /**
     * 女性版： 发起群邀
     */
    public void reqGirlGroupInvite(int type, RequestComplete complete) {
        HashMap<String, Object> postParams = new HashMap<>();
        postParams.put("vtype", type);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.girlGroupInviteVa, postParams, complete);
    }

    /**
     * 音视频群发， 取消群发邀请
     */
    public void reqVCGroupCancel(long invitedId, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("invite_id", invitedId);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqVCGroupCancel, postParams, complete);
    }

    /**
     * 接受女性音视频聊天邀请
     *
     * @param invite_id 视频聊天邀请id
     */
    public void reqGirlAcceptChat(long invite_id, boolean isUseVideoCard, String source, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("invite_id", invite_id);
        postParams.put("usecard", isUseVideoCard ? 1 : 0);
        if(source != null){
            postParams.put("source", source);
        }
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqGirlAcceptChat, postParams, complete);
    }

    // ================================= 音视频优化版 ==========================

    /**
     * 获取视频评价信息: 男方调用
     */
    public void reqGetFeedbackInfo(RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("vc_id", getEngineConfig().mVcid);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqGetFeedbackInfo, postParams, complete);
    }

    /**
     * 提交评价： 男方调用
     */
    public void reqAddFeedback(long tuid, int star, List<String> reasons, String others, RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("vc_id", getEngineConfig().mVcid);
        postParams.put("tuid", tuid);
        postParams.put("star", star);
        postParams.put("reasons", reasons.toArray());
        postParams.put("others", others);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqAddFeedback, postParams, complete);
    }

    /**
     * 获取视频结算信息: 女方调用
     */
    public void reqGetSettlement(RequestComplete complete) {
        Map<String, Object> postParams = new HashMap<>();
        postParams.put("vc_id", getEngineConfig().mVcid);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqGetSettlement, postParams, complete);
    }
}
