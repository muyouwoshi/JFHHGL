package com.juxin.predestinate.module.local.msgview.chatview.input;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.GridView;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.juxin.library.log.PToast;
import com.juxin.library.utils.TypeConvertUtil;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.module.local.album.ImgSelectUtil;
import com.juxin.predestinate.module.local.mail.MailSpecialID;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;
import com.juxin.predestinate.module.local.msgview.chatview.base.ChatViewPanel;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.custom.ViewPagerAdapter;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.TimerUtil;
import com.juxin.predestinate.ui.agora.model.AgoraConstant;
import com.juxin.predestinate.ui.agora.util.VideoAudioChatHelper;
import com.juxin.predestinate.ui.user.check.bean.VideoConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 私聊页底部功能panel
 *
 * @author Kind
 * @date 2017/3/31
 */
public class ChatExtendPanel extends ChatViewPanel implements RequestComplete {

    private ChatExtend chatExtend = new ChatExtend();
    private ViewPager vp = null;
    private CommonGridBtnPanel.BtnAdapter chatExtendAdapter;
    private VideoConfig config;
    private int chumLevle;
    private HttpResponse response;

    public ChatExtendPanel(Context context, ChatAdapter.ChatInstance chatInstance) {
        super(context, chatInstance);

        chatInstance.chatExtendPanel = this;

        setContentView(R.layout.p1_chat_extend);
        initView();
    }

    public void initView() {
        vp = (ViewPager) findViewById(R.id.chat_panel_viewpager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getAllViews());
        vp.setAdapter(viewPagerAdapter);

        show(false);
    }

