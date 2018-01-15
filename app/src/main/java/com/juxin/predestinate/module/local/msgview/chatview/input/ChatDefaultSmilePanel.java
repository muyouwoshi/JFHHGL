package com.juxin.predestinate.module.local.msgview.chatview.input;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;
import com.juxin.predestinate.module.logic.baseui.custom.ViewPagerAdapter;
import com.juxin.predestinate.module.util.UIUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kind on 2017/3/31.
 */

public class ChatDefaultSmilePanel extends ChatBaseSmilePanel implements AdapterView.OnItemClickListener {
    private ViewPager vp = null;

    public ChatDefaultSmilePanel(Context context, ChatAdapter.ChatInstance chatInstance) {
        super(context, chatInstance);

        setContentView(R.layout.p1_chat_default_smile);
        initView();
    }

    public void initView() {
        vp = (ViewPager) findViewById(R.id.chat_panel_viewpager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getAllViews());
        vp.setAdapter(viewPagerAdapter);

        initPointsView(vp, viewPagerAdapter.getCount(), false);
    }

    private List<View> getAllViews() {
        List<View> views = new ArrayList<View>();
        View view = null;
        int index = 0;

        while ((view = getChildView(index++)) != null) {
            views.add(view);
        }

        return views;
    }

    private View getChildView(int index) {
        List<EmojiPack.EmojiItem> listTemp = getPageRes(index);

        if (listTemp == null) {
            return null;
        }

        View view = View.inflate(getContext(), R.layout.p1_chat_smile_grid, null);
        GridView gv = (GridView) view.findViewById(R.id.chat_panel_gridview);
        gv.setPadding(0, UIUtil.dp2px(9), 0, 0);

        List<EmojiPack.EmojiItem> list = new ArrayList<EmojiPack.EmojiItem>();
        list.addAll(listTemp);

        // 删除按钮
       // list.add(EmojiPack.getDelBtnItem());

        final ChatDefaultSmileAdapter chatSmileAdapter = new ChatDefaultSmileAdapter(getContext(), list);
        gv.setAdapter(chatSmileAdapter);
        gv.setOnItemClickListener(this);

        return view;
    }

    /**
     * 保存表情资源的列表。
     */
    private static List<EmojiPack.EmojiItem> resList = null;
    private static int pageResNum = 32;

    /**
     * 根据最大索引生成对应的资源文件名。
     *
     * @return 资源文件的文件名列表。
     */
    public static List<EmojiPack.EmojiItem> getSmileRes() {
        if (resList != null) {
            return resList;
        }

        resList = ChatSmile.emojiPack.getEmoji();
        return resList;
    }

    /**
     * 根据每页需要显示的表情数，截取对应的文件名List。
     *
     * @param index 对应页。
     * @return 指定页的资源信息。
     */
    public static List<EmojiPack.EmojiItem> getPageRes(int index) {
        List<EmojiPack.EmojiItem> listTemp = getSmileRes();
        int start = index * pageResNum;
        int offset = listTemp.size() - start;

        if (offset <= 0) {
            return null;
        }

        if (offset > pageResNum) {
            offset = pageResNum;
        }

        return listTemp.subList(start, start + offset);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (id == -1) {
            return;
        }

        EmojiPack.EmojiItem smileItem = (EmojiPack.EmojiItem) parent.getAdapter().getItem(position);
        getChatInstance().chatInputPanel.addSmile(smileItem);
    }
}
