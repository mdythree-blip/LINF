package com.linf.hook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import java.util.HashMap;

public class MainHook implements IXposedHookLoadPackage {
    private static final String TARGET_PACKAGE = "com.rtk.app";

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals(TARGET_PACKAGE)) return;

        XposedBridge.log("LINF Hook: Loaded target package " + TARGET_PACKAGE);

        // 1. 设置入口 Hook
        hookSettingsEntry(lpparam);

        // 2. 广告去除 Hook
        hookAdRemoval(lpparam);

        // 3. 自定义回帖后缀 Hook
        hookCommentSuffix(lpparam);

        // 4. 家族管理自动化 Hook
        hookFamilyManagement(lpparam);

        // 5. 开屏自定义 Hook
        hookSplashCustomization(lpparam);
    }

    private void hookSettingsEntry(final LoadPackageParam lpparam) {
        // Hook SettingActivity 的 onClick，当点击“关于我们”时弹出我们的设置弹窗
        XposedHelpers.findAndHookMethod("com.rtk.app.main.Home5Activity.SettingActivity", lpparam.classLoader, "onClick", View.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                View view = (View) param.args[0];
                if (view.getId() == 2131300162) { // R.id.setting_aboutUs
                    Activity activity = (Activity) param.thisObject;
                    showLinfSettingsDialog(activity);
                    param.setResult(null); // 拦截原点击事件
                }
            }
        });
    }

    private void showLinfSettingsDialog(final Context context) {
        // 这里应当构建一个 AlertDialog，包含开关调节各项功能
        // 为了简化，我们通过日志输出模拟，实际开发中需使用反射或动态布局构建 UI
        XposedBridge.log("LINF Hook: Showing settings dialog");
        // AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // ... 构建 UI 并调用 ConfigManager 保存配置
    }

    private void hookAdRemoval(final LoadPackageParam lpparam) {
        // 1. 去除列表广告
        XposedHelpers.findAndHookMethod("com.rtk.app.adapter.HomePageItem1Adapter", lpparam.classLoader, "getItemViewType", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ConfigManager.loadConfig();
                if (!ConfigManager.isRemoveAds()) return;
                
                int result = (int) param.getResult();
                if (result == 3) { // 3 是广告类型
                    param.setResult(0); // 设为普通布局，防止 SDK 初始化失败
                }
            }
        });

        // 2. 去除开屏广告 (FirstActivity)
        // Hook D 方法，强制缩短等待时间
        XposedHelpers.findAndHookMethod("com.rtk.app.main.MainActivityPack.FirstActivity", lpparam.classLoader, "D", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                ConfigManager.loadConfig();
                if (!ConfigManager.isRemoveAds()) return;
                
                // 这里可以修改时长或直接跳转
                // 但为了不破坏初始化，我们 Hook 广告回调
            }
        });

        // Hook 广告监听器，模拟加载失败或直接跳过
        XposedHelpers.findAndHookMethod("com.rtk.app.main.MainActivityPack.FirstActivity", lpparam.classLoader, "onAdShow", "com.bytedance.sdk.openadsdk.TTAdSdk$SplashAdListener", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                ConfigManager.loadConfig();
                if (!ConfigManager.isRemoveAds()) return;
                
                // 模拟跳过点击
                XposedHelpers.callMethod(param.thisObject, "D");
            }
        });

        // 3. 过滤下载弹窗广告 (DownLoadDialog)
        XposedHelpers.findAndHookMethod("com.rtk.app.bean.DownloadTipsBean$DataBean", lpparam.classLoader, "getAd_status", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ConfigManager.loadConfig();
                if (ConfigManager.isRemoveAds()) {
                    param.setResult(0); // 强制广告状态为 0
                }
            }
        });
    }

    private void hookCommentSuffix(final LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod("com.rtk.app.main.comment.CommentActivity", lpparam.classLoader, "O", int[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                ConfigManager.loadConfig();
                if (!ConfigManager.isAutoSuffix()) return;

                int[] iArr = (int[]) param.args[0];
                if (iArr[0] != 1) return;

                Activity activity = (Activity) param.thisObject;
                TextView activityCommentsContent = (TextView) XposedHelpers.getObjectField(activity, "activityCommentsContent");
                String originalText = activityCommentsContent.getText().toString().trim();

                // 构造后缀: 换两行 + 粗体下划线 LINF修改
                // 注意：由于是发送到服务器，需要根据服务器是否支持 HTML 标签来决定格式。
                // 如果服务器支持 HTML，使用 <b><u>...</u></b>
                // 如果不支持，则只能发送纯文本。根据需求“粗体加下划线”，通常在 Hook 插件中是指在 UI 上显示或发送特定格式。
                // 考虑到这是一个 Android App，且需求提到“必须加上LINF修改”，我们先按纯文本加标识处理。
                // 构造后缀: 换两行 + 粗体下划线 LINF修改
                // 使用 HTML 标签以满足“粗体加下划线”的要求
                String suffixContent = ConfigManager.getSuffixContent();
                if (!suffixContent.contains("LINF修改")) {
                    suffixContent += " LINF修改";
                }
                String suffix = "\n\n<b><u>" + suffixContent + "</u></b>";
                
                // 暂时修改 EditText 内容以包含后缀进行发送
                activityCommentsContent.setText(originalText + suffix);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                // 发送后恢复原状（如果需要）
                // Activity activity = (Activity) param.thisObject;
                // TextView activityCommentsContent = (TextView) XposedHelpers.getObjectField(activity, "activityCommentsContent");
                // String currentText = activityCommentsContent.getText().toString();
                // if (currentText.contains("\n\n<b><u>")) {
                //     activityCommentsContent.setText(currentText.split("\n\n<b><u>")[0]);
                // }
            }
        });
    }

    private void hookFamilyManagement(final LoadPackageParam lpparam) {
        // 1. 家族申请列表一键同意 (FamilyApplyJoinListActivity)
        XposedHelpers.findAndHookMethod("com.rtk.app.main.family.FamilyApplyJoinListActivity", lpparam.classLoader, "initView", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ConfigManager.loadConfig();
                if (!ConfigManager.isAutoFamilyAgree()) return;

                final Activity activity = (Activity) param.thisObject;
                // 在右上角添加一键同意按钮
                addOneClickButton(activity, "一键同意", v -> {
                    Object adapter = XposedHelpers.getObjectField(activity, "s");
                    if (adapter != null) {
                        List<?> dataList = (List<?>) XposedHelpers.getObjectField(adapter, "f13132d");
                        if (dataList != null) {
                            for (int i = 0; i < dataList.size(); i++) {
                                XposedHelpers.callMethod(adapter, "k", dataList.get(i), i, null, null);
                            }
                        }
                    }
                });
            }
        });

        // 2. 帖子审核一键同意 (PostAuditListActivity)
        XposedHelpers.findAndHookMethod("com.rtk.app.main.HomeCommunityPack.PostAuditListActivity", lpparam.classLoader, "initView", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ConfigManager.loadConfig();
                if (!ConfigManager.isAutoFamilyAgree()) return;

                final Activity activity = (Activity) param.thisObject;
                addOneClickButton(activity, "一键审核", v -> {
                    // 获取当前 Fragment 的 Adapter 并循环调用审核逻辑
                    // 逻辑类似于家族申请
                });
            }
        });
    }

    private void addOneClickButton(Activity activity, String text, View.OnClickListener listener) {
        // 动态在 Activity 布局中寻找标题栏并添加按钮
        // 这是一个通用的辅助方法
        try {
            ViewGroup root = (ViewGroup) activity.findViewById(android.R.id.content);
            TextView btn = new TextView(activity);
            btn.setText(text);
            btn.setPadding(20, 10, 20, 10);
            btn.setOnClickListener(listener);
            // 简单定位在右上角
            root.addView(btn);
        } catch (Exception e) {
            XposedBridge.log("LINF Hook: Failed to add button - " + e.getMessage());
        }
    }

    private void hookSplashCustomization(final LoadPackageParam lpparam) {
        // 1. 修改开屏时长 (FirstActivity)
        XposedHelpers.findAndHookMethod("com.rtk.app.main.MainActivityPack.FirstActivity", lpparam.classLoader, "D", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                ConfigManager.loadConfig();
                int duration = ConfigManager.getSplashDuration();
                // 这里的逻辑应当是修改 Handler.postDelayed 的时长参数
                // 或者直接通过反射修改广告配置类中的时长字段
            }
        });

        // 2. 替换开屏图片 (CoverActivity)
        XposedHelpers.findAndHookMethod("com.rtk.app.main.MainActivityPack.CoverActivity", lpparam.classLoader, "initView", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ConfigManager.loadConfig();
                if (!ConfigManager.isCustomSplashImage()) return;

                Activity activity = (Activity) param.thisObject;
                // 获取背景 ImageView 并替换图片
                // ImageView bg = (ImageView) XposedHelpers.getObjectField(activity, "coverImage");
                // if (bg != null) {
                //     bg.setImageBitmap(loadCustomBitmap());
                // }
            }
        });
    }
}
