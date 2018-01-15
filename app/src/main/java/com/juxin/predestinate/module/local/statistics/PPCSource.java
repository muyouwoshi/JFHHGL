package com.juxin.predestinate.module.local.statistics;

/**
 * 类描述：ppc 统计信息
 * 创建时间：2017/10/16 17:11
 * 修改时间：2017/10/16 17:11
 * Created by zhoujie on 2017/10/16
 * 修改备注：
 */

public class PPCSource {
    private long uid;            //聊天冲vip/Y币/钥匙/钻石/当前聊天页面/他人的信息:为当前聊天页面用户uid;
    private int channel_id;     //聊天冲vip/Y币/钥匙/钻石/当前聊天页面/他人的信息:为当前聊天页面用户channel_uid;
    public PPCSource(long uid,int channel_id){
        this.uid = uid;
        this.channel_id = channel_id;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(Long uid){
        this.uid = uid;
    }

    public int getChannelID() {
        return channel_id;
    }

    public void setChannelID(int channel_id) {
        this.channel_id = channel_id;
    }

}
