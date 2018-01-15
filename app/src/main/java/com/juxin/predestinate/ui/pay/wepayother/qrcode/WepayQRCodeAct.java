package com.juxin.predestinate.ui.pay.wepayother.qrcode;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.library.dir.DirType;
import com.juxin.predestinate.module.util.UIShow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 微信二维码支付
 * IQQ
 */

public class WepayQRCodeAct extends BaseActivity implements OnClickListener {
    private Context context;
    private ImageView iv_Qrcode;
    private String picpath;
    private int time, money;
    private Bitmap bmp;
    private View viewQR;
    private TextView tv_money, tv_time;
    private TimerTask task;
    private Timer timer = new Timer();
    private String URI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_wx_qrcode);
        context = this;
        setPagerTitle();
        initView();
        initData();
    }

    private void setPagerTitle() {
        setTitle("微信支付");
    }

    private void initData() {
    }

    private void initView() {
        findViewById(R.id.pay_wx_save).setOnClickListener(this);
        iv_Qrcode = (ImageView) findViewById(R.id.pay_wx_qrcode);
        picpath = getIntent().getStringExtra("qrurl");
        time = getIntent().getIntExtra("time", 600);
        money = getIntent().getIntExtra("money", 0);
        URI = getIntent().getStringExtra("uri");
        tv_money = (TextView) findViewById(R.id.pay_wx_tv_money);
        tv_money.setText(money + "");
        tv_time = (TextView) findViewById(R.id.pay_wx_tv_time);
        startTime();
        viewQR = findViewById(R.id.pay_wx_qrcode_main);
        ImageLoader.loadFitCenter(context, picpath, iv_Qrcode);
    }

    private void startTime() {
        task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time--;
                        changeTime(time);
                    }
                });
            }
        };
        if (time > -1)
            timer.schedule(task, 1000, 1000);
    }

    public void changeTime(int second) {
        int h = 0;
        int d = 0;
        int s = 0;
        int temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
            tv_time.setText(h + ":" + d + ":" + s);
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
            tv_time.setText(d + ":" + s);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //通知刷个人资料  在
        MsgMgr.getInstance().sendMsg(MsgType.MT_Update_MyInfo, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_wx_save:
                saveToPhoto();
                UIShow.showWxpayOpenWx(this, URI);
                break;
        }
    }

    public void saveViewToGallery(Context context, View view) {
        // 首先保存图片
        String path = DirType.getImageDir();
        String fileName = "0qr_" + System.currentTimeMillis() + ".jpg";
        File file = new File(path, fileName);
        try {
            Bitmap bmp = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            view.draw(canvas);
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
    }


    private void saveViewToGallery(View view) {
        bmp = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        view.draw(canvas);
        String path = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + "Camera";
        // 首先保存图片
        Log.i("saveImage", "" + path);
        long now = System.currentTimeMillis();
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").
                format(new Date(now)) + ".jpg";
        File file = new File(path, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Log.i("saveImage", "保存成功" + file.getAbsolutePath());
            Intent intent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("saveImage", "保存失败");
        } finally {
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveToPhoto() {
        try {
            saveViewToGallery(viewQR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (task != null)
            task.cancel();
        timer.cancel();
        super.onDestroy();
    }

}
