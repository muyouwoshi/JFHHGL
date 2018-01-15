package com.juxin.predestinate.module.local.statistics;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.juxin.library.log.PLogger;
import com.juxin.predestinate.bean.StackNode;
import com.juxin.predestinate.bean.live.RoomPayInfo;
import com.juxin.predestinate.module.local.statistics.CommonSource.CommonSourceContainer;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.ui.live.GirlLiveEndActivity;
import com.juxin.predestinate.ui.live.ui.LiveTopFragment;

import java.util.Map;

/**
 * @author Mr.Huang
 * @date 2017/9/11
 */
public class SourcePoint {

    private static volatile SourcePoint singleton;
    private Register register = new Register();
    private volatile CommonSourceContainer lockSource;  // 统一的source来源
    private final YCoinSource ySource;  //根据统一的source来源检查并返回 Y币 的source来源
    private final VipSource vipSource;  //根据统一的source来源检查并返回 VIP 的source来源
    private final GiftSendSource giftSource; //根据统一的source来源检查并返回 Gift 的source来源
    private final DiamondSource diamondSource;  //根据统一的source来源检查并返回 Diamond 的source来源
    private final AudioVedioSource audioVedioSource; //根据统一的source来源检查并返回 AudioVedio 的source来源
    private RoomPaySourceContainer roomPaySourceContainer; // roomPay 统计信息
    private PPCSource ppcSource; //PPC统计信息
    private String shareSource;

    private SourcePoint() {
        ySource = new YCoinSource();
        vipSource = new VipSource();
        giftSource = new GiftSendSource();
        diamondSource = new DiamondSource();
        audioVedioSource = new AudioVedioSource();
    }

    public static SourcePoint getInstance() {
        if (singleton == null) {
            synchronized (SourcePoint.class) {
                if (singleton == null) {
                    singleton = new SourcePoint();
                }
            }
        }
        return singleton;
    }

    public void register(StackRegister register) {
        if (register != null) {
            register.register(this.register);
        }
    }

    private String getSource(Activity activity, String prefix) {
        String source = getSource(activity);
        if ("".equals(source)) {
            return source;
        }
        if ("haoyou_chatframe".equals(source)) {      //从好友进入聊天框发起来源都改为任务来源
            if (prefix.contains("video")) {
                source += "_videotask_go";
            } else if (prefix.contains("voice")) {
                source += "_voicetask_go";
            } else if (prefix.contains("gift")) {
                source += "_gifttask_go";
            }
        } else {
            source = (source + "_" + prefix);
        }
        PLogger.d("source: " + source);
        return source;
    }

    private String getSource(Activity activity) {
        String source = null;
        if (activity != null && activity instanceof FragmentActivity) {
            StackNode node = StackNode.getStackNode((FragmentActivity) activity);
            if (node != null) {
                source = getSource(node);
            }
        }
        source = (source == null ? "" : source);
        PLogger.d("source: " + source);
        return source;
    }

    private String getSource(StackNode node) {
        PLogger.d("node: " + node.toString());
        String source = null;
        for (Map.Entry<StackNode, String> entry : register.stackSourceMap.entrySet()) {
            StackNode key = entry.getKey();
            if (equals(key, node)) {
                PLogger.d("key: " + key.toString());
                source = entry.getValue();
                break;
            }
        }
        return source == null ? "" : source;
    }

