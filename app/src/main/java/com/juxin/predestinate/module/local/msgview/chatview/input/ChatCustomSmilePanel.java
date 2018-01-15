package com.juxin.predestinate.module.local.msgview.chatview.input;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.library.utils.BitmapUtil;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.file.UpLoadResult;
import com.juxin.predestinate.module.local.album.ImgSelectUtil;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;
import com.juxin.predestinate.module.local.msgview.smile.SmileItem;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.custom.ViewPagerAdapter;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义
 * Created by Kind on 2017/3/30.
 */
public class ChatCustomSmilePanel extends ChatBaseSmilePanel implements AdapterView.OnItemClickListener, ChatCustomSmileAdapter.DelCEmojiCallBack, PObserver {

    //保存表情资源的列表
    private static int pageResNum = 8;
    private boolean mOutDelClick = false;
    private long mLastTime;
    private List<SmileItem> items = null;

    private TextView mOutDelTv;
    private ViewPager viewPager;

    public ChatCustomSmilePanel(Context context, ChatAdapter.ChatInstance chatInstance, TextView outDelTv) {
        super(context, chatInstance);
        this.mOutDelTv = outDelTv;
        setContentView(R.layout.p1_chat_default_smile);
        MsgMgr.getInstance().attach(this);
        initView();
    }

    public void setData(List<SmileItem> items) {
        if (items == null) {
            items = new ArrayList<>();
        }
        if (items.size() == 0 || (items.size() > 0 && !"custom".equals(items.get(0).getPic()))) {
            items.add(0, new SmileItem("custom"));
        }
        this.items = items;
        mOutDelClick = false;
        mOutDelTv.setText("删除");
        initData();
    }

    public void setDeleteClick(boolean del) {
        mOutDelClick = del;
        initData();
    }

    /**
     * 解绑监听
     */
    public void detach() {
        MsgMgr.getInstance().detach(this);
    }

