package com.juxin.predestinate.module.local.statistics;

import com.juxin.predestinate.bean.StackNode;
import com.juxin.predestinate.bean.live.RoomPayInfo;
import com.juxin.predestinate.ui.live.ui.LiveTopFragment;

/**
 * 类描述：roomPay统计信息容器，此类需要钻石统计来源信息
 * 创建时间：2017/10/12 16:21
 * 修改时间：2017/10/12 16:21
 * Created by Administrator on 2017/10/12
 * 修改备注：
 */

public class RoomPaySourceContainer extends DiamondSource.DiamondSourceContainer{
    private RoomPayInfo info;
    private CommonSource.CommonSourceContainer sourceContainer;

    RoomPaySourceContainer(CommonSource.CommonSourceContainer sourceContainer) {
        super(sourceContainer);
        this.sourceContainer = sourceContainer;
    }
    public void setInfo(RoomPayInfo info){
        this.info = info;
    }
    public RoomPayInfo getInfo(){
        return info;
    }
}
