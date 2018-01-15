package com.juxin.predestinate.ui.agora.model;

/**
 * 维护频道信息配置
 */
public class EngineConfig {

    // --- Agora配置 ---
    public long mVcid;          // 频道ID
    public String mChannel;     // 频道ID
    public String mChannelKey;  // 频道加密key
    public boolean mHasJoin;    // 已在频道中

    public boolean isInviteIng=false;//是否在单独邀请中
    public boolean isGroupInviteIng=false;//是否在群邀请中

    // --- 项目配置 ---
    public int msgVer;               // 消息版本号
    public int mInviteType;          // 受邀类型 邀请、被邀请
    public int mChatType;            // 通信类型 音频、视频
    public long mIntervalTime;       // 鉴黄上传间隔在线配置
    public long mIntervalFirstTime;  // 首次鉴黄上传间隔在线配置

    // 女性群发消息
    public long mInviteID;           // 群发邀请id
    public int mGirlType;            // 单聊，群聊
    public long mChatPrice;          // 音视频聊天价格

    // H5信息
    public long mTimespan;            // h5音视频通话时长(单位：s)

    // 他人用户信息
    public long mOtherId;             // 通信用户ID
    public String nickname;
    public int mCamera = 2;          // (男性)视频时开关摄像头配置  1看自己，2不看自己
    public boolean mOpenCamera;      // 对方是否开启了摄像头

    public boolean mJoinStatus;      // 接通状态， true 已接通
    public long mInviteTime;         // 邀请计时
    public long millisPass;          // 通话总时长
    public long millisLeft = -1;     // 余额倒计时时长(单位：s)
    public String mGirlEarn = "0.00";   // 通话女性用户收入
    public int mGroupInvNum = 0;   // 女性群邀人数

    // 日志收集
    public String reason;             // 视频挂断原因

    public boolean isUseVideoCard=false;//是否使用了视频卡
    public int videoCardTime=0;         //视频卡使用时间

    // 重置配置信息
    public void reset() {
        mTimespan = 0;
        millisPass = 0;
        mInviteTime = 0;
        millisLeft = -1;
        mHasJoin = false;
        mJoinStatus = false;
        mOpenCamera = false;
        mChannel = null;
        mChannelKey = null;
    }

    public EngineConfig() {
    }
}
