package com.juxin.predestinate.module.local.album.help;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.utils.FileUtil;
import com.juxin.predestinate.bean.StackNode;
import com.juxin.predestinate.module.local.album.activity.PickBigImagesActivity;
import com.juxin.predestinate.module.local.album.activity.ResetOrFinishAct;
import com.juxin.predestinate.module.local.album.activity.TransferActivity;
import com.juxin.predestinate.module.local.album.bean.AlbumDirectories;
import com.juxin.predestinate.module.local.album.bean.SingleDirectoryModel;
import com.juxin.predestinate.module.local.album.bean.SingleImageModel;
import com.juxin.predestinate.module.local.album.crop.CropImageAct;
import com.juxin.predestinate.module.logic.application.App;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * 相册内部展示帮助类
 * <p>
 * Created by Su on 2016/12/21.
 */

public class AlbumHelper {
    private volatile static AlbumHelper instance = null;
    private List<SingleImageModel> allImages = new ArrayList<>();         // 相册中全部图片实体List
    private List<AlbumDirectories> imageDirectories = new ArrayList<>();  // 相册目录实体List

    public void release() {
        allImages = null;
        imageDirectories = null;
        instance = null;
    }

    public static AlbumHelper getInstance() {
        if (instance == null) {
            synchronized (AlbumHelper.class) {
                if (instance == null) {
                    instance = new AlbumHelper();
                }
            }
        }
        return instance;
    }

    // ******************************* 获取相册数据 ***********************************************

    public List<SingleImageModel> getAllImages() {
        if (allImages == null) {
            return new ArrayList<>();
        }
        return allImages;
    }

    public List<AlbumDirectories> getImageDirectories() {
        if (imageDirectories == null) {
            return new ArrayList<>();
        }
        return imageDirectories;
    }

    /**
     * 获取相册中某个目录下的所有图片
     */
    public ArrayList<SingleImageModel> getAllImagesFromCurrentDirectory(int position) {
        ArrayList<SingleImageModel> list = new ArrayList<>();
        if (position == -1) { // 全部图片
            for (SingleImageModel model : getAllImages()) {
                list.add(model);
            }
        } else {
            try {
                for (SingleImageModel model : getImageDirectories().get(position).images.getImages()) {
                    list.add(model);
                }
            }catch (Exception e){
            }
        }
        return list;
    }

    /**
     * 获取所有的选中图片
     */
    public ArrayList<SingleImageModel> getChoosePicFromList(List<String> picklist) {
        ArrayList<SingleImageModel> list = new ArrayList<>();
        for (String path : picklist) {
            SingleImageModel model = new SingleImageModel(path, true, 0, 0);
            list.add(model);
        }
        return list;
    }

    /**
     * 获取position相对应的图片的id
     */
    public long getImgID(int position, int currentShowPosition) {
        try {
            //如果是选择的全部图片
            if (currentShowPosition == -1) {
                return getAllImages().get(position).id;
            } else {
                return getImageDirectories().get(currentShowPosition).images.getImages().get(position).id;
            }
        }catch (Exception e){
            return 0;
        }
    }

