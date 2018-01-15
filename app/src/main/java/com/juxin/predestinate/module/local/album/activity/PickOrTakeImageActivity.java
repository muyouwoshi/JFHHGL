package com.juxin.predestinate.module.local.album.activity;

import android.animation.ValueAnimator;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.album.adapter.AlbumDirectoryAdapter;
import com.juxin.predestinate.module.local.album.adapter.AlbumExhibitionAdapter;
import com.juxin.predestinate.module.local.album.bean.AlbumDirectories;
import com.juxin.predestinate.module.local.album.bean.SingleImageModel;
import com.juxin.predestinate.module.local.album.help.AlbumAnime;
import com.juxin.predestinate.module.local.album.help.AlbumBitmapCacheHelper;
import com.juxin.predestinate.module.local.album.help.AlbumHelper;
import com.juxin.predestinate.module.local.album.help.ImgConstant;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.util.UIUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 仿微信拍照/选取图片
 * <p>
 * Created by Su on 2016/12/6.
 */
public class PickOrTakeImageActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener, AlbumHelper.OnAlbumCompleteListener {

    /**
     * View
     */
    private GridView gridView = null;
    private View v_line;                                // 底部预览按钮左侧分割线
    private TextView tv_choose_image_directory;         // 底部右侧展示当前相册名称
    private TextView tv_preview;                        // 底部左侧预览
    private RelativeLayout rl_date;                     // 顶部展示时间布局
    private TextView tv_date;                           // 顶部左侧时间
    private TextView title_right_txt;                   //右边
    private RelativeLayout rl_choose_directory;          // 选择文件夹的弹出框
    private ListView listView;                           // 目录列表
    private AlbumDirectoryAdapter albumDirectoryAdapter; // 相册目录列表Adapter
    private AlbumExhibitionAdapter albumExhibitionAdapter;// 图片展示Adapter

    /**
     * Data
     */
    private ArrayList<String> picklist;              // 选中图片的信息
    private List<SingleImageModel> allImages;        // 按时间排序的所有图片list
    private List<AlbumDirectories> imageDirectories; // 按目录排序的所有图片list

    /**
     * Animation
     */
    private AlbumAnime albumAnime;

