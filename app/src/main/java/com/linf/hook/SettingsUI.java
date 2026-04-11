package com.linf.hook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class SettingsUI {

    public static void showSettingsDialog(final Context context) {
        ConfigManager.loadConfig();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("LINF 客户端功能设置");

        ScrollView scrollView = new ScrollView(context);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = 40;
        layout.setPadding(padding, padding, padding, padding);

        // 1. 广告去除开关
        CheckBox cbRemoveAds = createCheckBox(context, "去除开屏与下载广告", ConfigManager.isRemoveAds());
        cbRemoveAds.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ConfigManager.setBoolean("remove_ads", isChecked);
        });
        layout.addView(cbRemoveAds);

        // 2. 回帖后缀开关
        CheckBox cbAutoSuffix = createCheckBox(context, "自定义回帖后缀", ConfigManager.isAutoSuffix());
        cbAutoSuffix.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ConfigManager.setBoolean("auto_suffix", isChecked);
        });
        layout.addView(cbAutoSuffix);

        // 3. 后缀内容编辑
        EditText etSuffix = new EditText(context);
        etSuffix.setHint("后缀内容 (默认: LINF修改)");
        etSuffix.setText(ConfigManager.getSuffixContent());
        layout.addView(etSuffix);

        // 4. 家族自动化开关
        CheckBox cbAutoFamily = createCheckBox(context, "自动同意家族申请/审核", ConfigManager.isAutoFamilyAgree());
        cbAutoFamily.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ConfigManager.setBoolean("auto_family_agree", isChecked);
        });
        layout.addView(cbAutoFamily);

        TextView tvFamilyTip = new TextView(context);
        tvFamilyTip.setText("注：开启后在申请/审核界面右上角点击一键处理");
        tvFamilyTip.setTextSize(12);
        layout.addView(tvFamilyTip);

        // 5. 开屏时长
        EditText etSplashTime = new EditText(context);
        etSplashTime.setHint("开屏时长 (秒)");
        etSplashTime.setText(String.valueOf(ConfigManager.getSplashDuration()));
        layout.addView(etSplashTime);

        scrollView.addView(layout);
        builder.setView(scrollView);

        builder.setPositiveButton("保存", (dialog, which) -> {
            ConfigManager.setString("suffix_content", etSuffix.getText().toString());
            try {
                ConfigManager.setInt("splash_duration", Integer.parseInt(etSplashTime.getText().toString()));
            } catch (Exception ignored) {}
            ConfigManager.saveConfig(context);
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private static CheckBox createCheckBox(Context context, String text, boolean checked) {
        CheckBox cb = new CheckBox(context);
        cb.setText(text);
        cb.setChecked(checked);
        return cb;
    }
}
