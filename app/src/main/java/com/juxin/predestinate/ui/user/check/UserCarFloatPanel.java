package com.juxin.predestinate.ui.user.check;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.module.logic.application.ModuleMgr;

/**
 * 创建日期：2017/7/24
 * 描述:TA 人资料男号等级汽车悬浮显示
 * <p>
 * 作者:lc
 */
@Deprecated
public class UserCarFloatPanel extends BasePanel {
    private final int channel;
    private UserDetail userDetail;
    private NobilityList.Nobility nobility;

    public UserCarFloatPanel(Context context, int channel, UserDetail userProfile) {
        super(context);
        setContentView(R.layout.p2_user_car_float);
        this.channel = channel;
        this.userDetail = userProfile;
        nobility = ModuleMgr.getCommonMgr().getCommonConfig().queryNobility(userProfile.getNobilityInfo().getRank(), userDetail.getGender());
        initView();
    }

    private void initView() {
        if (userDetail.isMan()) {
            ImageView iv_float_car = (ImageView) findViewById(R.id.iv_float_car);
            ImageLoader.loadFitCenter(getContext(), nobility.getCar_pic(), iv_float_car, R.drawable.f1_icon_car, R.drawable.f1_icon_car);
        }
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        UserPartInfoPanel partInfoPanel = new UserPartInfoPanel(context, channel, userDetail);
        container.addView(partInfoPanel.getContentView());
    }
}