    // ----------------------------私有方法------------------------------

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.chat_panel_viewpager);
    }

    private void initData() {
        try {
            synchronized (items) {
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getAllViews());
                viewPager.setAdapter(viewPagerAdapter);
                initPointsView(viewPager, viewPagerAdapter.getCount(), true);
                if (null != items) {
                    if (items.size() == 1) {
                        mOutDelTv.setVisibility(View.GONE);
                    } else {
                        mOutDelTv.setVisibility(View.VISIBLE);
                    }
                } else {
                    mOutDelTv.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<View> getAllViews() throws Exception {
        List<View> views = new ArrayList<>();
        View view;
        int index = 0;

        while ((view = getChildView(index++)) != null) {
            views.add(view);
        }
        return views;
    }

    private View getChildView(int index) throws Exception {
        List<SmileItem> listTemp = getPageRes(index);
        if (listTemp == null) return null;

        View view = View.inflate(getContext(), R.layout.p1_chat_cus_smile_grid, null);
        synchronized (listTemp) {
            GridView gv = (GridView) view.findViewById(R.id.chat_panel_gridview);
            gv.setNumColumns(pageResNum / 2);
            gv.setGravity(Gravity.CENTER);
            gv.setVerticalSpacing(UIUtil.dp2px(5));

            ChatCustomSmileAdapter customSmileAdapter = new ChatCustomSmileAdapter(getContext(), listTemp, index, mOutDelClick, this);
            gv.setAdapter(customSmileAdapter);
            gv.setOnItemClickListener(this);
        }
        return view;
    }

    /**
     * 根据每页需要显示的表情数，截取对应的文件名List。
     *
     * @param index 对应页。
     * @return 指定页的资源信息。
     */
    private List<SmileItem> getPageRes(int index) throws Exception {
        if (items == null) return null;

        List<SmileItem> listTemp;
        synchronized (items) {
            listTemp = items;
            int start = index * pageResNum;
            int offset = listTemp.size() - start;

            if (offset <= 0) return null;
            if (offset > pageResNum) offset = pageResNum;

            listTemp = listTemp.subList(start, start + offset);
        }
        return listTemp;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (id == -1) return;
        SmileItem item = (SmileItem) parent.getAdapter().getItem(position);
        if ("custom".equals(item.getPic())) {
            if (mOutDelClick) {
                mOutDelTv.setText("删除");
                setDeleteClick(false);
            }
            ImgSelectUtil.getInstance().pickPhotoGallery(context, new ImgSelectUtil.OnChooseCompleteListener() {
                @Override
                public void onComplete(String... path) {
                    try {
                        if (path == null || path.length == 0 || TextUtils.isEmpty(path[0])) {
                            PToast.showShort("图片选择失败");
                            return;
                        }
                        String sPath = path[0];
                        if (!BitmapUtil.bitmapIsSmall(sPath, 500)) {//尺寸小于500
                            PToast.showShort("表情尺寸太大");
                            return;
                        }
                        long fileL = new File(sPath).length();
                        if (fileL > (1024 * 1024)) {//大小小于1M
                            PToast.showShort("表情大小不能大于1M");
                            return;
                        }
                        uploadCFace(sPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            ChatAdapter chatAdapter = getChatInstance().chatAdapter;
            ModuleMgr.getChatMgr().sendImgMsg(null, chatAdapter.getWhisperId(), item.getPic(), chatAdapter.getChatInfo().getKnow());
        }
    }

    /**
     * 上传图片，成功后执行添加表情接口
     */
    private void uploadCFace(String path) {
        ModuleMgr.getMediaMgr().sendHttpFile(Constant.UPLOAD_TYPE_FACE, path, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    UpLoadResult upLoadResult = (UpLoadResult) response.getBaseData();
                    addCFace(upLoadResult.getFile_http_path());
                } else {
                    PToast.showShort("表情添加失败");
                }
            }
        });
    }

    /**
     * 添加表情
     */
    private void addCFace(final String url) {
        if (isHasAdded(url)) {
            PToast.showShort(getContext().getString(R.string.face_is_already_exist));
        } else {
            ModuleMgr.getCommonMgr().addCustomFace(url, new RequestComplete() {
                @Override
                public void onRequestComplete(HttpResponse response) {
                    try {
                        if (!response.isOk()) {
                            PToast.showShort("表情添加失败");
                            return;
                        }
                        if (null == items) return;

                        optItems(new SmileItem(url), 0);
                        PToast.showShort("表情添加成功");
                        initData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 删除表情
     */
    @Override
    public void delCEmoji(final String url, final int curPage, final int positon) {
        if (TextUtils.isEmpty(url)) return;
        if (isFastClick()) return;

        ModuleMgr.getCommonMgr().delCustomFace(url, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                try {
                    if (!response.isOk()) {
                        PToast.showShort("表情删除失败");
                        return;
                    }
                    if (null == items) return;

                    optItems(null, curPage * pageResNum + positon);
                    PToast.showShort("表情删除成功");
                    initData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void optItems(SmileItem smileItem, int positon) {
        synchronized (items) {
            if (null != smileItem) {
                items.add(smileItem);
            } else {
                items.remove(positon);
            }
        }
    }

    @Override
    public void onMessage(String key, Object value) {
        if (null == value) return;
        switch (key) {
            case MsgType.MT_ADD_CUSTOM_SMILE:
                if (isFastClick()) return;
                addCFace((String) value);
                break;

            default:
                break;
        }
    }

    private boolean isFastClick() {
        boolean isFastClick = false;
        long curTime = System.currentTimeMillis();
        if (curTime - mLastTime < 1000) {
            isFastClick = true;
            PToast.showShort("操作太快，请稍后");
        }
        mLastTime = System.currentTimeMillis();
        return isFastClick;
    }

    /**
     * 判断这个图片是否添加过
     *
     * @param url 图片的url
     * @return
     */
    private boolean isHasAdded(String url) {
        if (items == null || items.size() == 0) {
            return false;
        } else {
            for (SmileItem item : items) {
                if (item.getPic().equals(url)) {
                    return true;
                }
            }
        }
        return false;
    }

}
