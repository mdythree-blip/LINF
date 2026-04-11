# XPosed API
-keep class de.robv.android.xposed.** { *; }
-keepclassmembers class de.robv.android.xposed.** { *; }

# Hook Plugin
-keep class com.linf.hook.** { *; }
-keepclassmembers class com.linf.hook.** { *; }

# General rules
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
