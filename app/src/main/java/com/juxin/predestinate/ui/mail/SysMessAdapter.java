package com.juxin.predestinate.ui.mail;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.chat.msgtype.SysNoticeMessage;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;
import com.juxin.predestinate.module.util.TimeUtil;
import com.juxin.predestinate.ui.utils.CMDJumpUtil;
import java.util.List;

/**
 * 系统消息
 * Created by zm on 2017/6/13.
 */
public class SysMessAdapter extends ExBaseAdapter<SysNoticeMessage> {

    private Context mContext;

    public SysMessAdapter(Context mContext, List<SysNoticeMessage> datas) {
        super(mContext, datas);
        this.mContext = mContext;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MyViewHolder mHolder;
        if (convertView == null) {
            convertView = inflate(R.layout.p1_sys_mess_item);
            mHolder = new MyViewHolder(convertView);

            convertView.setTag(mHolder);
        } else {
            mHolder = (MyViewHolder) convertView.getTag();
        }
        final SysNoticeMessage noticeMessage = getItem(position);
        if (noticeMessage == null)
            return convertView;

        mHolder.tvTitle.setText(noticeMessage.getMsgDesc() + "");
        mHolder.tvContent.setText(noticeMessage.getInfo());
        if (!TextUtils.isEmpty(noticeMessage.getPic())) {
            mHolder.imgPic.setVisibility(View.VISIBLE);
            mHolder.imgPic.setOnClickListener(new CustomOnClick(noticeMessage));
            ImageLoader.loadCenterCrop(mContext, noticeMessage.getPic(), mHolder.imgPic, 0, 0);
            mHolder.tvJump.setTextColor(ContextCompat.getColor(mContext, R.color.color_zhuyao));
        } else {
            mHolder.imgPic.setVisibility(View.GONE);
            mHolder.tvJump.setTextColor(ContextCompat.getColor(mContext, R.color.color_45A3EC));
        }
        mHolder.tvTips.setText(TimeUtil.millisecondToFormatString(noticeMessage.getTime()));

        mHolder.tvJump.setText(noticeMessage.getBtn_text());
        mHolder.tvContent.setOnClickListener(new CustomOnClick(noticeMessage));
        mHolder.tvJump.setOnClickListener(new CustomOnClick(noticeMessage));
        mHolder.sys_mess_item.setOnClickListener(new CustomOnClick(noticeMessage));

        return convertView;
    }

    class MyViewHolder {

        LinearLayout sys_mess_item;
        TextView tvTips, tvTitle, tvContent, tvJump;
        ImageView imgPic;

        public MyViewHolder(View convertView) {
            initView(convertView);
        }

        private void initView(View convertView) {
            sys_mess_item = (LinearLayout) convertView.findViewById(R.id.sys_mess_item);
            tvTips = (TextView) convertView.findViewById(R.id.sys_mess_item_tv_tips);
            tvTitle = (TextView) convertView.findViewById(R.id.sys_mess_item_tv_title);
            tvContent = (TextView) convertView.findViewById(R.id.sys_mess_item_tv_content);
            tvJump = (TextView) convertView.findViewById(R.id.sys_mess_item_tv_jump);
            imgPic = (ImageView) convertView.findViewById(R.id.sys_mess_item_img_pic);
        }
    }

    private class CustomOnClick implements View.OnClickListener {
        private SysNoticeMessage noticeMessage;

        CustomOnClick(SysNoticeMessage noticeMessage) {
            this.noticeMessage = noticeMessage;
        }

        @Override
        public void onClick(View v) {
            new CMDJumpUtil(App.getActivity(), noticeMessage.getBtn_action(), noticeMessage.getLWhisperID(), null).onCMD();
        }
    }
}