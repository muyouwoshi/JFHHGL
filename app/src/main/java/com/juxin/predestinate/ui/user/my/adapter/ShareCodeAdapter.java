package com.juxin.predestinate.ui.user.my.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PSP;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.my.ShareUrl;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.ui.live.bean.ShareMatrial;

import java.util.List;

/**
 * 创建日期：2017/10/30
 * 描述:
 *
 * @author :lc
 */
public class ShareCodeAdapter extends ExBaseAdapter<ShareMatrial> {

    public ShareCodeAdapter(Context context, List<ShareMatrial> datas) {
        super(context, datas);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder mHolder;
        if (convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflate(R.layout.item_share_code);
            mHolder.iv_code_mode = convertView.findViewById(R.id.iv_code_mode);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        int selectIndex = PSP.getInstance().getInt(ModuleMgr.getCommonMgr().getPrivateKey(Constant.SHARE_CODE_MODE), 0);
        if (position == selectIndex) {
            mHolder.iv_code_mode.setBackgroundResource(R.drawable.spread_red_stroke);
        } else {
            mHolder.iv_code_mode.setBackgroundResource(0);
        }
        ImageLoader.loadRoundCenterCrop(getContext(), getItem(position).getPreviewImageUrl(), mHolder.iv_code_mode);
        return convertView;
    }

    private static class ViewHolder {
        ImageView iv_code_mode;
    }
}