    private static boolean equals(StackNode c, StackNode d) {
        if (c.stack.equals(d.stack)) {
            if (c.previous != null && d.previous != null) {
                return equals(c.previous, d.previous);
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public interface StackRegister {
        void register(Register register);
    }

    /**
     * @param activity 将此activity的source信息保留，再次获取source时使用此activity的source信息
     * @param prefix   在activity的source字段后面添加后缀形成新的source
     */
    public SourcePoint lockSource(Activity activity, String prefix) {
        String source = getSource(activity, prefix);
        lockSource = new CommonSourceContainer(source, StackNode.getStackNode((FragmentActivity) activity));
        return this;
    }

    /**
     * @param source 完整的source信息，不需要从Activity中获取前缀
     */
    public SourcePoint lockSource(String source) {
        lockSource = new CommonSourceContainer(source, StackNode.getStackNode((FragmentActivity) App.activity));
        return this;
    }

    /**
     * 在{@link #lockSource(Activity, String)}或{@link #lockSource(String)} 之后使用
     *
     * @return 返回包含已保存的source统计信息的 roompay 统计信息容器<p>
     */
    public SourcePoint lockRoompaySource(RoomPayInfo info) {
        if (lockSource == null) {
            lockSource("");
        }
        roomPaySourceContainer = new RoomPaySourceContainer(lockSource);
        roomPaySourceContainer.setInfo(info);
        return this;
    }

    /**
     * @param uid        聊天冲vip/Y币/钥匙/钻石/当前聊天页面/他人的信息:为当前聊天页面用户uid;
     * @param channel_id 聊天冲vip/Y币/钥匙/钻石/当前聊天页面/他人的信息:为当前聊天页面用户channel_uid;
     */
    public SourcePoint lockPPCSource(long uid, int channel_id) {
        ppcSource = new PPCSource(uid, channel_id);
        return this;
    }

    /**
     * 不需要统计时置空 例：捕女神由H5统计
     */
    public SourcePoint clearPPCSource() {
        ppcSource = null;
        return this;
    }

    public SourcePoint lockShareSource(String source){
        this.shareSource = source;
        return this;
    }

    /**
     * 获取影视频发起来源
     */
    public String getSource() {
        if (lockSource == null) {//没有提前调用lockSource("")保存埋点信息时，保存一个空字符串，从当前的Activity堆栈信息中找其他信息
            lockSource("");
        }
        String source = audioVedioSource.getSource(lockSource);
        return source;
    }

    /**
     * 获取VIP，Y币，钻石的来源
     */
    public String getSource(String pay_name) {
        if (lockSource == null || pay_name == null) {
            return getSource();
        }
        String source = "";
        if (pay_name.contains("Y币")) {
            return getYCoinSource();
        } else if (pay_name.contains("VIP")) {
            return getVIPSource();
        } else if (pay_name.contains("钻石")) {
            return getDiamonSource();
        }
        return source;
    }

    public String getYCoinSource() {
        return ySource.getSource(lockSource);
    }

    public String getVIPSource() {
        return vipSource.getSource(lockSource);
    }

    public String getDiamonSource() {
        return getDiamonSource(new DiamondSource.DiamondSourceContainer(lockSource));
    }

    private String getDiamonSource(DiamondSource.DiamondSourceContainer container) {
        return diamondSource.getSource(container);
    }

    /**
     * 获取roompay统计信息
     *
     * @param willPost true 需要检查并上传，false 不需要检查
     */
    public RoomPayInfo getRoomPaySource(boolean willPost) {
        if (roomPaySourceContainer == null) {
            return null;
        }
        if (!willPost) {
            return roomPaySourceContainer.getInfo();
        }
        roomPaySourceContainer.getInfo().source = getDiamonSource(roomPaySourceContainer);//获取钻石source
        if (!roomPaySourceContainer.node.withNode(LiveTopFragment.class)
                || roomPaySourceContainer.node.stack.equals(GirlLiveEndActivity.class.getName())) {
            return null;//是否在直播是发起
        }
        return roomPaySourceContainer.getInfo();
    }

    /**
     * 获取礼物来源
     */
    public String getGiftSource() {
        if (lockSource == null) {
            lockSource("");
        }
        return giftSource.getSource(new GiftSendSource.GiftSendSourceContainer(lockSource));
    }

    public PPCSource getPPCSource() {
        return ppcSource;
    }

    public String getShareSource(){
        return TextUtils.isEmpty(shareSource)? "" :shareSource;
    }

    /**
     * 注册页面跳转路径
     */
    public static final class Register {

        private Map<StackNode, String> stackSourceMap = new ArrayMap<>();

        /**
         * 注册对应路径的source
         *
         * @param source source点
         * @param clazz  对应的Activity路径栈。
         *               <b>栈的顺序是从栈底到栈顶</b>
         *               <b>如果Activity中有Fragment则Fragment也算一个栈</b>
         *               <b>需要注意的是一个Activity中必须只能显示一个Fragment {@link com.juxin.predestinate.bean.StackNode}</b>
         */
        public void register(String source, Class... clazz) {
            if (clazz == null || clazz.length == 0) {
                return;
            }
            int i = 1;
            StackNode previous = null;
            for (Class c : clazz) {
                StackNode node = new StackNode();
                node.stack = c.getName();
                node.previous = previous;
                previous = node;
                i++;
            }
            stackSourceMap.put(previous, source);
        }
    }
}
