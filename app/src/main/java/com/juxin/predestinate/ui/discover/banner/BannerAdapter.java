package com.juxin.predestinate.ui.discover.banner;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.discover.BannerConfig;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.local.statistics.StatisticsDiscovery;
import com.juxin.predestinate.module.util.UIShow;

import java.util.List;

public class BannerAdapter extends PagerAdapter {

    private Context context;
    private List<BannerConfig.Banner> mList;

    public BannerAdapter(Context context, List<BannerConfig.Banner> list) {
        this.context = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_banner);
        if (mList != null && !mList.isEmpty()) {
            ImageLoader.loadFitCenter(context, mList.get(position % mList.size()).getPic_url(), imageView);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StatisticsDiscovery.onBannerClick(mList.get(position % mList.size()).getH5_url());
                    UIShow.showWebActivity(context, mList.get(position % mList.size()).getH5_url());
                }
            });
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
