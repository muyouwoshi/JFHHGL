package com.juxin.predestinate.ui.user.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.center.user.detail.UserPhoto;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;

import java.util.List;

/**
 * HorizontalListView适配器
 * Created by Su on 2017/5/12.
 */
public class HorizontalAdapter extends ExBaseAdapter<UserPhoto> {
    private int params;
    private boolean isVip;
    private int channel;

    /**
     * @author Mr.Huang
     * @date 2017-09-06
     * @action update
     * 添加判断参数
     */
    public HorizontalAdapter(Context context, int channel, int params, List<UserPhoto> datas) {
        super(context, datas);
        this.channel = channel;
        this.params = params;
    }

    public void setParams(int params) {
        this.params = params;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        if (convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflate(R.layout.p1_user_checkinfo_secret_adapter);
            mHolder.layout_item_bg = convertView.findViewById(R.id.layout_item_bg);
            mHolder.img_media = (ImageView) convertView.findViewById(R.id.img_media);
            mHolder.img_shade = (ImageView) convertView.findViewById(R.id.img_shade);
            mHolder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
            mHolder.img_media.setLayoutParams(new RelativeLayout.LayoutParams(params, params));
            mHolder.img_shade.setLayoutParams(new RelativeLayout.LayoutParams(params, params));
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        UserPhoto userPhoto = getItem(position);
        String url = ImageLoader.checkOssImageUrl(userPhoto.getPic());
        /**
         * @author Mr.Huang
         * @date 2017-09-06
         * @action update
         * 添加判断是不是自己的相册
         */
        if (position == 0 || isVip || channel == CenterConstant.USER_CHECK_INFO_OWN) {
            mHolder.layout_item_bg.setBackgroundColor(0);
            mHolder.img_shade.setVisibility(View.GONE);
            ImageLoader.loadCenterCrop(getContext(), url, mHolder.img_media);
        } else {
            mHolder.layout_item_bg.setBackgroundColor(0x80000000);
            mHolder.img_shade.setVisibility(View.VISIBLE);
            ImageLoader.loadBlur(getContext(), url, mHolder.img_media);
        }


        return convertView;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    /**
     * @author Mr.Huang
     * 检查男号vip
     * 如果是女号为true
     */
    public void checkVip(){
        UserDetail ud = ModuleMgr.getCenterMgr().getMyInfo();
        if (ud.isMan()) {
            isVip = ud.isVip();
        } else {
            isVip = true;
        }
    }

    @Override
    public void notifyDataSetChanged() {
        checkVip();
        super.notifyDataSetChanged();
    }

    private class ViewHolder {
        ImageView img_media, img_shade;
        TextView tv_num;
        View layout_item_bg;
    }
}