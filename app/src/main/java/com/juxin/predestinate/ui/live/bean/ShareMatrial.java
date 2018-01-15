package com.juxin.predestinate.ui.live.bean;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 分享素材获取
 * Created by chengxiaobo on 2017/11/7.
 */

public class ShareMatrial implements Serializable {

    /**
     * {
     * "shareMatrialList": [
     * {
     * "id": 1,
     * "posterPageUrl": "http://w.bfyvthd.com/mahi/index03?qrcode_content=ph5lja.gjcohuk.com/tb/3/yfbtg%25257Cxtvpp%25257CIgG%25252BqX0N1b32Fl%25252FKGNc8hnrcVMVATQu6JmAQUdfTXxe6XDKr&username=hhvv", //海报页url
     * "landingPageUrl": "http://ph5lja.gjcohuk.com/tb/3/yfbtg%7Cxtvpp%7CIgG%2BqX0N1b32Fl%2FKGNc8hnrcVMVATQu6JmAQUdfTXxe6XDKr",//落地页url
     * "previewImageUrl": "http://image1.yuanfenba.net/uploads/oss/promoter/matrial/201711/06/1905048140.jpg", //预览图URL
     * "shareContentTitle": "我是小白兔，我在小友赚了40.60元",// 分享标题
     * "shareContentSubTitle": "简单赚钱，真的可以提现哦", //分享副标题
     * "shareContentIcon": "http://" //分享图标
     * "matrialType":1,   //素材类型  1:图片  2: 文字
     * "preview_img":"http://"  //预览图地址
     * }
     * ]
     * },
     */

    public static final int SHARE_TYPE_PIC = 1;
    public static final int SHARE_TYPE_URL = 2;

    private String id;
    //海报页url
    private String posterPageUrl;
    //落地页url
    private String landingPageUrl;
    //预览图url
    private String previewImageUrl;
    // 分享标题
    private String shareContentTitle;
    //分享副标题
    private String shareContentSubTitle;
    //分享图标
    private String shareContentIcon;
    //1:图片  2: 文字
    private int matrialType;
    //"http://"  //预览图地址
    private String preview_img;

    public ShareMatrial parseJson(JSONObject jo) {
//        "id": 6,
//                "shareImageUrl": "http://w.bfyvthd.com/mahi/index01?qrcode_content=",
//                "createTime": "Oct 30, 2017 4:58:13 PM",
//                "lastUpdateTime": "Oct 31, 2017 12:10:33 PM",
//                "state": 1,
//                "shareUrl": "aixiuyuan.cn/tb//mahi/index4?token=/",
//                "contentUserId": "0",
//                "shareHeadImageUrl": "http://image1.yuanfenba.net/uploads/oss/promoter/matrial/201710/31/1210331504.jpg",
//                "shareGender": 2,
//                "selfGender": 2

        id = jo.optString("id");
        posterPageUrl = jo.optString("posterPageUrl");
        landingPageUrl = jo.optString("landingPageUrl");
        previewImageUrl = jo.optString("previewImageUrl");
        shareContentTitle = jo.optString("shareContentTitle");
        shareContentSubTitle = jo.optString("shareContentSubTitle");
        shareContentIcon = jo.optString("shareContentIcon");
        matrialType = jo.optInt("matrialType");
        preview_img = jo.optString("preview_img");
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPosterPageUrl() {
        return posterPageUrl;
    }

    public void setPosterPageUrl(String posterPageUrl) {
        this.posterPageUrl = posterPageUrl;
    }

    public String getLandingPageUrl() {
        return landingPageUrl;
    }

    public void setLandingPageUrl(String landingPageUrl) {
        this.landingPageUrl = landingPageUrl;
    }

    public String getPreviewImageUrl() {
        return previewImageUrl;
    }

    public void setPreviewImageUrl(String previewImageUrl) {
        this.previewImageUrl = previewImageUrl;
    }

    public String getShareContentTitle() {
        return shareContentTitle;
    }

    public void setShareContentTitle(String shareContentTitle) {
        this.shareContentTitle = shareContentTitle;
    }

    public String getShareContentSubTitle() {
        return shareContentSubTitle;
    }

    public void setShareContentSubTitle(String shareContentSubTitle) {
        this.shareContentSubTitle = shareContentSubTitle;
    }

    public String getShareContentIcon() {
        return shareContentIcon;
    }

    public void setShareContentIcon(String shareContentIcon) {
        this.shareContentIcon = shareContentIcon;
    }

    public int getMatrialType() {
        return matrialType;
    }

    public void setMatrialType(int matrialType) {
        this.matrialType = matrialType;
    }

    public String getPreview_img() {
        return preview_img;
    }

    public void setPreview_img(String preview_img) {
        this.preview_img = preview_img;
    }
}
