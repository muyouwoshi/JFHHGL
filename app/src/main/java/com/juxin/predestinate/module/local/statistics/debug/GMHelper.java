package com.juxin.predestinate.module.local.statistics.debug;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.utils.TypeConvertUtil;
import com.juxin.predestinate.BuildConfig;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.custom.SimpleTipDialog;
import com.juxin.predestinate.module.logic.config.FinalKey;
import com.juxin.predestinate.module.logic.config.Hosts;
import com.juxin.predestinate.module.logic.socket.IMProxy;
import com.juxin.predestinate.module.util.PickerDialogUtil;
import com.juxin.predestinate.module.util.UIShow;

/**
 * GM命令操作工具类
 * Created by ZRP on 2017/8/4.
 */
public class GMHelper {

    // GM命令start字符串
    private static final String GM_START_KEY = "{";
    // GM命令end字符串
    private static final String GM_END_KEY = "}";
    // GM命令参数切割字符串
    private static final String GM_SPLIT = "%";

    // 打开彩蛋页面
    private static final String GM_CMD_OPEN_EGG = "EGG";
    // 打开对应uid用户个人资料
    private static final String GM_CMD_OPEN_USER = "USER";
    // 打开对应uid用户私聊页面
    private static final String GM_CMD_OPEN_CHAT = "CHAT";
    // 正/测式服地址切换
    private static final String GM_CMD_SWITCH_SERVER = "SWITCH";
    // 日志打印切换
    private static final String GM_CMD_SWITCH_LOG = "LOG";

    /**
     * 手动执行GM命令
     *
     * @param editText   被监听GM命令的editText
     * @param invokeView 点击之后执行GM命令的view
     */
    public static void gmInvokeManual(final EditText editText, View invokeView) {
        if (editText == null || invokeView == null) return;

        invokeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gmStringAnalysis(editText.getText().toString());
            }
        });
    }

    /**
     * 监测editText输入，自动执行GM命令
     *
     * @param editText 被监听GM命令的editText
     */
    public static void gmInvokeAuto(EditText editText) {
        if (editText == null) return;
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                gmStringAnalysis(s.toString());
            }
        });
    }

    /**
     * 分析并执行gm命令
     *
     * @param gmString gm命令字符串
     */
    private static void gmStringAnalysis(String gmString) {
        if (TextUtils.isEmpty(gmString)) return;
        if (!gmString.startsWith(GM_START_KEY) || !gmString.endsWith(GM_END_KEY)) return;
        gmString = gmString.replace(GM_START_KEY, "").replace(GM_END_KEY, "");

        String cmd = gmString;
        String param = "";
        try {
            // cmd默认只带一个参数，通过GM_SPLIT进行切割
            if (gmString.contains(GM_SPLIT)) {
                cmd = gmString.split(GM_SPLIT)[0];
                param = gmString.split(GM_SPLIT)[1];
            }
            switch (cmd) {
                case GM_CMD_OPEN_EGG:
                    UIShow.showSearchTestActivity((Activity) App.getActivity());
                    break;
                case GM_CMD_OPEN_USER:
                    if (TextUtils.isEmpty(param)) return;
                    UIShow.showCheckOtherInfoAct(App.getActivity(), TypeConvertUtil.toLong(param));
                    break;
                case GM_CMD_OPEN_CHAT:
                    if (TextUtils.isEmpty(param)) return;
                    UIShow.showPrivateChatAct(App.getActivity(), TypeConvertUtil.toLong(param), null);
                    break;
                case GM_CMD_SWITCH_SERVER:
                    switchServerType();
                    break;
                case GM_CMD_SWITCH_LOG:
                    switchLogType();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            PToast.showLong(e.toString());
        }
    }

    /**
     * 切换客户端服务器类型
     */
    private static void switchServerType() {
        final boolean isTestServer = Hosts.SERVER_TYPE == 1;//是否为测试服
        simulateExitDialog("当前为" + (isTestServer ? "测试服" : "正式服") + "，是否切换到" + (isTestServer ? "正式服" : "测试服") + "？",
                "点击确定之后应用将关闭，重进应用才可切换成功", new Runnable() {
                    @Override
                    public void run() {
                        ModuleMgr.getLoginMgr().logout();//退出当前帐号
                        IMProxy.getInstance().reSetHostPort(Hosts.TCP_HOST[isTestServer ? 0 : 1],
                                Integer.valueOf(Hosts.TCP_PORT[isTestServer ? 0 : 1]));
                        PSP.getInstance().put(Hosts.SERVER_TYPE_KEY, isTestServer ? 0 : 1);
                    }
                });
    }

    /**
     * 切换客户端日志打印，以方便测试人员配合排查异常
     */
    private static void switchLogType() {
        final boolean canLog = PSP.getInstance().getBoolean(FinalKey.GM_PRINT_DEBUG_LOG, BuildConfig.DEBUG);
        simulateExitDialog("当前日志打印已" + (canLog ? "打开" : "关闭") + "，是否" + (canLog ? "关闭" : "打开") + "？",
                "点击确定之后应用将关闭，重进应用才可切换成功", new Runnable() {
                    @Override
                    public void run() {
                        PSP.getInstance().put(FinalKey.GM_PRINT_DEBUG_LOG, !canLog);
                    }
                });
    }

    /**
     * 弹出退出应用的弹窗，以激活当前GM命令操作
     *
     * @param title    弹窗标题
     * @param content  弹窗提示内容
     * @param runnable 点击确定之后额外执行的操作
     */
    private static void simulateExitDialog(final String title, final String content, final Runnable runnable) {
        MsgMgr.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    PickerDialogUtil.showTipDialogCancelBack((FragmentActivity) App.getActivity(), new SimpleTipDialog.ConfirmListener() {
                                @Override
                                public void onCancel() {
                                }

                                @Override
                                public void onSubmit() {
                                    if (runnable != null) runnable.run();
                                    UIShow.simulateExit(App.getActivity());
                                }
                            },
                            content, title, "", "确定", false, false);
                } catch (Exception e) {
                    PLogger.printThrowable(e);
                }
            }
        });
    }
}
