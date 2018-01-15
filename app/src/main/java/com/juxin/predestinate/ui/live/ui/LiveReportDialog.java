package com.juxin.predestinate.ui.live.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;

import java.util.HashMap;

/**
 * 举报
 * <p>
 * Created by chengxiaobo on 2017/9/12.
 */

public class LiveReportDialog extends BaseDialogFragment {


    private String[] mArrString = {"反动政治", "骚扰(垃圾信息、广告)", "欺诈信息", "色情、暴力信息", "其他"};
    private String mFromUid = "";
    private String mToUid = "";

    private OnItemClickListener onItemClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFromUid = getArguments().getString("fromUid");
        mToUid = getArguments().getString("toUid");

        setDialogWidthSizeRatio(0.95);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.live_report_dialog);
        setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        initView();
        return getContentView();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void initView() {

        LinearLayout llReport = (LinearLayout) findViewById(R.id.ll_report);

        TextView tv = getItemView(llReport);
        tv.setText("请选择举报类型");
        tv.setTextColor(Color.parseColor("#bebebe"));
        llReport.addView(tv);

        llReport.addView(getViewLine(2));

        for (int i = 0; i < mArrString.length; i++) {

            TextView tvItem = getItemView(llReport);
            tvItem.setText(mArrString[i]);
            tvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    doUpload(((TextView) v).getText().toString());
                }
            });

            llReport.addView(tvItem);
            llReport.addView(getViewLine());

        }
        llReport.removeViewAt(llReport.getChildCount() - 1); //去掉最后一条线

        TextView tvCancel = (TextView) findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LiveReportDialog.this.dismiss();
            }
        });

    }

    /**
     * 上传举报信息
     */
    private void doUpload(String type) {

        /**
         * {
         "uid":1002,         //举报的人
         "buid":1001,        //被举报的人
         "rtype":"dfd",      //举报的类型
         "detail":"dfdf"     //举报详情
         }
         */
        LoadingDialog.show(getActivity());
        final HashMap<String, Object> postParams = new HashMap<>();
        postParams.put("platform", "2");//android为2
        postParams.put("uid", mFromUid);//举报的人
        postParams.put("buid", mToUid);//被举报的人
        postParams.put("rtype", type);//举报的类型
        postParams.put("detail", "");//举报详情

        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.report, postParams, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                LoadingDialog.closeLoadingDialog();
                if (response.isOk()) {
                    PToast.showShort("举报成功");
                    LiveReportDialog.this.dismiss();

                    if(onItemClickListener!=null){
                        onItemClickListener.onReportSuc();
                    }

                    return;
                }
                PToast.showShort(response.getMsg());
            }
        });
    }

    /**
     * 获取ItemView
     *
     * @param parentView
     * @return
     */
    private TextView getItemView(ViewGroup parentView) {

        TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.live_report_item, parentView, false);
        return tv;
    }

    /**
     * 获取线View
     *
     * @param height px
     * @return
     */
    private View getViewLine(int height) {

        View view = new View(getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        view.setBackgroundColor(Color.parseColor("#EAEAEA"));

        return view;

    }

    /**
     * 获取线View
     *
     * @return
     */
    private View getViewLine() {

        return getViewLine(1);
    }

    public interface OnItemClickListener{
        void onReportSuc();
    }
}
