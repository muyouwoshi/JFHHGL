package com.juxin.predestinate.ui.user.auth;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.StackNode;

import java.io.File;
import java.io.IOException;

/**
 * 录制视频界面
 *
 * @author guowz
 */

public class RecordVideoAct extends FragmentActivity {
    private static final String TAG = RecordVideoAct.class.getSimpleName();
    public static final String EXTRA_RECORD_FILE_PATH = "com.xiaochen.android.fate_it.ui.ex.path";
    // 系统的视频文件
    private File videoFile;
    private MediaRecorder mediaRecorder;
    // 显示视频预览的SurfaceView
    private SurfaceView surfaceView;
    //摄像头
    private Camera camera;
    // 记录是否正在进行录制
    private boolean isRecording = false;
    private ImageView ivRecord;
    private TextView tvHintRecord;
    private int timeCount = 5;
    private int videoWidth = 1280,videoHeight = 720;
    private int startFailedCnt = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉标题栏 ,必须放在setContentView之前
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.f1_act_record_video);
        // 设置横屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 选择支持半透明模式,在有surfaceview的activity中使用。
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        initSurfaceView();
        ivRecord = (ImageView) findViewById(R.id.btn_record);
        tvHintRecord = (TextView) findViewById(R.id.tv_hint_record);

    }

    private void initSurfaceView() {
        // 获取程序界面中的SurfaceView
        surfaceView = (SurfaceView)findViewById(R.id.sView);
        // 设置分辨率
        surfaceView.getHolder().setFixedSize(1280, 720);
        // 设置该组件让屏幕不会自动关闭
        surfaceView.getHolder().setKeepScreenOn(true);
        SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                doCreate();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                doChange(holder);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                doDestory();
            }
        };
        surfaceView.getHolder().addCallback(callback);
    }


    /**
     * 打开前置摄像头
     */
    private void openFrontCamera() {
        int camIdx = findFrontCamera();
        if (camIdx == -1) {
            PToast.showShort(getResources().getString(R.string.toast_camera_isnull));
            finish();
            return;
        }
        try {
            camera = Camera.open(camIdx);
        } catch (Exception e) {
            PToast.showShort(getResources().getString(R.string.toast_camera_error));
            goToAppSetting();
            finish();
            Log.i(TAG, "open camera exception");
        }
    }

    /**
     * 查找前置摄像头ID
     *
     * @return cameraId
     */
    private int findFrontCamera() {
        int cameraCount;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); // get cameras number

        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return camIdx;
            }
        }
        if(cameraCount == 1)
            return Camera.CameraInfo.CAMERA_FACING_BACK;
        return -1;
    }

    private void doChange(SurfaceHolder holder) {
        Log.i(TAG, "do change");
        try {
            camera.setErrorCallback(new Camera.ErrorCallback() {
                @Override
                public void onError(int error, Camera camera) {
                    Log.i(TAG,"camera error:" + error);
                }
            });
            camera.setPreviewDisplay(holder);
            //设置surfaceView旋转的角度，系统默认的录制是横向的画面
            camera.setDisplayOrientation(getDegree());
            camera.startPreview();


        } catch (Exception e) {
            Log.i(TAG,"set diaplay exception",e);
            e.printStackTrace();
        }
    }

    private void doCreate() {
        openFrontCamera();
        mediaRecorder = new MediaRecorder();
    }

    private void doDestory() {
        Log.i(TAG, "surface destory");
        stopRecord();
    }

    private int getDegree() {
        //获取当前屏幕旋转的角度
        int rotating = this.getWindowManager().getDefaultDisplay().getRotation();
        int degree = 0;
        //根据手机旋转的角度，来设置surfaceView的显示的角度
        switch (rotating) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
            default:
                break;
        }
        return degree;
    }


    public void record(View v) {
        if (isRecording) {
            stopRecord();
            doCreate();
            doChange(surfaceView.getHolder());
            return;
        }
        if (!Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            Toast.makeText(RecordVideoAct.this
                    , getResources().getString(R.string.toast_sdcard_isnull)
                    , Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // 创建保存录制视频的视频文件
            try {
                videoFile = new File(Environment
                        .getExternalStorageDirectory()
                        .getCanonicalFile() + "/testvideo.mp4");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(startFailedCnt == 0)
                camera.unlock();
            mediaRecorder.reset();
            mediaRecorder.setCamera(camera);
            // 设置从麦克风采集声音(或来自录像机的声音AudioSource.CAMCORDER)
            mediaRecorder.setAudioSource(MediaRecorder
                    .AudioSource.MIC);
            // 设置从摄像头采集图像
            mediaRecorder.setVideoSource(MediaRecorder
                    .VideoSource.CAMERA);
            // 设置视频文件的输出格式
            // 必须在设置声音编码格式、图像编码格式之前设置
            mediaRecorder.setOutputFormat(MediaRecorder
                    .OutputFormat.MPEG_4);
            // 设置声音编码的格式
            mediaRecorder.setAudioEncoder(MediaRecorder
                    .AudioEncoder.AMR_NB);

            // 设置图像编码的格式
            mediaRecorder.setVideoEncoder(MediaRecorder
                    .VideoEncoder.H264);

//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
//                Camera.Size size = camera.getParameters().getPreferredPreviewSizeForVideo();
//                Log.i(TAG,"preferred size width:" + size.width + ",height:" + size.height);
//            }
            mediaRecorder.setVideoSize(videoWidth, videoHeight);
            mediaRecorder.setVideoEncodingBitRate(1024 * 1024);
            mediaRecorder.setOrientationHint(270);
            mediaRecorder.setOutputFile(videoFile.getAbsolutePath());
            // 指定使用SurfaceView来预览视频
            mediaRecorder.setPreviewDisplay(surfaceView
                    .getHolder().getSurface());
            mediaRecorder.setMaxDuration(5000);
            mediaRecorder.prepare();
            // 开始录制
            mediaRecorder.start();
            isRecording = true;
            ivRecord.setImageResource(R.drawable.f1_btn_video_recording);
            tvHintRecord.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            cdt.start();
            tvHintRecord.setText(String.valueOf(timeCount));
        } catch (Exception e) {
            e.printStackTrace();
            if(startFailedCnt == 1)
                return;
            startFailedCnt++;
            videoWidth = 640;
            videoHeight = 480;
            record(null);
        }
    }

    public void cancelRecord(View v) {
        if (isRecording)
            return;
        if (mediaRecorder != null) {
            mediaRecorder.release();
        }
        if (camera != null) {
            camera.release();
        }
        finish();
    }


    private final CountDownTimer cdt = new CountDownTimer(6000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (tvHintRecord != null)
                tvHintRecord.setText(String.valueOf(millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            timeCount = 5;
            Intent data = new Intent();
            data.putExtra(EXTRA_RECORD_FILE_PATH, videoFile.getAbsolutePath());
            if (stopRecord()) {
                setResult(RESULT_OK, data);
            } else {
                setResult(RESULT_CANCELED);
            }
            finish();
        }
    };

    private boolean stopRecord() {
        boolean isOk = false;
        if (isRecording) {
            try {
                Thread.sleep(500);
                tvHintRecord.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                tvHintRecord.setText(getResources().getString(R.string.txt_record_time_desc));
                cdt.cancel();
                mediaRecorder.setOnInfoListener(null);
                mediaRecorder.setOnInfoListener(null);
                mediaRecorder.setPreviewDisplay(null);
                mediaRecorder.stop();
                mediaRecorder.release();
                isOk = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                camera.release();
                isRecording = false;
                ivRecord.setImageResource(R.drawable.f1_btn_video_record);
            }
        }
        return isOk;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return isRecording || super.onKeyDown(keyCode, event);
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        StackNode.appendIntent(this, intent);
        startActivityForResult(intent, 123);
    }
}
