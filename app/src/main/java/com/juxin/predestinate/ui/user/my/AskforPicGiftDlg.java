package com.juxin.predestinate.ui.user.my;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PToast;
import com.juxin.library.utils.BitmapUtil;
import com.juxin.library.utils.FileUtil;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.module.local.album.ImgSelectUtil;
import com.juxin.predestinate.module.local.statistics.StatisticsUser;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.baseui.custom.HorizontalListView;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.library.dir.DirType;
import com.juxin.predestinate.module.logic.media.MediaMgr;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.my.GiftHelper;
import com.juxin.predestinate.ui.user.util.HorizGiftPicAdapter;
import com.juxin.predestinate.ui.utils.NumLimitTextWatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2017/6/27
 * 描述:索要图片礼物弹框
 * 作者:lc
 */
public class AskforPicGiftDlg extends BaseDialogFragment implements View.OnClickListener, AdapterView.OnItemClickListener, HorizGiftPicAdapter.OptCallBack, GiftHelper.OnRequestGiftListCallback {

    private Context context;
    private EditText et_context;
    private HorizontalListView hsv_listview;
    private ImageView iv_add_pic, iv_gift1, iv_gift2, iv_gift3;
    private LinearLayout ll_gift1, ll_gift2, ll_gift3;
    private TextView tv_context_num, tv_gift_price1, tv_gift_price2, tv_gift_price3, tv_gift_name1, tv_gift_name2, tv_gift_name3, tv_send;

    private HorizGiftPicAdapter adapter;

    private String content;
    private final int picMaxNum = 6;
    private final String ADD_BTN_FLAG = "addBtn";
    private int sum = 40;
    private ArrayList<String> arrList = new ArrayList<>();          //压缩后路径
    private ArrayList<String> orginList = new ArrayList<>();        //原路径
    private List<GiftsList.GiftInfo> arrGifts = new ArrayList();

    // 界面上展示的几个礼物信息，使用SparseArray保证顺序
    private SparseArray<GiftsList.GiftInfo> displayGifts = new SparseArray<>();
    private GiftsList.GiftInfo selectGiftInfo;

