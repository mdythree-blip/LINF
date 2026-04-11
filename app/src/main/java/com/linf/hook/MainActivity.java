package com.linf.hook;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 简单的 UI，显示插件信息
        TextView textView = new TextView(this);
        textView.setText("LINF Hook Plugin v1.0\n\n" +
                "功能：\n" +
                "• 去除开屏与下载广告\n" +
                "• 自定义回帖后缀\n" +
                "• 家族管理自动化\n" +
                "• 开屏自定义\n\n" +
                "此插件需要 LSPosed 或免 Root 框架支持。\n" +
                "点击\"关于我们\"打开设置面板。");
        textView.setPadding(40, 40, 40, 40);
        setContentView(textView);
    }
}
