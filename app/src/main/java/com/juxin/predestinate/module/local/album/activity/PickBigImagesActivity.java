package com.juxin.predestinate.module.local.album.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.album.adapter.PreviewAdapter;
import com.juxin.predestinate.module.local.album.bean.SingleImageModel;
import com.juxin.predestinate.module.local.album.help.AlbumBitmapCacheHelper;
import com.juxin.predestinate.module.local.album.help.ImgConstant;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;

import java.util.ArrayList;

/**
 * 相册大图预览
 * <p>
 * Created by Su on 2016/12/6.
 */
public class PickBigImagesActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private ViewPager viewPager;
    private ImageView iv_choose_state;
    private TextView title_right_txt;

    private ArrayList<SingleImageModel> allimages;
    private ArrayList<String> picklist;

    private int currentPic; // 当前选中的图片
    private int last_pics;
    private int total_pics;
    private boolean isFinish = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setCanNotify(false);//设置该页面不弹出悬浮窗消息通知
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p1_album_exbig_act);
        setBackView();

        initView();
        initData();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.vp_content);
        iv_choose_state = (ImageView) findViewById(R.id.iv_choose_state);
        title_right_txt = (TextView) findViewById(R.id.base_title_right_txt);
        iv_choose_state.setOnClickListener(this);
        title_right_txt.setOnClickListener(this);
    }

    private void initData() {
        allimages = (ArrayList<SingleImageModel>) getIntent().getSerializableExtra(ImgConstant.STR_ALBUM_DIRECTORY);
        picklist = (ArrayList<String>) getIntent().getSerializableExtra(ImgConstant.STR_ALL_PICK_DATA);
        if (picklist == null)
            picklist = new ArrayList<>();
        currentPic = getIntent().getIntExtra(ImgConstant.STR_CURRENT_PIC, 0);
        total_pics = getIntent().getIntExtra(ImgConstant.STR_TOTAL_PIC, 4);
        last_pics = total_pics - picklist.size();

        setTitle((currentPic + 1) + "/" + getImagesCount());
        toggleChooseBg(currentPic);
        refreshTxt();
        PreviewAdapter preAdapter = new PreviewAdapter(this, viewPager, allimages);
        viewPager.setAdapter(preAdapter);
        viewPager.setOnPageChangeListener(this);
        viewPager.setCurrentItem(currentPic);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        toggleChooseBg(position);
        currentPic = position;
        setTitle((currentPic + 1) + "/" + getImagesCount());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_choose_state:
                toggleChooseState(currentPic);
                //如果被选中
                if (getChooseStateFromList(currentPic)) {
                    if (last_pics <= 0) {   // 超出多选个数
                        toggleChooseState(currentPic);
                        Toast.makeText(this, String.format(getString(R.string.choose_pic_num_out_of_index), total_pics), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    picklist.add(getPathFromList(currentPic));
                    last_pics--;
                } else {
                    picklist.remove(getPathFromList(currentPic));
                    last_pics++;
                }
                toggleChooseBg(currentPic);
                refreshTxt();
                break;

            case R.id.base_title_right_txt:
                isFinish = true;
                finish();
                break;
        }
    }

    // ---------------------------- 内部调用方法 -------------------------------------------------

    /**
     * 刷新右上角完成按钮
     */
    private void refreshTxt() {
        int selected = total_pics - last_pics <= 0 ? 0 : total_pics - last_pics;
        title_right_txt.setText(String.format(getString(R.string.choose_pic_finish_with_num), selected, total_pics));
    }

    /**
     * 切换图片选中背景
     */
    private void toggleChooseBg(int position) {
        if (getChooseStateFromList(position)) {
            iv_choose_state.setBackgroundResource(R.drawable.p1_album_chosed);
        } else {
            iv_choose_state.setBackgroundResource(R.drawable.p1_album_chose);
        }
    }

    /**
     * 通过位置获取该位置图片的path
     */
    private String getPathFromList(int position) {
        return allimages.get(position).path;
    }

    /**
     * 通过位置获取该位置图片的选中状态
     */
    private boolean getChooseStateFromList(int position) {
        if(position < getImagesCount()) {
            return allimages.get(position).isPicked;
        }

        return  false;
    }

    /**
     * 反转图片的选中状态
     */
    private void toggleChooseState(int position) {
        if(position < getImagesCount()) {
            allimages.get(position).isPicked = !allimages.get(position).isPicked;
        }
    }

    /**
     * 获得所有的图片数量
     */
    private int getImagesCount() {
        return allimages.size();
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra("pick_data", picklist);
        data.putExtra("isFinish", isFinish);
        setResult(RESULT_OK, data);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlbumBitmapCacheHelper.getInstance(this).clearCache();
    }
}
