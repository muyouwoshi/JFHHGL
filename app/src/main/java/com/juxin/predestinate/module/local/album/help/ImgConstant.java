package com.juxin.predestinate.module.local.album.help;

/**
 * 图片相册常量管理类
 * <p>
 * Created by Su on 2016/12/6.
 */

public class ImgConstant {

    /**选择的照片文件夹**/
    public final static String STR_ALBUM_DIRECTORY = "directory";

    /**所有被选中的图片**/
    public final static String STR_ALL_PICK_DATA = "pick_data";

    /**当前被选中的照片**/
    public final static String STR_CURRENT_PIC = "current_pic";

    /**全部照片**/
    public final static String STR_TOTAL_PIC = "total_pic";

    /**多选相片数量**/
    public static final String STR_NUMS = "nums";

    /**是否裁剪标志**/
    public static final String ISCROP = "isCrop";

    /**裁剪类型**/
    public static final String CROP_TYPE = "crop_type";


    // *************************** 自增常量 ************************************
    // 请求码
    public static final int CODE_FOR_TAKE_PIC = 0;     // 图片取消、确认
    public static final int CODE_FOR_BIG_PREVIEW = 1;  // 大图预览
    public static final int CODE_FOR_CROP = 2;  // 裁切

    public static final int CROP_CIRCLE = 1;  // 圆形裁切
    public static final int CROP_RECTANGLE = 2;  // 矩形裁切
}
