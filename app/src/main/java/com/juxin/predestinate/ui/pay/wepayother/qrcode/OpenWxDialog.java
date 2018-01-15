package com.juxin.predestinate.ui.pay.wepayother.qrcode;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;

import com.juxin.predestinate.R;


public class OpenWxDialog extends Dialog {
    private Context context;
    private String URI;


    public OpenWxDialog(Context context, String URI) {
        super(context, R.style.dialog);
        this.context = context;
        this.URI = URI;
        initView(context);
    }

    private void startWexin() {
        Intent intent = new Intent();
        ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");// 报名该有activity
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(cmp);
        context.startActivity(intent);
    }

    private void toWexinScan() {
        try {
            Uri uri = Uri.parse(URI);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.p1_dlg_pay_wx_open, null);
        setContentView(view);

        findViewById(R.id.pay_wx_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWexin();
                dismiss();
            }
        });
    }


}
