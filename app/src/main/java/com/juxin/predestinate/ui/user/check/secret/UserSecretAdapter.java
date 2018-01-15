package com.juxin.predestinate.ui.user.check.secret;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserVideo;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.util.TimeUtil;
import com.juxin.predestinate.third.recyclerholder.BaseRecyclerViewAdapter;
import com.juxin.predestinate.third.recyclerholder.BaseRecyclerViewHolder;

/**
 * 私密视频页adapter
 * Created by Su on 2017/3/30.
 */

public class UserSecretAdapter extends BaseRecyclerViewAdapter {
    private int params;   // item宽高

    public UserSecretAdapter(Context context) {
        super(context, null);
        int horizontalSpacing = ModuleMgr.getAppMgr().getScreenWidth() / 50;
        params = (ModuleMgr.getAppMgr().getScreenWidth() - 8 * horizontalSpacing) / 3;
    }

    @Override
    public int[] getItemLayouts() {
        return new int[]{R.layout.p1_user_secret_adapter};
    }

    @Override
    public void onBindRecycleViewHolder(BaseRecyclerViewHolder viewHolder, int position) {
        UserVideo data = (UserVideo) getItem(position);

        ImageView img_preview = viewHolder.findViewById(R.id.iv_video_preview);
        TextView tv_hot = viewHolder.findViewById(R.id.tv_hot_value);
        TextView tv_time = viewHolder.findViewById(R.id.tv_video_time);
        img_preview.setLayoutParams(new RelativeLayout.LayoutParams(params, params));

        if(data.isCanView()){
            ImageLoader.loadAvatar(img_preview.getContext(), data.getPic(), img_preview);
        }else{
            ImageLoader.loadBlur(img_preview.getContext(), data.getPic(), img_preview);
        }
        tv_hot.setText(String.valueOf(data.getViewTimes()));
        tv_time.setText(TimeUtil.getLongToMinuteTime(data.getDuration() * 1000l));
    }

    @Override
    public int getRecycleViewItemType(int position) {
        return 0;
    }
}
