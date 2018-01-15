package com.juxin.predestinate.ui.mail.item;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.juxin.library.view.CustomFrameLayout;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.ui.mail.base.CustomBaseMailItem;
import com.juxin.predestinate.ui.mail.base.CustomBlankBarItem;
import com.juxin.predestinate.ui.mail.base.CustomBottomItem;
import com.juxin.predestinate.ui.mail.base.CustomLetterMailItem;
import com.juxin.predestinate.ui.mail.base.CustomOtherMailItem;
import com.juxin.predestinate.ui.mail.base.CustomSpecialMailItem;
import com.juxin.predestinate.ui.mail.base.CustomTitleItem;

/**
 * Created by Kind on 16/2/2.
 */
public class CustomMailItem extends LinearLayout {

    private Context context;
    private CustomFrameLayout customFrameLayout;

    public CustomMailItem(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CustomMailItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CustomMailItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.p1_custom_mail_item, this);
        customFrameLayout = (CustomFrameLayout) view.findViewById(R.id.customMailFrameLayout);
        customFrameLayout.setList(new int[]{R.id.chat_item_letter, R.id.chat_item_act,
                R.id.chat_item_private, R.id.chat_item_title, R.id.chat_item_bottom, R.id.chat_item_blankbar});
    }

    public void onCreateView() {
        getItemLetterView();
        getItemActView();
        getItemSpecialView();
        getItemTitleView();
        getItemBottomView();
        getItemBlankBarView();
    }

    /**
     * 模板1
     * 获得私聊view
     *
     * @return
     */
    CustomLetterMailItem customLetterMailItem;

    public CustomBaseMailItem getItemLetterView() {
        customLetterMailItem = (CustomLetterMailItem) customFrameLayout.findViewById(R.id.chat_item_letter);
        customLetterMailItem.init();
        return customLetterMailItem;
    }

    /**
     * 显示私聊类型
     */
    public void showItemLetter(BaseMessage msgData) {
        customFrameLayout.show(R.id.chat_item_letter);
        customLetterMailItem.showData(msgData);
    }

    public void showLetterGap() {
        customLetterMailItem.showGap();
    }

    public void hideLetterGap() {
        customLetterMailItem.hideGap();
    }

    /**
     * 模板2
     *
     * @return
     */
    CustomOtherMailItem customOtherMailItem;

    public CustomBaseMailItem getItemActView() {
        customOtherMailItem = (CustomOtherMailItem) customFrameLayout.findViewById(R.id.chat_item_act);
        customOtherMailItem.init();
        return customOtherMailItem;
    }

    /**
     * 显示私聊类型
     */
    public void showItemAct(BaseMessage msgData) {
        customFrameLayout.show(R.id.chat_item_act);
        customOtherMailItem.showData(msgData);
    }

    public void showGreetGap() {
        customOtherMailItem.showGap();
    }

    public void hideGreetGap() {
        customOtherMailItem.hideGap();
    }

    /**
     * 模板3 目前是给私密信息用的
     *
     * @return
     */
    CustomSpecialMailItem customSpecialMailItem;

    public CustomBaseMailItem getItemSpecialView() {
        customSpecialMailItem = (CustomSpecialMailItem) customFrameLayout.findViewById(R.id.chat_item_private);
        customSpecialMailItem.init();
        return customSpecialMailItem;
    }

    /**
     * 显示私密信息
     */
    public void showItemSpecial(BaseMessage msgData) {
        customFrameLayout.show(R.id.chat_item_private);
        customSpecialMailItem.showData(msgData);
    }

    CustomTitleItem customTitleItem;

    public CustomBaseMailItem getItemTitleView() {
        customTitleItem = (CustomTitleItem) customFrameLayout.findViewById(R.id.chat_item_title);
        customTitleItem.init();
        return customTitleItem;
    }

    /**
     * 显示语音视频邀请消息
     */
    public void showItemTitle(BaseMessage msgData) {
        customFrameLayout.show(R.id.chat_item_title);
        customTitleItem.showData(msgData);
    }

    public void showTitleGap() {
        customTitleItem.showGap();
    }

    public void hideTitleGap() {
        customTitleItem.hideGap();
    }

    CustomBottomItem customBottomItem;

    public CustomBaseMailItem getItemBottomView() {
        customBottomItem = (CustomBottomItem) customFrameLayout.findViewById(R.id.chat_item_bottom);
        customBottomItem.init();
        return customBottomItem;
    }

    /**
     * 显示语音视频邀请消息
     */
    public void showItemBottom(BaseMessage msgData) {
        customFrameLayout.show(R.id.chat_item_bottom);
        customBottomItem.showData(msgData);
    }

    CustomBlankBarItem customBlankBarItem;
    public CustomBlankBarItem getItemBlankBarView() {
        customBlankBarItem = (CustomBlankBarItem) customFrameLayout.findViewById(R.id.chat_item_blankbar);
        customBlankBarItem.init();
        return customBlankBarItem;
    }

    public void showItemBlankBar(BaseMessage msgData) {
        customFrameLayout.show(R.id.chat_item_blankbar);
    }

    /**
     * 隐藏所有
     */
    public void GoneAll() {
        customFrameLayout.GoneAll();
    }
}