    /**
     * 延迟请求音视频开关配置
     */
    private void reqVideoChatConfig() {
        TimerUtil.beginTime(new TimerUtil.CallBack() {
            @Override
            public void call() {
                ModuleMgr.getCenterMgr().reqVideoChatConfig(getChatInstance().chatAdapter.getLWhisperId(), ChatExtendPanel.this);
            }
        }, 200);
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
        reqVideoChatConfig();
        List<CommonGridBtnPanel.BTN_KEY> listTemp = chatExtend.getPageExtend(index);

        if (listTemp == null || listTemp.isEmpty()) {
            return null;
        }

        View view = View.inflate(getContext(), R.layout.p1_chat_smile_grid, null);
        GridView gv = view.findViewById(R.id.chat_panel_gridview);
        gv.setNumColumns(4);

        List<CommonGridBtnPanel.BTN_KEY> list = new ArrayList<>();
        list.addAll(listTemp);

        chatExtendAdapter = new CommonGridBtnPanel.BtnAdapter(getContext(), list);
        gv.setAdapter(chatExtendAdapter);

        chatExtendAdapter.setBtnClickListener(new CommonGridBtnPanel.BtnClickListener() {
            @Override
            public void onClick(CommonGridBtnPanel.BTN_KEY key) {
                if (key == null) {
                    return;
                }
                final ChatAdapter chatAdapter = getChatInstance().chatAdapter;
                switch (key) {
                    case IMG:
                        if (getContext() instanceof BaseActivity) {
                            if (PermissionsUtil.hasPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                ImgSelectUtil.getInstance().pickPhotoGallery(getContext(), new ImgSelectUtil.OnChooseCompleteListener() {
                                    @Override
                                    public void onComplete(String... path) {
                                        if (path.length > 0) {
                                            Statistics.userBehavior(SendPoint.chatframe_tool_prcture,
                                                    TypeConvertUtil.toLong(getChatInstance().chatAdapter.getWhisperId()));
                                            ModuleMgr.getChatMgr().sendImgMsg(chatAdapter.getChannelId(), chatAdapter.getWhisperId(), path[0], chatAdapter.getChatInfo().getKnow());
                                        }
                                    }
                                });
                            } else {
                                PermissionsUtil.requestPermission((BaseActivity) getContext(), new PermissionListener() {
                                    @Override
                                    public void permissionGranted(@NonNull String[] permissions) {
                                        ImgSelectUtil.getInstance().pickPhotoGallery(getContext(), new ImgSelectUtil.OnChooseCompleteListener() {
                                            @Override
                                            public void onComplete(String... path) {
                                                if (path.length > 0) {
                                                    Statistics.userBehavior(SendPoint.chatframe_tool_prcture,
                                                            TypeConvertUtil.toLong(getChatInstance().chatAdapter.getWhisperId()));
                                                    ModuleMgr.getChatMgr().sendImgMsg(chatAdapter.getChannelId(), chatAdapter.getWhisperId(), path[0], chatAdapter.getChatInfo().getKnow());
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void permissionDenied(@NonNull String[] permissions) {
                                        PToast.showShort("请打开存储权限");
                                    }
                                }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            }
                        } else {
                            ImgSelectUtil.getInstance().pickPhotoGallery(getContext(), new ImgSelectUtil.OnChooseCompleteListener() {
                                @Override
                                public void onComplete(String... path) {
                                    if (path.length > 0) {
                                        Statistics.userBehavior(SendPoint.chatframe_tool_prcture,
                                                TypeConvertUtil.toLong(getChatInstance().chatAdapter.getWhisperId()));
                                        ModuleMgr.getChatMgr().sendImgMsg(chatAdapter.getChannelId(), chatAdapter.getWhisperId(), path[0], chatAdapter.getChatInfo().getKnow());
                                    }
                                }
                            });
                        }
                        break;
                    case VIDEO://视频聊天
                        SourcePoint.getInstance().lockSource(App.activity, "toolplus_video");
                        Statistics.userBehavior(SendPoint.chatframe_tool_video,
                                TypeConvertUtil.toLong(getChatInstance().chatAdapter.getWhisperId()));
                        if (config == null || !config.isVideoChat()
                                || MailSpecialID.customerService.getSpecialID() == chatAdapter.getLWhisperId()) {
                            PToast.showShort(getContext().getString(R.string.user_other_not_video_chat));
                            return;
                        }
                        UserInfoLightweight info = chatAdapter.getUserInfo(chatAdapter.getLWhisperId());
                        VideoAudioChatHelper.getInstance().inviteVAChat((Activity) getContext(), chatAdapter.getLWhisperId(), AgoraConstant.RTC_CHAT_VIDEO,
                                ModuleMgr.getCenterMgr().getMyInfo().isMan(), Constant.APPEAR_TYPE_NO, info == null ? "" : String.valueOf(info.getChannel_uid()), false,
                                SourcePoint.getInstance().getSource());
                        break;
                    case VOICE://语音
                        SourcePoint.getInstance().lockSource(App.activity, "toolplus_voice");
                        Statistics.userBehavior(SendPoint.chatframe_tool_voice,
                                TypeConvertUtil.toLong(getChatInstance().chatAdapter.getWhisperId()));
                        if (config == null || !config.isVoiceChat()
                                || MailSpecialID.customerService.getSpecialID() == chatAdapter.getLWhisperId()) {
                            PToast.showShort(getContext().getString(R.string.user_other_not_voice_chat));
                            return;
                        }
                        long whisperId = chatAdapter.getLWhisperId();
                        UserInfoLightweight infoLight = chatAdapter.getUserInfo(whisperId);

                        VideoAudioChatHelper.getInstance().inviteVAChat((Activity) getContext(), whisperId, AgoraConstant.RTC_CHAT_VOICE,
                                infoLight == null ? "" : String.valueOf(infoLight.getChannel_uid()),
                                SourcePoint.getInstance().getSource());
                        break;
                    default:
                        break;
                }
            }
        });
        return view;
    }

    /**
     * 设置新的扩展信息。
     *
     * @param chatExtendDatas 扩展功能表。
     */
    public void setChatExtendDatas(List<CommonGridBtnPanel.BTN_KEY> chatExtendDatas) {
        if (chatExtendDatas == null) {
            getChatInstance().chatAdapter.setShowExtend(false);
            chatExtend.setChatExtendDatas(new ArrayList<CommonGridBtnPanel.BTN_KEY>());
        } else {
            getChatInstance().chatAdapter.setShowExtend(true);
            chatExtend.setChatExtendDatas(chatExtendDatas);
        }

        if (vp != null) {
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getAllViews());
            vp.setAdapter(viewPagerAdapter);
        }
    }

    @Override
    public void onRequestComplete(HttpResponse response) {
        this.response = response;
        if (response.getUrlParam() == UrlParam.reqVideoChatConfig) {
            if (response.isOk()) {
                config = (VideoConfig) response.getBaseData();
                if (config == null) {
                    return;
                }

                if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
                    CommonGridBtnPanel.BTN_KEY.VIDEO.setPrice(config.getVideoPrice());
                    CommonGridBtnPanel.BTN_KEY.VIDEO.setEnable(config.isVideoChat());
                    CommonGridBtnPanel.BTN_KEY.VOICE.setPrice(config.getAudioPrice());
                    CommonGridBtnPanel.BTN_KEY.VOICE.setEnable(config.isVoiceChat());
                } else {
                    CommonGridBtnPanel.BTN_KEY.VIDEO.setPrice(0);
                    CommonGridBtnPanel.BTN_KEY.VIDEO.setEnable(true);
                    CommonGridBtnPanel.BTN_KEY.VOICE.setPrice(0);
                    CommonGridBtnPanel.BTN_KEY.VOICE.setEnable(true);
                }

                // 设置打折后的音视频价格
                NobilityList.CloseFriend chumInfo = ModuleMgr.getCommonMgr().getCommonConfig().getNobilityList().queryCloseFriend(chumLevle);
                if (chumInfo.getDiscount() != 10 && chumLevle != -1) {  //设置语音打折后的价格
                    CommonGridBtnPanel.BTN_KEY.VOICE.setPriceTrue(config.getAudioPrice() * chumInfo.getDiscount() / 10);
                } else {
                    CommonGridBtnPanel.BTN_KEY.VOICE.setPriceTrue(0);
                }

                if (chumInfo.getDiscount() != 10 && chumLevle != -1) {  //设置视频打折后的价格
                    CommonGridBtnPanel.BTN_KEY.VIDEO.setPriceTrue(config.getVideoPrice() * chumInfo.getDiscount() / 10);
                } else {
                    CommonGridBtnPanel.BTN_KEY.VIDEO.setPriceTrue(0);
                }
                if ((config.isVoiceChat() && !(MailSpecialID.customerService.getSpecialID() == getChatInstance().chatAdapter.getLWhisperId()))
                        || !ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
                    CommonGridBtnPanel.BTN_KEY.VOICE.setIcon(R.drawable.chat_input_grid_voice_selector);
                } else {
                    CommonGridBtnPanel.BTN_KEY.VOICE.setIcon(R.drawable.p1_add_c03);
                }

                if ((config.isVideoChat() && !(MailSpecialID.customerService.getSpecialID() == getChatInstance().chatAdapter.getLWhisperId()))
                        || !ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
                    CommonGridBtnPanel.BTN_KEY.VIDEO.setIcon(R.drawable.chat_input_grid_video_selector);
                } else {
                    CommonGridBtnPanel.BTN_KEY.VIDEO.setIcon(R.drawable.p1_add_b03);
                }
                chatExtendAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setChumLevle(int chumLevle) {
        this.chumLevle = chumLevle;
        if (response != null) {
            onRequestComplete(response);
        }
    }
}
