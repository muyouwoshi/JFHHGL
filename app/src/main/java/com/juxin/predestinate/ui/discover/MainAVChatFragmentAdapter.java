package com.juxin.predestinate.ui.discover;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.utils.ViewUtils;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.logic.application.ModuleMgr;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.Huang
 * @date 2017/8/31
 */
public class MainAVChatFragmentAdapter extends BaseAdapter {

    private List<Item> list = new ArrayList<>();
    private OnItemClickListener listener;
    private List<UserInfoLightweight> listUsers = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(View child, UserInfoLightweight userInfoLightweight);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Item getItem(int position) {
        return list.get(position);
    }

    public List<Item> getList() {
        return list;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.fate_video_primary_av_chat_fragmet_item, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        Item item = list.get(position);
        vh.setViewData(item);
        return convertView;
    }

    public void clear() {
        this.list.clear();
    }

    public void addAll(List<Item> list) {
        this.list.addAll(list);
    }

    public void setListData(List<UserInfoLightweight> listUsers){
        this.listUsers.clear();
        this.listUsers.addAll(listUsers);
    }

    public List<UserInfoLightweight> getListUsers(){
        return this.listUsers;
    }

    public int size() {
        int count = 0;
        for (Item item : list) {
            count += item.size;
        }
        return count;
    }

    private final class ViewHolder {

        private MinItemView one;
        private MinItemView two;
        private MinItemView three;

        private ViewHolder(View itemView) {
            this.one = new MinItemView(itemView.findViewById(R.id.item_one));
            this.two = new MinItemView(itemView.findViewById(R.id.item_two));
            this.three = new MinItemView(itemView.findViewById(R.id.item_three));
        }

        private void setViewData(Item item) {
            update(item.one, 1);
            update(item.two, 2);
            update(item.three, 3);
        }

        private void update(UserInfoLightweight u, int index) {
            switch (index) {
                case 1:
                    one.update(u);
                    break;
                case 2:
                    two.update(u);
                    break;
                case 3:
                    three.update(u);
                    break;
            }
        }

    }

    private final class MinItemView implements View.OnClickListener {
        private ImageView ivHead, ivMedia, ivTitle;
        private TextView tvName, tvAgeKM;
        private View view;
        private UserInfoLightweight u;

        private MinItemView(View view) {
            this.view = view;
            this.ivHead = ViewUtils.findById(view, R.id.iv_head);
            this.ivMedia = ViewUtils.findById(view, R.id.iv_media);
            this.ivTitle = ViewUtils.findById(view, R.id.iv_title);
            this.tvName = ViewUtils.findById(view, R.id.tv_name);
            this.tvAgeKM = ViewUtils.findById(view, R.id.tv_age_km);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClick(view, u);
            }
        }

        private void update(UserInfoLightweight u) {
            this.u = u;
            if (u == null) {
                this.view.setVisibility(View.INVISIBLE);
                return;
            }
            this.view.setOnClickListener(this);
            ImageLoader.loadRoundAvatar(ivHead.getContext(), ImageLoader.checkOssImageUrl(u.getAvatar(), 256), ivHead);
            if (u.getVtype() == 1) {
                ivMedia.setImageResource(R.drawable.ic_video_primary);
            } else {
                ivMedia.setImageResource(R.drawable.ic_voice_primary);
            }
            if(u.getNobility_rank() > 0) {//爵位图标
                ImageLoader.loadFitCenter(ivTitle.getContext(), ModuleMgr.getCommonMgr().getCommonConfig().queryNobility(u.getNobility_rank(), u.getGender()).getTitle_icon(), ivTitle);
            }
            tvName.setText(u.getNickname());
            tvAgeKM.setText(u.getAge() + "岁 • " + u.getDistance());
            this.view.setVisibility(View.VISIBLE);
        }

    }


    public static class Item {
        public int size;
        public UserInfoLightweight one;
        public UserInfoLightweight two;
        public UserInfoLightweight three;
    }
}
