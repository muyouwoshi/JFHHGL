package com.juxin.predestinate.ui.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.View;

import com.juxin.library.dir.DirType;
import com.juxin.library.dir.SDCardUtil;
import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.my.ShareTypeData;
import com.juxin.predestinate.module.local.center.CenterMgr;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.ChineseFilter;
import com.juxin.predestinate.module.util.JsonUtil;
import com.juxin.predestinate.module.util.ShareUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.live.bean.ShareMatrial;
import com.juxin.predestinate.ui.mail.unlock.ShareChanelData;
import com.juxin.predestinate.ui.live.bean.ShareMatrial;
import com.juxin.predestinate.ui.user.util.CenterConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 分享截图工具类
 *
 * @author IQQ
 * @date 2017/9/18
 */
public class ScreenShot {

    // 获取指定Activity的截屏，保存到png文件
    public static Bitmap takeScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();
        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    // 保存到sdcard
    public static void savePic(Bitmap b, String strFileName) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(strFileName);
            b.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存分享图片
     *
     * @param view          截图view
     * @param shareTypeData 分享类型
     */
    public static void saveShareImg(final Context context, final View view, final ShareTypeData shareTypeData, final ShareMatrial shareMatrial) {
        if (view != null) {
            LoadingDialog.show((FragmentActivity) context);
            //设置保存路径
            final String path = DirType.getImageDir() + "sharecode.png";

            final long uid;
            boolean isSelfUid = false;
            final int shareType = shareTypeData.getShareType();
            final String shareCode;
            if (shareType == CenterConstant.SHARE_TYPE_SECOND) {
                UserDetail myInfo = ModuleMgr.getCenterMgr().getMyInfo();
                uid = myInfo.getUid();
                shareCode = myInfo.getShareCode();
                isSelfUid = true;
            } else if (shareType == CenterConstant.SHARE_TYPE_THREE) {
                //直接用 shareMatrial
                uid = shareTypeData.getOtherId();
                shareCode = shareTypeData.getShareCode();
            } else {
                uid = shareTypeData.getOtherId();
                shareCode = shareTypeData.getShareCode();
            }
            String earnMoney = shareTypeData.getEarnMoney();
            earnMoney = TextUtils.isEmpty(earnMoney) ? "0" :  ChineseFilter.subZeroString(ChineseFilter.formatNum((Double.valueOf(earnMoney) * 100), 0) + "");
            if (shareMatrial != null) {
                setShareData(context, view, path, shareMatrial);
            } else {
                ShareUtil.getShareMatriData(isSelfUid ? "0" : String.valueOf(uid), earnMoney, new ShareUtil.GetShareMatrialCallBack() {
                    @Override
                    public void shareMatrialCallBack(List<ShareMatrial> list, int flag, String msg) {
                        LoadingDialog.closeLoadingDialog();
                        if (flag != ShareUtil.GET_DATA_OK) {
                            PToast.showShort(msg);
                            return;
                        }
                        if (list != null && !list.isEmpty()) {
                            setShareData(context, view, path, list.get(0));
                        }
                    }
                });
            }
        }
    }

