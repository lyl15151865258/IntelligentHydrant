# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\SDK/tools/proguard/proguard-android.txt
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
#-----------------混淆配置设定------------------------------------------------------------------------
-optimizationpasses 5                                                               #指定代码压缩级别
-dontusemixedcaseclassnames                                                        #不使用大小写混合
-dontskipnonpubliclibraryclasses                                                  #指定不忽略非公共类库
-dontpreverify                                                                      #不预校验，如果需要预校验，是-dontoptimize
-ignorewarnings                                                                     #屏蔽警告
-verbose                                                                             #混淆时记录日志
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*            #混淆时的算法

-keepattributes EnclosingMethod
-keepattributes InnerClasses

#-----------------生成日志数据，gradle build时在本项目根目录输出---------------------------------------
-dump class_files.txt            #apk包内所有class的内部结构
-printseeds seeds.txt            #未混淆的类和成员
-printusage unused.txt           #打印未被使用的代码
-printmapping mapping.txt        #混淆前后的映射

#-----------------混淆第三方jar包，如果出现Warning:Exception while processing task java.io.IOException: The same input jar [XXX] is specified twice应当去掉-----------------------------------------------------------------------
#-libraryjars libs/nineoldandroids-2.4.0.jar

#-----------------不需要混淆第三方类库------------------------------------------------------------------
#-----------------友盟社会化分享-------------------
-dontshrink
-dontoptimize
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-dontwarn com.facebook.**
-keep public class javax.**
-keep public class android.webkit.**
-dontwarn android.support.v4.**
-keep enum com.facebook.**
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public interface com.facebook.**
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**

-keep public class com.umeng.socialize.* {*;}


-keep class com.facebook.**
-keep class com.facebook.** { *; }
-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**
-keep class com.umeng.socialize.handler.**
-keep class com.umeng.socialize.handler.*
-keep class com.umeng.weixin.handler.**
-keep class com.umeng.weixin.handler.*
-keep class com.umeng.qq.handler.**
-keep class com.umeng.qq.handler.*
-keep class UMMoreHandler{*;}
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
-keep class im.yixin.sdk.api.YXMessage {*;}
-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}
-keep class com.tencent.mm.sdk.** {
   *;
}
-keep class com.tencent.mm.opensdk.** {
   *;
}
-keep class com.tencent.wxop.** {
   *;
}
-keep class com.tencent.mm.sdk.** {
   *;
}
-dontwarn twitter4j.**
-keep class twitter4j.** { *; }

-keep class com.tencent.** {*;}
-dontwarn com.tencent.**
-keep class com.kakao.** {*;}
-dontwarn com.kakao.**
-keep public class com.umeng.com.umeng.soexample.R$*{
    public static final int *;
}
-keep public class com.linkedin.android.mobilesdk.R$*{
    public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}
-keep class com.umeng.socialize.impl.ImageImpl {*;}
-keep class com.sina.** {*;}
-dontwarn com.sina.**
-keep class  com.alipay.share.sdk.** {
   *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep class com.linkedin.** { *; }
-keep class com.android.dingtalk.share.ddsharemodule.** { *; }
-keepattributes Signature


#-----------------极光推送-------------------
-dontoptimize
-dontpreverify
-keepattributes  EnclosingMethod,Signature
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

#==================gson && protobuf========================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}
-keep class com.google.protobuf.** {*;}

#---------------（可选）避免Log打印输出---------------
-assumenosideeffects class android.util.Log {
   public static *** v(...);
   public static *** d(...);
   public static *** i(...);
   public static *** w(...);
 }

#-----------------百度地图不需混淆的jar-------------------
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**

#-----------------过滤v4、v7包以及他们的子类--------------
-dontwarn android.support.**                                                #去掉警告
-keep class android.support.** { *; }                                       #过滤android.support.v4和v7
-keep interface android.support.** { *; }
-keep public class * extends android.support.**
-keep public class * extends android.app.Fragment

#-----------------butterknife-----------------------------
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#-----------------qiujuer-------------------
-keep class net.qiujuer.genius.** {*;}
-dontwarn net.qiujuer.genius.**

#-----------------glide-------------------
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#-----------------okhttp-------------------
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

#-----------------retrofit-------------------
-dontwarn okio.**
-dontwarn javax.annotation.**


#-----------------banner-------------------
-keep class com.youth.banner.** {
    *;
 }

#-----------------easing-------------------
-keep class com.daimajia.easing.** { *; }
-keep class com.daimajia.androidanimations.** { *; }
-keep interface com.daimajia.easing.** { *; }

#-----------------https://github.com/AriaLyy/Aria-------------------
-dontwarn com.arialyy.aria.**
-keep class com.arialyy.aria.**{*;}
-keep class **$$DownloadListenerProxy{ *; }
-keep class **$$UploadListenerProxy{ *; }
-keep class **$$DownloadGroupListenerProxy{ *; }
-keepclasseswithmembernames class * {
    @Download.* <methods>;
    @Upload.* <methods>;
    @DownloadGroup.* <methods>;
}

#-----------------萤石云视频-------------------------------------------------------------------
-dontwarn com.ezviz.player.**
-keep class com.ezviz.player.** { *;}

-dontwarn com.ezviz.statistics.**
-keep class com.ezviz.statistics.** { *;}

-dontwarn com.ezviz.stream.**
-keep class com.ezviz.stream.** { *;}

-dontwarn com.ezviz.hcnetsdk.**
-keep class com.ezviz.hcnetsdk.** { *;}

-dontwarn com.ezviz.npcsdk.**
-keep class com.ezviz.npcsdk.** { *;}

-dontwarn com.ezviz.opensdk.**
-keep class com.ezviz.opensdk.** { *;}

-dontwarn com.hik.**
-keep class com.hik.** { *;}

-dontwarn com.hikvision.**
-keep class com.hikvision.** { *;}

-dontwarn com.videogo.**
-keep class com.videogo.** { *;}

-dontwarn org.MediaPlayer.PlayM4.**
-keep class org.MediaPlayer.PlayM4.** { *;}

-dontwarn com.google.zxing.**
-keep class com.google.zxing.** { *;}

#Gson混淆配置
-keepattributes Annotation
-keep class sun.misc.Unsafe { *; }
-keep class com.idea.fifaalarmclock.entity.*
-keep class com.google.gson.stream.* { *; }

#引用mars的xlog，混淆配置
-keep class com.tencent.mars.** {
  public protected private *;
}

#-----------------不需要混淆系统组件等-------------------------------------------------------------------
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keep class cn.njmeter.njmeter.model.**{*;}                                   #过滤掉自己编写的实体类

#----------------保护指定的类和类的成员，但条件是所有指定的类和类成员是要存在------------------------------------
#-keepclasseswithmembernames class * {
#    public <init>(android.content.Context, android.util.AttributeSet);
#}


#----------------保持自定义控件类不被混淆------------------------------------------------------------------------
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}


#----------------Mob SMS短信验证码------------------------------------------------------------------------
-keep class com.mob.**{*;}
-keep class cn.smssdk.**{*;}
-dontwarn com.mob.**