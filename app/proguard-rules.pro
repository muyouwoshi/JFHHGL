# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontoptimize
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-dontwarn
-dontnote

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

#libraryjars   libs/android-support-v4.jar
-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment

-keep  public class java.util.HashMap {
	public <methods>;
}
-keep  public class java.lang.String {
	public <methods>;
}
-keep  public class java.util.List {
	public <methods>;
}
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keep public class com.juxin.predestinate.R$*{
    public static final int *;
}

-dontwarn freemarker.**

# 打release包时剔除日志打印
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int d(...);
    public static int w(...);
    public static int v(...);
    public static int i(...);
}

# ==============应用内交互界面混淆处理start============
# JS交互
-keep class com.juxin.predestinate.module.logic.invoke.WebAppInterface {*;}
# CMD反射
-keepclassmembers public class com.juxin.predestinate.module.logic.invoke.Invoker$Invoke {*;}
# 表情映射
-keepclassmembers public class com.juxin.predestinate.module.local.msgview.chatview.input.ChatSmile {*;}
# 聊天面板
-keep class * extends java.util.logging.Formatter
-keep public class com.juxin.predestinate.module.local.msgview.ChatAdapter$ChatInstance
-keepclassmembers class * extends com.juxin.predestinate.module.local.msgview.chatview.ChatPanel {
    public <init>(android.content.Context, com.juxin.predestinate.module.local.msgview.ChatAdapter$ChatInstance, boolean);
}

# =====x5内核=====
-dontwarn com.tencent.smtt.**
-dontwarn com.tencent.smtt.sdk.WebChromeClient.**
-dontwarn com.tencent.smtt.export.external.DexLoader.**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement.**
-keep class com.tencent.smtt.**  {* ;}
-keep class com.tencent.tbs.video.interfaces.**  {* ;}
-keep class com.tencent.smtt.sdk.WebChromeClient$FileChooserParams {*;}

# FastJson
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# Glide okhttp
-keep class com.bumptech.glide.integration.okhttp3.OkHttpGlideModule

# okhttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# retrofit2
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#gif
-dontwarn pl.droidsonroids.gif.**
-keep public class pl.droidsonroids.gif.GifIOException{<init>(int);}
-keep class pl.droidsonroids.gif.GifInfoHandle{<init>(long,int,int,int);}

# leackcanary
-dontwarn com.squareup.leakcanary.**
-keep class com.squareup.leakcanary.** { *; }

# ProGuard configurations for Bugtags
-keepattributes LineNumberTable,SourceFile
-keep class com.bugtags.library.** {*;}
-dontwarn com.bugtags.library.**
-keep class io.bugtags.** {*;}
-dontwarn io.bugtags.**
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
# End Bugtags

# ==============支付================
#银联
-dontwarn com.unionpay.**
-dontnote com.unionpay.**
-dontwarn cn.gov.pbc.tsm.**
-dontwarn com.UCMobile..**

-keep class com.unionpay.** { *;}
-keep class cn.gov.pbc.tsm.** { *;}
-keep class com.UCMobile.** { *;}
#微信
#-libraryjars libs/libammsdk.jar
-dontwarn com.tencent.**
-keep class com.tencent.** { *; }
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage { *;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}

#支付宝
-dontwarn com.alipay.**
-dontnote com.alipay.**
-keep class com.alipay.**  { *;}

-dontnote com.ta.**
-keep class com.ta.** {*;}
-keep class com.ut.** {*;}

-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}

-keep class com.upay.billing.** { *; }
-dontwarn com.upay.billing.**

-dontwarn com.switfpass.pay.**
-dontnote com.switfpass.pay.**
-keep class com.switfpass.pay.** { *; }

-dontwarn eposp.wtf_library.**
-dontnote eposp.wtf_library.**
-keep class eposp.wtf_library.** { *; }

# ==============支付 end================
# ==============第三方支付================
#保持某包下所有类不混淆
-keep class com.activity.**{*;}
-keep class bfb.weixin.pay.**{*;}

-dontwarn com.gather.flood.system.*
-dontwarn com.switfpass.pay.activity.*
-dontwarn com.gather.flood.phone.*
-dontwarn com.switfpass.pay.activity.zxing.decoding.*
-dontwarn com.gather.flood.system.*
-dontwarn com.switfpass.pay.utils.*
-dontwarn com.alipay.android.phone.mrpc.core.*
-dontwarn com.switfpass.pay.activity.zxing.*
-dontwarn com.switfpass.pay.service.*
-dontwarn com.switfpass.pay.activity.zxing.camera.*
-dontwarn com.tm.pay.*

-keep class com.gather.flood.system.**{*;}
-keep class com.switfpass.pay.activity.**{*;}
-keep class com.gather.flood.phone.**{*;}
-keep class com.switfpass.pay.activity.zxing.decoding.**{*;}
-keep class com.gather.flood.system.**{*;}
-keep class com.switfpass.pay.utils.**{*;}
-keep class com.alipay.android.phone.mrpc.core.**{*;}
-keep class com.switfpass.pay.activity.zxing.**{*;}
-keep class com.switfpass.pay.service.**{*;}
-keep class com.switfpass.pay.activity.zxing.camera.**{*;}
-keep class com.tm.pay.**{*;}

-dontwarn com.tm.plugin.alipay.*
-keep class com.tm.plugin.alipay.**{*;}

-keep class com.tencent.** { *;}
-dontwarn com.tencent.*

-keepclasseswithmembers,allowshrinking class * {
    public <init>(android.content.Context,android.util.AttributeSet);
}

-keepclasseswithmembers,allowshrinking class * {
    public <init>(android.content.Context,android.util.AttributeSet,int);
}

-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclasseswithmembers,allowshrinking class * {
    native <methods>;
}

-dontwarn com.sun.crypto.provider.*
-keep class com.sun.crypto.provider.**{*;}

-dontwarn com.third.wa5.sdk.*
-keep class com.third.wa5.sdk.**{*;}


-dontwarn com.zy.*
-keep class com.zy.**{*;}
# ==============第三方支付 end================

# ===============阿里云httpdns start=================
-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}
# ===============阿里云httpdns end=================

# fast-json解析类混淆处理
-keep class com.juxin.predestinate.module.local.statistics.AppInfo{*;}
-keep class com.juxin.predestinate.bean.my.RedPackage{*;}
-keep class com.juxin.predestinate.ui.live.bean.EnterLiveBean{*;}
-keep class com.juxin.predestinate.ui.live.bean.LiveSearchBean{*;}

# ProGuard configurations for NetworkBench Lens
-keepattributes SourceFile,LineNumberTable
-keep class com.networkbench.** { *; }
-dontwarn com.networkbench.**
-keepattributes Exceptions, Signature, InnerClasses
# End NetworkBench Lens

# BaseRecyclerViewAdapterHelper start
-keep class com.chad.library.adapter.** {
*;
}
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers  class **$** extends com.chad.library.adapter.base.BaseViewHolder {
     <init>(...);
}
# BaseRecyclerViewAdapterHelper end

# ===============微信分享  start=================
-keep class com.tencent.mm.opensdk.** {*;}
-keep class com.tencent.wxop.** {*;}
-keep class com.tencent.mm.sdk.** {*;}
# ===============微信分享  end=================