    /**
     * Global variable
     */
    private int currentState = SCROLL_STATE_IDLE;
    private long lastPicTime = 0;           // 最新一张图片的展示时间
    private int currentShowPosition = -1;   // 当前显示的相册目录，默认显示 "全部图片"
    private int picNums = 4;                // 选择图片的数量总数，默认为4
    private int currentPicNums = 0;         // 当前选中的图片数量
    private int CROP_TYPE = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setCanNotify(false);//设置该页面不弹出悬浮窗消息通知
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p1_album_select_act);
        setBackView("选择图片");
        Intent intent = getIntent();
        if (intent.hasExtra(ImgConstant.CROP_TYPE)) {
            CROP_TYPE = intent.getIntExtra(ImgConstant.CROP_TYPE, 0);
        }
        initView();
        initData();
        initAnim();
    }

    private void initView() {
        title_right_txt = (TextView) this.findViewById(R.id.base_title_right_txt);
        title_right_txt.setVisibility(View.VISIBLE);
        title_right_txt.setOnClickListener(this);

        gridView = (GridView) findViewById(R.id.gv_content);
        albumExhibitionAdapter = new AlbumExhibitionAdapter(this, null);
        gridView.setAdapter(albumExhibitionAdapter);
        gridView.setOnScrollListener(this);

        //************************ footer Start ****************************************************
        // 防止点击透传
        RelativeLayout rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
        rl_bottom.setOnClickListener(null);
        // 左侧显示选中的相册文件夹名称
        tv_choose_image_directory = (TextView) findViewById(R.id.tv_choose_image_directory);
        // footer右侧显示的预览按钮
        tv_preview = (TextView) findViewById(R.id.tv_preview);
        v_line = findViewById(R.id.v_line);
        // 弹出选择不同相册文件夹
        rl_choose_directory = (RelativeLayout) findViewById(R.id.rl_choose_directory);
        listView = (ListView) findViewById(R.id.lv_directories);
        albumDirectoryAdapter = new AlbumDirectoryAdapter(this, listView, null);
        listView.setAdapter(albumDirectoryAdapter);
        listView.setOnItemClickListener(this);
        //************************ footer End ******************************************************

        //************************ Header Start ****************************************************
        // 滑动相册的时候，头部显示的当前相册创建的时间布局
        rl_date = (RelativeLayout) findViewById(R.id.rl_date);
        tv_date = (TextView) findViewById(R.id.tv_date);
        //************************ Header End ******************************************************

        rl_choose_directory.setOnClickListener(this);
        tv_choose_image_directory.setOnClickListener(this);
        tv_preview.setOnClickListener(this);
    }

    // 初始化动画
    private void initAnim() {
        albumAnime = new AlbumAnime();
        albumAnime.initAnim(this, listView, new AlbumAnime.OnAlbumAnimeListener() {
            @Override
            public void onDirectoryShow(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) listView.getLayoutParams();
                rl_choose_directory.setAlpha(1 - Math.abs(value / UIUtil.dip2px(PickOrTakeImageActivity.this, 400)));
                params.bottomMargin = value;
                listView.setLayoutParams(params);
                listView.invalidate();
                rl_choose_directory.invalidate();
            }

            @Override
            public void onDirectoryClose(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) listView.getLayoutParams();
                params.bottomMargin = value;
                listView.setLayoutParams(params);
                rl_choose_directory.setAlpha(1 - Math.abs(value / UIUtil.dip2px(PickOrTakeImageActivity.this, 400)));
                if (value <= -UIUtil.dip2px(PickOrTakeImageActivity.this, 300)) {
                    rl_choose_directory.setVisibility(View.GONE);
                }
                listView.invalidate();
                rl_choose_directory.invalidate();
            }

            @Override
            public void onHeaderDateShow(Animation animation) {
                rl_date.setVisibility(View.GONE);
            }
        });
    }

    private void initData() {
        picklist = new ArrayList<>();
        picNums = getIntent().getIntExtra(ImgConstant.STR_NUMS, 4);
        AlbumHelper.getInstance().initAlbum(this);
        tv_choose_image_directory.setText(getString(R.string.album_all));
        tv_preview.setText(getString(R.string.preview_without_num));

        if (picNums == 1) {
            tv_preview.setVisibility(View.GONE);
            v_line.setVisibility(View.GONE);
            title_right_txt.setVisibility(View.GONE);
        } else {
            title_right_txt.setText("完成");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 选择相册目录
            case R.id.rl_choose_directory:
            case R.id.tv_choose_image_directory:
                if (allImages == null) return;
                if (allImages.size() <= 0) {
                    PToast.showShort("相册没有图片");
                    return;
                }
                if (Build.VERSION.SDK_INT < 11) {
                    if (rl_choose_directory.getVisibility() == View.VISIBLE) {
                        rl_choose_directory.setVisibility(View.GONE);
                    } else {
                        rl_choose_directory.setVisibility(View.VISIBLE);
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) listView.getLayoutParams();
                        params.bottomMargin = 0;
                        listView.setLayoutParams(params);
                        ((ViewGroup) (listView.getParent())).invalidate();
                    }
                } else {
                    if (rl_choose_directory.getVisibility() == View.VISIBLE) {
                        albumAnime.startTurnOff();
                    } else {
                        rl_choose_directory.setVisibility(View.VISIBLE);
                        albumAnime.startTurnOn();
                    }
                }
                break;

            // 预览，点击跳转到大图界面
            case R.id.tv_preview:
                if (currentPicNums > 0) {
                    ArrayList<SingleImageModel> data = AlbumHelper.getInstance().getChoosePicFromList(picklist);
                    AlbumHelper.getInstance().skipToPreview(this, data, picklist, picNums, 0);
                    AlbumBitmapCacheHelper.getInstance(this).releaseHalfSizeCache();
                }
                break;

            // 完成
            case R.id.base_title_right_txt:
                returnDataAndClose();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        AlbumBitmapCacheHelper.getInstance(this).removeAllThreads(); // 移除当前加载线程

        // 选中不同的相册文件夹
        if (!(currentShowPosition == i - 1)) {
            currentShowPosition = i - 1;
            reloadChooseDirectory();
        }

        // 关闭相册列表
        if (Build.VERSION.SDK_INT < 11) {
            rl_choose_directory.setVisibility(View.GONE);
        } else {
            albumAnime.startTurnOff();
        }
    }

    @Override
    public void onBackPressed() {
        if (rl_choose_directory.getVisibility() == View.VISIBLE) {
            if (Build.VERSION.SDK_INT < 11) {
                rl_choose_directory.setVisibility(View.GONE);
            } else {
                albumAnime.startTurnOff();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        currentState = scrollState;

        if (currentState != SCROLL_STATE_FLING) {  // 未处于惯性滑动过程
            albumAnime.startAlpha(rl_date);
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        //保证当选择全部文件夹的时候，显示的时间为第一个图片，排除第一个拍照按钮
        if (currentShowPosition == -1 && firstVisibleItem > 0)
            firstVisibleItem--;

        long time = AlbumHelper.getInstance().getImgDate(firstVisibleItem, currentShowPosition);
        if (lastPicTime != time) {
            lastPicTime = time;
        }
        if (currentState != SCROLL_STATE_IDLE) { // 保证实时更新图片展示时间
            showTimeLine(lastPicTime);
        }
    }

    // 相册初始化完成
    @Override
    public void onInitAlbumfinish() {
        imageDirectories = AlbumHelper.getInstance().getImageDirectories();
        allImages = AlbumHelper.getInstance().getAllImages();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                albumDirectoryAdapter.setList(imageDirectories);
                albumDirectoryAdapter.setData(currentShowPosition);
                albumExhibitionAdapter.setList(allImages);
                albumExhibitionAdapter.setData(getCount(), currentShowPosition);
                albumExhibitionAdapter.setConstant(picNums, gridView);
            }
        });
    }

    /**
     * 刷新图片展示
     */
    public void refresh(View view, AlbumExhibitionAdapter.GridViewHolder holder) {
        int position = holder.position;
        switch (view.getId()) {
            // 点击相册图片，若是多相片选择，则跳转到大图预览界面。否则直接将数据传递到下个页面
            case R.id.iv_content:
                if (picNums > 1) {
                    ArrayList<SingleImageModel> imageModels = AlbumHelper.getInstance().getAllImagesFromCurrentDirectory(currentShowPosition);
                    AlbumHelper.getInstance().skipToPreview(this, imageModels, picklist, picNums, position);
                    AlbumBitmapCacheHelper.getInstance(this).releaseHalfSizeCache();
                    return;
                }
                String path = AlbumHelper.getInstance().getImgPath(position, currentShowPosition);
                if (AlbumHelper.getInstance().isCrop()) {
                    AlbumHelper.getInstance().skipToCrop2(this, path, CROP_TYPE);
                    return;
                }
                AlbumHelper.getInstance().skipToFinish(this, path);
                break;

            // 多选图片右上角选中按钮
            case R.id.iv_pick_or_not:
                AlbumHelper.getInstance().toggleSelectState(position, currentShowPosition);
                boolean isPicked = AlbumHelper.getInstance().getSelectState(position, currentShowPosition);
                if (isPicked) {  // 选中
                    if (currentPicNums == picNums) {  // 超出多选个数
                        AlbumHelper.getInstance().toggleSelectState(position, currentShowPosition);
                        Toast.makeText(this, String.format(getString(R.string.choose_pic_num_out_of_index), picNums), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String pathUrl = AlbumHelper.getInstance().getImgPath(position, currentShowPosition);
                    picklist.add(pathUrl);
                    holder.iv_pick_or_not.setImageResource(R.drawable.p1_album_chosed);
                    holder.v_gray_masking.setVisibility(View.VISIBLE);
                    currentPicNums++;
                    tv_preview.setText(String.format(getString(R.string.preview_with_num), currentPicNums));
                    title_right_txt.setText(String.format(getString(R.string.choose_pic_finish_with_num), currentPicNums, picNums));
                    return;
                }
                String pathUrl = AlbumHelper.getInstance().getImgPath(position, currentShowPosition);
                picklist.remove(pathUrl);
                holder.iv_pick_or_not.setImageResource(R.drawable.p1_album_chose);
                holder.v_gray_masking.setVisibility(View.GONE);
                currentPicNums--;
                if (currentPicNums == 0) {
                    title_right_txt.setText(getString(R.string.choose_pic_finish));
                    tv_preview.setText(getString(R.string.preview_without_num));
                } else {
                    tv_preview.setText(String.format(getString(R.string.preview_with_num), currentPicNums));
                    title_right_txt.setText(String.format(getString(R.string.choose_pic_finish_with_num), currentPicNums, picNums));
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            // 大图预览
            case ImgConstant.CODE_FOR_BIG_PREVIEW:
                AlbumBitmapCacheHelper.getInstance(this).resizeCache();
                if (data != null) {
                    ArrayList<String> temp = (ArrayList<String>) data.getSerializableExtra("pick_data");
                    boolean isFinish = data.getBooleanExtra("isFinish", false);
                    toggleSelectState(temp);
                    if (isFinish) {
                        returnDataAndClose();
                    }
                }
                break;

            // 调用系统相机拍照返回结果
            case ImgConstant.CODE_FOR_TAKE_PIC:
                if (AlbumHelper.getInstance().getPhotoUri() != null) {
                    handleTakePhotoResult();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (albumAnime != null) {
            albumAnime.release();
            albumAnime = null;
            picklist = null;
        }
        AlbumBitmapCacheHelper.getInstance(this).clearCache();
    }

    // ---------------------------------内部调用工具方法-------------------------------------------------

    /**
     * 展示顶部的时间
     */
    private void showTimeLine(long date) {
        albumAnime.cancelAlpha();
        rl_date.setVisibility(View.VISIBLE);
        tv_date.setText(AlbumHelper.getInstance().calculateShowTime(date * 1000));
    }

    /**
     * 获取当前展示页面图片数量
     */
    private int getCount() {
        if (currentShowPosition == -1) {  // 全部图片 size+拍摄照片按钮
            return allImages.size() + 1;
        }
        return imageDirectories.get(currentShowPosition).images.getImageCounts();
    }

    /**
     * 转换图片选择状态
     */
    private void toggleSelectState(ArrayList<String> temp) {
        // 在大图界面设置选中状态，更新布局
        for (String path : temp) {
            if (!picklist.contains(path)) {
                View v = gridView.findViewWithTag(path);
                if (v != null) {
                    ((ViewGroup) (v.getParent())).findViewById(R.id.v_gray_masking).setVisibility(View.VISIBLE);
                    ((ImageView) ((ViewGroup) (v.getParent())).findViewById(R.id.iv_pick_or_not)).setImageResource(R.drawable.p1_album_chosed);
                }
                AlbumHelper.getInstance().setPickStateFromHashMap(path, true);
                currentPicNums++;
            }
        }
        // 大图界面取消选中状态, 更新布局
        for (String path : picklist) {
            if (!temp.contains(path)) {
                View v = gridView.findViewWithTag(path);
                if (v != null) {
                    ((ViewGroup) (v.getParent())).findViewById(R.id.v_gray_masking).setVisibility(View.GONE);
                    ((ImageView) ((ViewGroup) (v.getParent())).findViewById(R.id.iv_pick_or_not)).setImageResource(R.drawable.p1_album_chose);
                }
                AlbumHelper.getInstance().setPickStateFromHashMap(path, false);
                currentPicNums--;
            }
        }
        picklist = temp;
        if (currentPicNums == 0) {
            tv_preview.setText(getString(R.string.preview_without_num));
            title_right_txt.setText(getString(R.string.choose_pic_finish));
        } else {
            title_right_txt.setText(String.format(getString(R.string.choose_pic_finish_with_num), currentPicNums, picNums));
            tv_preview.setText(String.format(getString(R.string.preview_with_num), currentPicNums));
        }
    }

    /**
     * 选择相册其他的文件夹后，重新加载当前页面图片数据
     */
    private void reloadChooseDirectory() {
        if (currentShowPosition == -1) {
            tv_choose_image_directory.setText(getString(R.string.album_all));
        } else {
            tv_choose_image_directory.setText(new File(imageDirectories.get(currentShowPosition).directoryPath).getName());
        }

        // 更新Adapter中当前显示相册目录位置
        albumDirectoryAdapter.setData(currentShowPosition);
        albumExhibitionAdapter.setData(getCount(), currentShowPosition);
        albumExhibitionAdapter.notifyDataSetChanged();
        gridView.smoothScrollToPosition(0);
        View v = listView.findViewWithTag("picked");
        if (v != null) {
            v.setVisibility(View.GONE);
            v.setTag(null);
        }
        v = (View) listView.findViewWithTag(currentShowPosition + 1).getParent().getParent();
        if (v != null) {
            v.findViewById(R.id.iv_directory_check).setVisibility(View.VISIBLE);
            v.findViewById(R.id.iv_directory_check).setTag("picked");
        }
    }


    /**
     * 点击完成按钮之后将图片的地址返回到上一个页面
     */
    private void returnDataAndClose() {
        AlbumBitmapCacheHelper.getInstance(this).clearCache();
        if (currentPicNums == 0) {
            Toast.makeText(this, getString(R.string.choose_no_pick), Toast.LENGTH_SHORT).show();
            return;
        }

        // 传递图片地址数据
        Intent data = new Intent();
        data.putExtra("data", picklist);
        setResult(RESULT_OK, data);
        finish();
    }


    // ************************************** 系统相机 **********************************************

    /**
     * 处理相机拍照获取的图片
     */
    private void handleTakePhotoResult() {
        ContentResolver cr = getContentResolver();
        // 按 刚刚指定 的那个文件名，查询数据库，获得更多的 照片信息，比如 图片的物理绝对路径
        Cursor c = cr.query(AlbumHelper.getInstance().getPhotoUri(), null, null, null, null);
        if (c != null) {
            if (c.moveToNext()) {
                String path = c.getString(1);
                picklist.clear();
                picklist.add(path);
                currentPicNums = 1;
                returnDataAndClose();
            } else {
                PToast.showShort("获取拍照图片失败");
            }
            c.close();
        } else {
            String path = AlbumHelper.getInstance().getPhotoUri().getPath();
            if (!TextUtils.isEmpty(path)) {
                picklist.clear();
                picklist.add(path);
                currentPicNums = 1;

                Intent data = new Intent();
                data.putExtra("data", picklist);
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}
