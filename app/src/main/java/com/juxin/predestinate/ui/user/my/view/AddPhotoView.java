package com.juxin.predestinate.ui.user.my.view;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.album.ImgSelectUtil;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.JsonUtil;

import org.json.JSONObject;


/**
 * Created by zm on 2016/12/8
 */
public class AddPhotoView extends LinearLayout implements View.OnClickListener, ImgSelectUtil.OnChooseCompleteListener{

    private ImageView imgPhoto,imgUp;
    private TextView tvTip;
    private String strPath;

    public AddPhotoView(Context context) {
        this(context, null);
    }

    public AddPhotoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AddPhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    //初始化
    private void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.f1_add_photo_view, this);
        this.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imgPhoto = (ImageView) findViewById(R.id.add_photo_img_photo);
        imgUp = (ImageView) findViewById(R.id.add_photo_img_up);
        tvTip = (TextView) findViewById(R.id.add_photo_tv_tip);
        imgPhoto.setOnClickListener(this);
    }

    /**
     * 获取图片地址
     *
     * @return
     */
    public String getImgStrPath(){
        return strPath;
    }

    /**
     * 设置图片
     */
    public void setImg(String url){
        tvTip.setVisibility(View.INVISIBLE);
        ImageLoader.loadRoundCenterCrop(getContext(), url, imgPhoto);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_photo_img_photo://添加图片
                ImgSelectUtil.getInstance().pickPhotoGallery(getContext(), this);
                break;
        }
    }

    @Override
    public void onComplete(String... path) {
        if (path == null || path.length == 0 || TextUtils.isEmpty(path[0])) {
            return;
        }
//        ImageLoader.loadRoundCorners(getContext(), path[0], 8, imgPhoto);

        //上传图片
        LoadingDialog.show((FragmentActivity) getContext(), "正在上传图片");
        ModuleMgr.getCommonMgr().uploadIdCard(path[0], new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
//                Log.e("TTTTTTTTTTTTTGGG", response.getResponseString() + "|||");
                LoadingDialog.closeLoadingDialog();
                if (response.isOk()) {
                    //                    MsgMgr.getInstance().sendMsg(MsgType.MT_Update_MyInfo, null);
                    tvTip.setVisibility(View.INVISIBLE);
                    AddPhotoView.this.setBackgroundResource(R.color.transparent);
                    JSONObject jsonObject = JsonUtil.getJsonObject(response.getResponseString());
                    ImageLoader.loadRoundCenterCrop(getContext(), jsonObject.optString("file_path"), imgPhoto);
                    strPath = jsonObject.optString("big_path");
                }else {
                    PToast.showShort(response.getMsg()+"");
                }
            }
        });
    }
}
