package com.linf.hook;

import android.content.Context;
import android.content.SharedPreferences;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

public class ConfigManager {
    private static final String PREF_NAME = "linf_settings";
    private static XSharedPreferences xPrefs;
    private static SharedPreferences sharedPrefs;

    // 缓存变量
    private static boolean removeAds = true;
    private static boolean autoSuffix = true;
    private static String suffixContent = "LINF修改";
    private static boolean autoFamilyAgree = false;
    private static int splashDuration = 3;

    public static void loadConfig() {
        if (xPrefs == null) {
            xPrefs = new XSharedPreferences("com.linf.hook", PREF_NAME);
            xPrefs.makeWorldReadable();
        }
        xPrefs.reload();
        removeAds = xPrefs.getBoolean("remove_ads", true);
        autoSuffix = xPrefs.getBoolean("auto_suffix", true);
        suffixContent = xPrefs.getString("suffix_content", "LINF修改");
        autoFamilyAgree = xPrefs.getBoolean("auto_family_agree", false);
        splashDuration = xPrefs.getInt("splash_duration", 3);
    }

    public static void saveConfig(Context context) {
        if (sharedPrefs == null) {
            sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean("remove_ads", removeAds);
        editor.putBoolean("auto_suffix", autoSuffix);
        editor.putString("suffix_content", suffixContent);
        editor.putBoolean("auto_family_agree", autoFamilyAgree);
        editor.putInt("splash_duration", splashDuration);
        editor.apply();
    }

    public static void setBoolean(String key, boolean value) {
        switch (key) {
            case "remove_ads": removeAds = value; break;
            case "auto_suffix": autoSuffix = value; break;
            case "auto_family_agree": autoFamilyAgree = value; break;
        }
    }

    public static void setString(String key, String value) {
        if (key.equals("suffix_content")) suffixContent = value;
    }

    public static void setInt(String key, int value) {
        if (key.equals("splash_duration")) splashDuration = value;
    }

    public static boolean isRemoveAds() { return removeAds; }
    public static boolean isAutoSuffix() { return autoSuffix; }
    public static String getSuffixContent() { return suffixContent; }
    public static boolean isAutoFamilyAgree() { return autoFamilyAgree; }
    public static int getSplashDuration() { return splashDuration; }
}