    private static void setShareData(final Context context, View view, final String path, final ShareMatrial shareMatrial) {
        saveCodePic(view, path).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean saveSuccess) throws Exception {
                        LoadingDialog.closeLoadingDialog();
                        //保存失败
                        if (!saveSuccess) {
                            PToast.showShort(context.getString(R.string.spread_share_save_error));
                            return;
                        }
                        //保存成功
                        getShareChannel((FragmentActivity) context, path, shareMatrial.getLandingPageUrl(), shareMatrial.getShareContentSubTitle(), shareMatrial.getShareContentTitle(), shareMatrial.getShareContentIcon(), 0, "", Integer.parseInt(shareMatrial.getId()));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LoadingDialog.closeLoadingDialog();
                        PToast.showShort(context.getString(R.string.spread_share_save_error));
                        throwable.printStackTrace();
                    }
                });
    }

    private static void getShareChannel(final FragmentActivity context, final String bitmapPath, final String shareLink,
                                        final String content, final String title, final String shareContentImageUri, final int uid, final String shareCode, final int mould) {
        final List<Integer> channels = new ArrayList<>();
        HashMap<String, Object> postParam = new HashMap<>();
        ShareUtil.getShareChannels(""+ModuleMgr.getCenterMgr().getMyInfo().getUid(),"1",new ShareUtil.GetChannelCallBack(){

            @Override
            public void channelBack(ArrayList<Integer> channels, int flag, String msg) {
                if (flag == ShareUtil.GET_DATA_OK) {
                    if (channels != null && channels.size() == 0) {
                        PToast.showShort("没有可分享的渠道");
                    } else {
                        if(!channels.contains(5)) {
                            channels.add(5);
                        }
                        if(!channels.contains(6)) {
                            channels.add(6);
                        }
                        UIShow.showShareDialog(context, bitmapPath, shareLink, content, title, shareContentImageUri, (int) uid, channels, shareCode, mould);
                    }
                } else if (flag == ShareUtil.GET_DATA_PARSE_ERROR) {
                    PToast.showShort(msg);
                } else if (flag == ShareUtil.GET_DATA_FAILED) {
                    PToast.showShort(msg);
                }
            }
        });
//        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.getShareChannel, postParam, new RequestComplete() {
//            @Override
//            public void onRequestComplete(HttpResponse response) {
//                if (response.isOk()) {
//                    try {
//                        if (response.getResponseJson().has("res") && !response.getResponseJson().isNull("res")) {
//                            JSONObject resJo = response.getResponseJson().getJSONObject("res");
//                            JSONArray array = resJo.optJSONArray("channels");
//                            for (int i = 0; i < array.length(); i++) {
//                                if (array.getInt(i) <= 6) {
//                                    channels.add(array.getInt(i));
//                                }
//                            }
//                            if (channels.size() == 0) {
//                                PToast.showShort("没有可分享的渠道");
//                                return;
//                            }
//                            UIShow.showShareDialog(context, bitmapPath, shareLink, shareContentImageUri, title, shareContentImageUri, (int) uid, channels, shareCode, mould);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        PToast.showShort("获取失败，请重试");
//                    }
//                } else {
//                    PToast.showShort(response.getMsg());
//                }
//            }
//        });
    }


    public static boolean saveMsgIcon(String filePath, Bitmap bitmap) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            boolean b = bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
            return b;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 截取view
     *
     * @param view 被截取的view
     * @param path 保存路径
     */
    public static Observable<Boolean> saveCodePic(View view, final String path) {
        final Bitmap bitmap = loadBitmapFromView(view);
        return new Observable<Boolean>() {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer) {
                boolean b = false;
                FileOutputStream fos;
                try {
                    if (bitmap != null) {
                        fos = new FileOutputStream(path);
                        b = bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                        fos.flush();
                        fos.close();
                        if (b) {
                            //通知更新到相册
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri uri = Uri.fromFile(new File(path));
                            intent.setData(uri);
                            getActivity().sendBroadcast(intent);
                        }
                    }
                    observer.onNext(b);
                } catch (IOException e) {
                    e.printStackTrace();
                    observer.onNext(false);
                } finally {
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                }
                observer.onComplete();
            }
        }.subscribeOn(Schedulers.io());

    }


    //获取当前activity
    public static Activity getActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map activities;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) { // 4.4 以下使用的是 HashMap
                activities = (HashMap) activitiesField.get(activityThread);
            } else { // 4.4 以上使用的是 ArrayMap
                activities = (ArrayMap) activitiesField.get(activityThread);
            }
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused"); // 找到 paused 为 false 的activity
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    return (Activity) activityField.get(activityRecord);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap takeScreenShot() {
        return takeScreenShot(getActivity());
    }


    /**
     * 将View转换为Bitmap
     *
     * @param v
     * @return
     */
    public static Bitmap loadBitmapFromView(View v) {
        if (v == null || (v.getWidth() * v.getHeight() < 1)) {
            return null;
        }
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        // Draw background
        Drawable bgDrawable = v.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(c);
        else
            c.drawColor(Color.WHITE);
        // Draw view to canvas
        v.draw(c);
        return b;

    }


    /**
     * 保存图片分享
     *
     * @param bitmap
     * @param callBack
     */
    public static void savePicToShare(Bitmap bitmap, SaveCallBack callBack) {
        String path = getCahceDir("img");
        String name = "shar_" + System.currentTimeMillis() + ".jpg";
        writeBitmap(path, name, bitmap, callBack);
    }


    /**
     * 保存图片
     *
     * @param path
     * @param name
     * @param bitmap
     * @param callBack 回调
     */
    private static void writeBitmap(String path, String name, Bitmap bitmap, SaveCallBack callBack) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        File _file = new File(path + name);
        if (_file.exists()) {
            _file.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(_file);
            if (name != null && !"".equals(name)) {
                int index = name.lastIndexOf(".");
                if (index != -1 && (index + 1) < name.length()) {
                    String extension = name.substring(index + 1).toLowerCase();
                    if ("png".equals(extension)) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    } else if ("jpg".equals(extension)
                            || "jpeg".equals(extension)) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    if (callBack != null) {
                        //通知更新到相册
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(_file);
                        intent.setData(uri);
                        getActivity().sendBroadcast(intent);
                        callBack.shareSuccess();
                    }
                } catch (IOException e) {
                    if (callBack != null) {
                        callBack.shareFail();
                    }
                    e.printStackTrace();
                }
            } else {
                if (callBack != null) {
                    callBack.shareFail();
                }
            }
        }
    }


    //缓存目录
    public static String getCahceDir() {
        return getCahceDir(getActivity(), "");
    }

    public static String getCahceDir(String dir) {
        return getCahceDir(getActivity(), dir);
    }

    public static String getCahceDir(Context context, String dir) {
        String savePath = "";
        try {
            if (SDCardUtil.isSdcardExist()) {
                savePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            } else {
                savePath = context.getFilesDir().getAbsolutePath();
            }

            if (!savePath.endsWith(File.separator))
                savePath = savePath + File.separator;

            savePath = savePath + context.getResources().getString(R.string.app_storage_name);

            if (!TextUtils.isEmpty(dir)) {
                if (!dir.startsWith(File.separator))
                    savePath = savePath + File.separator;

                savePath = savePath + dir;

                if (!savePath.endsWith(File.separator))
                    savePath = savePath + File.separator;
            }

            File file = new File(savePath);
            if (!file.exists() || !file.isDirectory()) {
                if (!file.mkdirs()) {
                    savePath = "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return savePath;
    }

    /**
     * 保存图片回调函数
     */
    public interface SaveCallBack {
        //保存成功
        void shareSuccess();

        //保存失败
        void shareFail();
    }
}