    public AskforPicGiftDlg() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(1, 0);
        setCancelable(true);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_askfor_pic_gift_dlg);
        View view = getContentView();
        initView(view);
        initGifts();
        setGiftView();
        return view;
    }

    private void initView(View view) {
        hsv_listview = (HorizontalListView) view.findViewById(R.id.hsv_listview);
        ll_gift1 = (LinearLayout) view.findViewById(R.id.ll_gift1);
        ll_gift2 = (LinearLayout) view.findViewById(R.id.ll_gift2);
        ll_gift3 = (LinearLayout) view.findViewById(R.id.ll_gift3);
        iv_add_pic = (ImageView) view.findViewById(R.id.iv_add_pic);
        iv_gift1 = (ImageView) view.findViewById(R.id.iv_gift1);
        iv_gift2 = (ImageView) view.findViewById(R.id.iv_gift2);
        iv_gift3 = (ImageView) view.findViewById(R.id.iv_gift3);
        et_context = (EditText) view.findViewById(R.id.et_context);
        tv_context_num = (TextView) view.findViewById(R.id.tv_context_num);
        tv_gift_price1 = (TextView) view.findViewById(R.id.tv_gift_price1);
        tv_gift_price2 = (TextView) view.findViewById(R.id.tv_gift_price2);
        tv_gift_price3 = (TextView) view.findViewById(R.id.tv_gift_price3);
        tv_gift_name1 = (TextView) view.findViewById(R.id.tv_gift_name1);
        tv_gift_name2 = (TextView) view.findViewById(R.id.tv_gift_name2);
        tv_gift_name3 = (TextView) view.findViewById(R.id.tv_gift_name3);
        tv_send = (TextView) view.findViewById(R.id.tv_send);

        iv_add_pic.setOnClickListener(this);
        ll_gift1.setOnClickListener(this);
        ll_gift2.setOnClickListener(this);
        ll_gift3.setOnClickListener(this);
        tv_send.setOnClickListener(this);

        ll_gift1.setSelected(true);
        adapter = new HorizGiftPicAdapter(context, arrList, this);
        hsv_listview.setAdapter(adapter);
        hsv_listview.setOnItemClickListener(this);

        et_context.addTextChangedListener(new NumLimitTextWatcher(et_context, tv_context_num, sum));
    }

    /**
     * 初始化礼物列表
     */
    private void initGifts() {
        arrGifts = ModuleMgr.getCommonMgr().getGiftLists().getArrCommonGifts();
        if (arrGifts.size() > 0) return;
        ModuleMgr.getCommonMgr().requestGiftList(this);
    }

    public void setGiftView() {
        try {
            List<Integer> listPhotoIds = ModuleMgr.getCommonMgr().getCommonConfig().getGift().getUnlock_photo_ids();
            for (int i = 0; i < listPhotoIds.size(); i++) {
                GiftsList.GiftInfo info = ModuleMgr.getCommonMgr().getGiftLists().getGiftInfo(listPhotoIds.get(i));
                displayGifts.put(i, info);
                switch (i) {
                    case 0:
                        tv_gift_name1.setText(info.getName());
                        tv_gift_price1.setText(getString(R.string.goods_diamond_need, info.getMoney()));
                        ImageLoader.loadCenterCrop(context, info.getPic(), iv_gift1);
                        break;

                    case 1:
                        tv_gift_name2.setText(info.getName());
                        tv_gift_price2.setText(getString(R.string.goods_diamond_need, info.getMoney()));
                        ImageLoader.loadCenterCrop(context, info.getPic(), iv_gift2);
                        break;

                    case 2:
                        tv_gift_name3.setText(info.getName());
                        tv_gift_price3.setText(getString(R.string.goods_diamond_need, info.getMoney()));
                        ImageLoader.loadCenterCrop(context, info.getPic(), iv_gift3);
                        break;

                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_gift1:
                selectGiftInfo = displayGifts.get(0);
                changeCheck(true, false, false);
                break;

            case R.id.ll_gift2:
                selectGiftInfo = displayGifts.get(1);
                changeCheck(false, true, false);
                break;

            case R.id.ll_gift3:
                selectGiftInfo = displayGifts.get(2);
                changeCheck(false, false, true);
                break;

            case R.id.iv_add_pic:
                selectPic();
                break;

            case R.id.tv_send:
                if (NetworkUtils.isNotConnected(context)) {
                    PToast.showShort(getString(R.string.net_error_check_your_net));
                    return;
                }
                content = et_context.getText().toString().trim();
                if (arrList == null || arrList.size() <= 1) {//有个添加按钮
                    PToast.showShort(getString(R.string.private_pic_upload));
                    return;
                }
                if (selectGiftInfo == null) selectGiftInfo = displayGifts.get(0);
                String[] strArr = orginList.toArray(new String[orginList.size()]);
                uploadPic(strArr);
                break;
            default:
                break;
        }
    }

    private void changeCheck(boolean b1, boolean b2, boolean b3) {
        ll_gift1.setSelected(b1);
        ll_gift2.setSelected(b2);
        ll_gift3.setSelected(b3);
    }

    /**
     * 上传图片
     */
    private void uploadPic(String... path) {
        LoadingDialog.show((FragmentActivity) context, getString(R.string.loading_pushpic));
        ModuleMgr.getMediaMgr().sendHttpMultiFiles(Constant.UPLOAD_TYPE_PHOTO, 0, new MediaMgr.OnMFUFullComplete() {

            @Override
            public void onFullComplete(ArrayList<String> mediaFullUrls) {
                LoadingDialog.closeLoadingDialog();
                FileUtil.deleteFiles(arrList);
                groupSend(mediaFullUrls);
            }

            @Override
            public void onUploadComplete(ArrayList<String> mediaUrls) {

            }
        }, path);
    }

    /**
     * 群发图片索要礼物消息
     */
    public void groupSend(ArrayList<String> mediaUrls) {
        if (selectGiftInfo == null) {
            PToast.showShort(getString(R.string.gift_not_find));
            dismissAllowingStateLoss();
            return;
        }
        StatisticsUser.userPicAskGift(content, selectGiftInfo.getMoney(), selectGiftInfo.getId(), mediaUrls);
        ModuleMgr.getCommonMgr().reqQunPhotos(selectGiftInfo.getId(), content, mediaUrls, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                try {
                    if (response.isOk()) {
                        if (context == null) return;
                        PToast.showShort(context.getString(R.string.send_suceed));
                        return;
                    }

                    PToast.showShort(response.getMsg());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dismissAllowingStateLoss();
    }

    private void selectPic() {
        ImgSelectUtil.getInstance().pickPhotoGallery(context, new ImgSelectUtil.OnChooseCompleteListener() {
            @Override
            public void onComplete(String... path) {
                try {
                    if (path == null || path.length == 0 || TextUtils.isEmpty(path[0])) {
                        PToast.showShort(getString(R.string.private_pic_sel_fail));
                        return;
                    }
                    String sPath = path[0];
                    String tempEnd = sPath.substring(sPath.lastIndexOf("."), sPath.length());
                    if (!tempEnd.equalsIgnoreCase(".jpg") && !tempEnd.equalsIgnoreCase(".png") && !tempEnd.equalsIgnoreCase(".jpeg")) {//其他格式待定
                        PToast.showShort(getString(R.string.private_pic_sel_pic));
                        return;
                    }
                    String scalePath = BitmapUtil.getSmallBitmapAndSave(sPath, DirType.getImageDir());
                    addPic(sPath, scalePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private synchronized void addPic(String orgPath, String scalePath) {
        iv_add_pic.setClickable(false);
        iv_add_pic.setVisibility(View.GONE);
        arrList.add(0, scalePath);
        orginList.add(0, orgPath);
        if (!arrList.contains(ADD_BTN_FLAG)) {// 未满6张图片，增加添加按钮占位
            arrList.add(ADD_BTN_FLAG);
        }
        if (arrList.size() == (picMaxNum + 1)) {// 满6张图片，移出添加按钮占位
            arrList.remove(ADD_BTN_FLAG);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public synchronized void delCallBack(int position) {
        iv_add_pic.setClickable(false);
        iv_add_pic.setVisibility(View.GONE);
        arrList.remove(position);
        orginList.remove(position);
        if (arrList.size() == 1) {
            arrList.clear();
            orginList.clear();
            iv_add_pic.setClickable(true);
            iv_add_pic.setVisibility(View.VISIBLE);
        } else {
            if (!arrList.contains(ADD_BTN_FLAG)) {
                arrList.add(ADD_BTN_FLAG);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestGiftListCallback(boolean isOk) {
        if (isOk) {
            arrGifts = ModuleMgr.getCommonMgr().getGiftLists().getArrCommonGifts();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position != (arrList.size() - 1)) return;
        if (!ADD_BTN_FLAG.equals(arrList.get(position))) return;
        selectPic();
    }
}
