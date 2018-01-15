package com.juxin.predestinate.module.local.msgview.chatview.input;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;
import java.util.List;

/**
 * 聊天面板多媒体选择扩展panel
 * Created by Kind on 2017/3/31.
 */
public class CommonGridBtnPanel extends BasePanel {

    public CommonGridBtnPanel(Context context, List<BTN_KEY> list) {
        super(context);
        setContentView(R.layout.p1_grid_btn_panel);
        initView(list);
    }

    private void initView(List<BTN_KEY> list) {
        GridView gridView = (GridView) findViewById(R.id.gridview);
        BtnAdapter adapter = new BtnAdapter(getContext(), list);
        gridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public static class BtnAdapter extends ExBaseAdapter<BTN_KEY> {

        private BtnClickListener btnClickListener = null;

        public BtnAdapter(Context context, List<BTN_KEY> datas) {
            super(context, datas);
        }

        public void setBtnClickListener(BtnClickListener btnClickListener) {
            this.btnClickListener = btnClickListener;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflate(R.layout.p1_grid_btn_item);
                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.txt = (TextView) convertView.findViewById(R.id.txt);
                holder.btn = (LinearLayout) convertView.findViewById(R.id.btn);
                holder.price = (TextView) convertView.findViewById(R.id.price);
                holder.priceTrue = (TextView) convertView.findViewById(R.id.price_true);
                holder.tvDiscount = (TextView) convertView.findViewById(R.id.txt_discount);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final BTN_KEY item = getItem(position);
            holder.txt.setText(item.getTitle());

            // 价格配置
            if (item.getPrice() > 0) {
                holder.price.setVisibility(View.VISIBLE);
                holder.price.setText(getContext().getString(R.string.user_other_set_chat_price, item.getPrice()));

                if (item.getPrice() != item.getPriceTrue() && item.getPriceTrue() != 0){
                    holder.priceTrue.setVisibility(View.VISIBLE);
                    holder.tvDiscount.setVisibility(View.VISIBLE);
                    holder.priceTrue.setText(getContext().getString(R.string.user_other_set_chat_price, item.getPriceTrue()));
                    holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);//设置中划线
                }else {
                    holder.tvDiscount.setVisibility(View.GONE);
                    holder.priceTrue.setVisibility(View.GONE);
                    holder.price.setPaintFlags(holder.price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }

            // 点击监听
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btnClickListener != null) {
                        btnClickListener.onClick(item);
                    }
                }
            });

            // 点击配置
            holder.icon.setImageResource(item.getIcon());
            if (!item.isEnable()) {
                holder.icon.setEnabled(!item.isEnable());
                holder.price.setTextColor(getResources().getColor(R.color.text_ciyao_gray));
            }
            return convertView;
        }

        class ViewHolder {
            ImageView icon;
            TextView txt;
            LinearLayout btn;
            TextView price;
            TextView priceTrue;
            TextView tvDiscount;
        }
    }

    public enum BTN_KEY {
        IMG("图片", R.drawable.chat_input_grid_img_selector),           //发送图片按钮
        VIDEO("邀请视频", R.drawable.chat_input_grid_video_selector),    //视频聊天
        VOICE("邀请语音", R.drawable.chat_input_grid_voice_selector),    //语音聊天
        ;

        String title;
        int icon;
        int price;              // 音视频价格
        boolean enable = true;  // 是否可点击
        int priceTrue;        // 打折后的价格

        BTN_KEY(String title, int icon) {
            this.title = title;
            this.icon = icon;
        }

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }

        public int getPriceTrue() {
            return priceTrue;
        }

        public void setPriceTrue(int priceTrue) {
            this.priceTrue = priceTrue;
        }
    }

    public interface BtnClickListener {
        void onClick(BTN_KEY key);
    }

}
