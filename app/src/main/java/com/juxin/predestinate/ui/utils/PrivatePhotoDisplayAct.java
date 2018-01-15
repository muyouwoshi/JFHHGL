package com.juxin.predestinate.ui.utils;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.my.adapter.ViewPagerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * IQQ
 */
public class PrivatePhotoDisplayAct extends BaseActivity {

    private String desc;
    private long qun_id, tuid;
    private List<String> imgList;

    private TextView tvPage, tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isCanBack(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p1_private_photo_display_act);
        initDatas();
        initView();
    }

    private void initDatas() {
        qun_id = getIntent().getLongExtra("qun_id", -1);
        tuid = getIntent().getLongExtra("tuid", -1);
        desc = getIntent().getStringExtra("desc");
        imgList = getIntent().getStringArrayListExtra("imgList");
        if(imgList == null) {
            PToast.showShort("请求出错！");
            this.finish();
        }
    }

    private void initView() {
        setBackView(getResources().getString(R.string.check_info_secret_photo));
        setTitleRightImgText(R.drawable.f1_private_photo_evaluate, getResources().getString(R.string.photo_feel_pingjia), RightClickListener);

        ViewPager viewPager = (ViewPager) findViewById(R.id.vp_photo_display);
        tvPage = (TextView) findViewById(R.id.tv_private_photo_page);
        tvInfo = (TextView) findViewById(R.id.tv_private_photo_info);

        ViewPagerViewAdapter vpAdapter = new ViewPagerViewAdapter(this, imgList);
        viewPager.setAdapter(vpAdapter);
        viewPager.addOnPageChangeListener(OnChangeListener);
        viewPager.setCurrentItem(0);
        bottomShow(0);
    }

    private View.OnClickListener RightClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            UIShow.showPhotoFeelDlg(PrivatePhotoDisplayAct.this, qun_id, tuid);
        }
    };

    private ViewPager.OnPageChangeListener OnChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            bottomShow(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void bottomShow(int position) {
        if (!TextUtils.isEmpty(desc)) {
            tvInfo.setVisibility(View.VISIBLE);
            tvInfo.setText(desc);
        } else {
            tvInfo.setVisibility(View.GONE);
        }
        tvPage.setText((position + 1) + " / " + imgList.size());
    }
}