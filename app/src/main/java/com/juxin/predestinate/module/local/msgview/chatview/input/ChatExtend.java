package com.juxin.predestinate.module.local.msgview.chatview.input;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kind on 2017/3/31.
 */

public class ChatExtend {

    private static int pageExtendNum = 4;
    private List<CommonGridBtnPanel.BTN_KEY> chatExtendDatas = null;

    public ChatExtend() {
        chatExtendDatas = new ArrayList<>();

        chatExtendDatas.add(CommonGridBtnPanel.BTN_KEY.IMG);
        chatExtendDatas.add(CommonGridBtnPanel.BTN_KEY.VIDEO);
        chatExtendDatas.add(CommonGridBtnPanel.BTN_KEY.VOICE);
    }

    public void setChatExtendDatas(List<CommonGridBtnPanel.BTN_KEY> chatExtendDatas) {
        this.chatExtendDatas = chatExtendDatas;
    }

    public List<CommonGridBtnPanel.BTN_KEY> getList() {
        return chatExtendDatas;
    }

    /**
     * 根据每页需要显示的扩展功能。
     *
     * @param index 对应页。
     * @return 指定页的扩展功能。
     */
    public List<CommonGridBtnPanel.BTN_KEY> getPageExtend(int index) {
        List<CommonGridBtnPanel.BTN_KEY> listTemp = chatExtendDatas;
        int start = index * pageExtendNum;
        int offset = listTemp.size() - start;

        if (offset <= 0) {
            return null;
        }

        if (offset > pageExtendNum) {
            offset = pageExtendNum;
        }

        return listTemp.subList(start, start + offset);
    }
}
