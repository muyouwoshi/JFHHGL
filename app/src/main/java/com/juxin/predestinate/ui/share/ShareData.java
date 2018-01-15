package com.juxin.predestinate.ui.share;

/**
 * 类描述：
 * 创建时间：2017/11/6 11:00
 * 修改时间：2017/11/6 11:00
 * Created by zhoujie on 2017/11/6
 * 修改备注：
 */

public class ShareData {
    private String shareTitle;
    private String shareContent;
    private String shareIconUrl;
    private boolean isOk;

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getShareContent() {
        return shareContent;
    }

    public void setShareContent(String shareContent) {
        this.shareContent = shareContent;
    }

    public String getShareIconUrl() {
        return shareIconUrl;
    }

    public void setShareIconUrl(String shareIconUrl) {
        this.shareIconUrl = shareIconUrl;
    }

    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }
}
