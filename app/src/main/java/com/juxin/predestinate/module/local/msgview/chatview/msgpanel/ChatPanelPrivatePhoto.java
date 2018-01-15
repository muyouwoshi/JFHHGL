package com.juxin.predestinate.module.local.msgview.chatview.msgpanel;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.chat.msgtype.PrivateMessage;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.baseui.custom.NoScrollHorizontalListView;
import com.juxin.predestinate.module.util.UIShow;

import java.util.ArrayList;
import java.util.List;

/**
 * 私密图片布局
 */
public class ChatPanelPrivatePhoto extends BasePanel implements AdapterView.OnItemClickListener {

    private PrivateMessage msg;

    private NoScrollHorizontalListView mHorizontalListView;
    private ChatSecrePhotoAdapter mChatSecrePhotoAdapter;
    private TextView tvNum;

    public ChatPanelPrivatePhoto(Context context) {
        super(context);
        setContentView(R.layout.f1_chat_item_panel_secre_photo);

        initView();
    }

    private void initView() {
        mHorizontalListView = (NoScrollHorizontalListView) findViewById(R.id.chat_item_panel_prave_phote_hlv);
        tvNum = (TextView) findViewById(R.id.chat_item_panel_prave_phote_tv_num);
        mChatSecrePhotoAdapter = new ChatSecrePhotoAdapter(getContext(), null);
        mHorizontalListView.setAdapter(mChatSecrePhotoAdapter);
        mHorizontalListView.setOnItemClickListener(this);
        mHorizontalListView.onTouchEvent(null);
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    public void reSetData(PrivateMessage msg) {
        if (msg == null)
            return;

        this.msg = msg;

        List<String> photos = new ArrayList<>();
        if (msg.getPhotos() == null) {
            return;
        }
        photos.addAll(msg.getPhotos());

        tvNum.setText(getContext().getString(R.string.photo_num, photos.size()));

        for (int i = photos.size() - 1; i >= 3; i--) {
            photos.remove(i);
        }

        mChatSecrePhotoAdapter.setList(photos);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (msg == null)
            return;
        //跳转浏览私密照片页
        UIShow.showPrivatePhotoDisplayAct(App.activity, msg.getQun_id(), msg.getLWhisperID(), msg.getInfo(), (ArrayList<String>) msg.getPhotos());
    }
}