    /**
     * 获取position相对应的图片路径
     */
    public String getImgPath(int position, int currentShowPosition) {
        //如果是选择的全部图片
        try {
            if (currentShowPosition == -1) {
                return getAllImages().get(position).path;
            }
            return getImageDirectories().get(currentShowPosition).images.getImagePath(position);
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 获取position相对应图片的时间
     */
    public long getImgDate(int position, int currentShowPosition) {
        if (getAllImages().size() == 0) {
            return System.currentTimeMillis();
        }
        try {
            //如果是选择的全部图片
            if (currentShowPosition == -1) {
                return getAllImages().get(position).date;
            } else {
                return getImageDirectories().get(currentShowPosition).images.getImages().get(position).date;
            }
        }catch (Exception e){
            return 0;
        }
    }

    /**
     * 设置path路径下图片的选中状态
     */
    public void setPickStateFromHashMap(String path, boolean isPick) {
        for (AlbumDirectories directories : getImageDirectories()) {
            if (isPick)
                directories.images.setImage(path);
            else
                directories.images.unsetImage(path);
        }
        for (SingleImageModel model : getAllImages()) {
            if (model.path.equalsIgnoreCase(path))
                model.isPicked = isPick;
        }
    }

    /**
     * 获取path路径下图片的选中状态
     */
    public boolean getSelectStateByPath(String path) {
        //如果是选择的全部图片
        for (SingleImageModel model : getAllImages()) {
            if (model.path.equalsIgnoreCase(path)) {
                return model.isPicked;
            }
        }
        return false;
    }

    /**
     * 获取position对应图片选中状态
     */
    public boolean getSelectState(int position, int currentShowPosition) {
        try {
            //如果是选择的全部图片
            if (currentShowPosition == -1) {
                return getAllImages().get(position).isPicked;
            } else {
                return getImageDirectories().get(currentShowPosition).images.getImagePickOrNot(position);
            }
        }catch (Exception e){
            return false;
        }

    }

    /**
     * 转变position对应图片的选中状态
     */
    public void toggleSelectState(int position, int currentShowPosition) {
        try {
            //如果是选择的全部图片
            if (currentShowPosition == -1) {
                getAllImages().get(position).isPicked = !getAllImages().get(position).isPicked;
                for (AlbumDirectories directories : getImageDirectories()) {
                    directories.images.toggleSetImage(getAllImages().get(position).path);
                }
            } else {
                getImageDirectories().get(currentShowPosition).images.toggleSetImage(position);
                for (SingleImageModel model : getAllImages()) {
                    if (model.path.equalsIgnoreCase(getImageDirectories().get(currentShowPosition).images.getImagePath(position)))
                        model.isPicked = !model.isPicked;
                }
            }
        }catch (Exception e){
        }
    }


    // ********************************* 初始化相册数据 start *********************************************************

    /**
     * 初始化相册图片
     *
     * @param listener 相册初始化完成监听
     */
    public void initAlbum(final OnAlbumCompleteListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = App.getActivity().getContentResolver();
                //获取jpeg和png格式的文件，并且按照时间进行倒序
                Cursor cursor = contentResolver.query(uri, null, MediaStore.Images.Media.MIME_TYPE + "=\"image/jpeg\" or " +
                        MediaStore.Images.Media.MIME_TYPE + "=\"image/png\" or " +
                        MediaStore.Images.Media.MIME_TYPE + "=\"image/gif\"", null, MediaStore.Images.Media.DATE_MODIFIED + " desc");
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        SingleImageModel singleImageModel = new SingleImageModel();
                        singleImageModel.path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                        // 跳过不存在文件
                        if (!FileUtil.isExist(singleImageModel.path)) continue;
                        try {
                            singleImageModel.date = Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)));
                        } catch (NumberFormatException e) {
                            singleImageModel.date = System.currentTimeMillis();
                        }
                        try {
                            singleImageModel.id = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)));
                        } catch (NumberFormatException e) {
                            singleImageModel.id = 0;
                        }
                        getAllImages().add(singleImageModel);     // 存入图片列表
                        String path = singleImageModel.path; // 存入对应目录
                        String parentPath = new File(path).getParent();
                        initAlbumDirectories(parentPath, path, singleImageModel.date, singleImageModel.id);
                    }
                    listener.onInitAlbumfinish();
                    cursor.close();
                }
            }
        }).start();
    }

    /**
     * 初始化相册目录
     */
    private void initAlbumDirectories(String parentPath, String path, long date, long id) {
        SingleDirectoryModel model = getModelFromKey(parentPath, getImageDirectories());
        if (model == null) {  // 创建一个新的相册文件夹目录
            model = new SingleDirectoryModel();
            AlbumDirectories directories = new AlbumDirectories();
            directories.images = model;
            directories.directoryPath = parentPath;
            getImageDirectories().add(directories);
        }
        model.addImage(path, date, id);
    }

    /**
     * 获取某目录详细实体类
     */
    private SingleDirectoryModel getModelFromKey(String path, List<AlbumDirectories> imageDirectories) {
        for (AlbumDirectories directories : imageDirectories) {
            if (directories.directoryPath.equalsIgnoreCase(path)) {
                return directories.images;
            }
        }
        return null;
    }

    /**
     * 相册初始化完成监听类
     */
    public interface OnAlbumCompleteListener {
        void onInitAlbumfinish();
    }
    // ********************************* 初始化相册数据 end *********************************************************

    /**
     * 计算照片的具体时间
     */
    public String calculateShowTime(long time) {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int mDayWeek = c.get(Calendar.DAY_OF_WEEK);
        mDayWeek--;
        //习惯性的还是定周一为第一天
        if (mDayWeek == 0)
            mDayWeek = 7;
        int mWeek = c.get(Calendar.WEEK_OF_MONTH);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        if ((System.currentTimeMillis() - time) < (mHour * 60 + mMinute) * 60 * 1000) {
            return "今天";
        } else if ((System.currentTimeMillis() - time) < (mDayWeek) * 24 * 60 * 60 * 1000) {
            return "本周";
        } else if ((System.currentTimeMillis() - time) < ((long) ((mWeek - 1) * 7 + mDayWeek)) * 24 * 60 * 60 * 1000) {
            return "这个月";
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM", java.util.Locale.getDefault());
            return format.format(time);
        }
    }


    // ******************************* 相机内部页面跳转 *********************************************************

    /**
     * 跳转到中转界面
     */
    public void showTransferAct(Context context, ArrayList<String> data) {
        Intent intent = new Intent(context, TransferActivity.class);
        intent.putExtra(ImgConstant.STR_CURRENT_PIC, data);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 跳转到裁切界面
     */
    public void skipToCrop(Activity activity, String imgPath) {
        Intent tmpIntent = new Intent(activity, CropImageAct.class);
        tmpIntent.putExtra("data", imgPath);
        StackNode.appendIntent(activity, tmpIntent);
        activity.startActivity(tmpIntent);
        activity.finish();
    }

    /**
     * 跳转到裁切界面 : 调本地相机
     * @param imgPath           图片路径
     * @param cropType          新增裁剪类型 2017/7/28 默认0 保持以前状态
     */
    public void skipToCrop2(Activity activity, String imgPath, int cropType) {
        Intent tmpIntent = new Intent(activity, CropImageAct.class);
        tmpIntent.putExtra("data", imgPath);
        tmpIntent.putExtra(ImgConstant.CROP_TYPE, cropType);
        StackNode.appendIntent(activity, tmpIntent);
        activity.startActivityForResult(tmpIntent, ImgConstant.CODE_FOR_CROP);
    }

    /**
     * 跳转到完成拍照界面
     */
    public void skipToFinish(Activity activity, String imgPath) {
        if (TextUtils.isEmpty(imgPath)) {
            PToast.showShort("获取图片出错，请重试");
            return;
        }
        Intent tmpIntent = new Intent(activity, ResetOrFinishAct.class);
        tmpIntent.putExtra("path", imgPath);
        StackNode.appendIntent(activity, tmpIntent);
        activity.startActivityForResult(tmpIntent, ImgConstant.CODE_FOR_TAKE_PIC);
    }

    /**
     * 跳转大图预览
     *
     * @param data     当前相册目录下的所有图片
     * @param picklist 已选中图片list
     * @param picNums  多选图片总数
     * @param position picklist中首次展示的图片
     */
    public void skipToPreview(Activity activity, ArrayList<SingleImageModel> data, ArrayList<String> picklist,
                              int picNums, int position) {
        Intent intent = new Intent(activity, PickBigImagesActivity.class);
        intent.putExtra(ImgConstant.STR_ALBUM_DIRECTORY, data);
        intent.putExtra(ImgConstant.STR_ALL_PICK_DATA, picklist);
        intent.putExtra(ImgConstant.STR_CURRENT_PIC, position);
        intent.putExtra(ImgConstant.STR_TOTAL_PIC, picNums);
        StackNode.appendIntent(activity, intent);
        activity.startActivityForResult(intent, ImgConstant.CODE_FOR_BIG_PREVIEW);
    }


    // ******************************* 调用本地相机 *********************************************************
    public Uri getPhotoUri() {
        return Uri.parse(PSP.getInstance().getString("photoUri", null));
    }

    public void setPhotoUri(Uri photoUri) {
        PSP.getInstance().put("photoUri", photoUri.toString());
    }

    /**
     * 判断系统中是否存在可以启动的相机应用
     *
     * @return 存在返回true，不存在返回false
     */
    public boolean hasCamera(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


    /**
     * 裁切状态： TuSDK相机无法通过界面跳转拿到裁切状态，所以此处添加一个状态保存
     */
    public boolean isCrop() {
        return PSP.getInstance().getBoolean(ImgConstant.ISCROP, false);
    }

    public void setIsCrop(boolean isCrop) {
        PSP.getInstance().put(ImgConstant.ISCROP, isCrop);
    }
}
