package com.juxin.predestinate.ui.user.my;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.StatisticsMessage;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;

/**
 * IQQ
 */
public class PhotoFeelDlg extends BaseDialogFragment implements View.OnClickListener {

    private long qun_id, tuid;
    private Boolean Like = true;

    private Context context;
    private ImageView ivLike, ivUnLike;

    public PhotoFeelDlg() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(1, 0);
        setCancelable(true);
    }

    public void setData(Context context, long qun_id, long tuid) {
        this.context = context;
        this.qun_id = qun_id;
        this.tuid = tuid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_dlg_photo_feel);
        View view = getContentView();
        initView(view);
        return view;
    }

    private void initView(View view) {
        ivLike = (ImageView) view.findViewById(R.id.iv_photo_feel_like);
        ivUnLike = (ImageView) view.findViewById(R.id.iv_photo_feel_unlike);
        TextView tvSure = (TextView) view.findViewById(R.id.btn_photo_feel_ok);

        ivLike.setOnClickListener(this);
        ivUnLike.setOnClickListener(this);
        tvSure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_photo_feel_like:

                ivLike.setBackgroundResource(R.drawable.f1_photo_feel_like_select);
                ivUnLike.setBackgroundResource(R.drawable.f1_photo_feel_unlike_unselect);
                Like = true;
                break;

            case R.id.iv_photo_feel_unlike:

                ivLike.setBackgroundResource(R.drawable.f1_photo_feel_like_unselect);
                ivUnLike.setBackgroundResource(R.drawable.f1_photo_feel_unlike_select);
                Like = false;
                break;

            case R.id.btn_photo_feel_ok:
                StatisticsMessage.chatPrivateMood(tuid, qun_id, Like);
                ModuleMgr.getCommonMgr().reqSerectComment(qun_id, tuid, Like, new RequestComplete() {
                    @Override
                    public void onRequestComplete(HttpResponse response) {
                        PToast.showShort(response.isOk() ? "评价成功" : response.getMsg());
                        dismissAllowingStateLoss();
                    }
                });
                break;

            default:
                break;
        }
    }
}
