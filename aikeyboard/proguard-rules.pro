# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\AndroidSDK\sdk/tools/proguard/proguard-android.txt
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

#-keep class com.enliple.keyboard.** {public *;}

-dontwarn com.enliple.keyboard.CKeyboard
-keep class com.enliple.** { *; }
-keep class com.skplanet.pdp.sentinel.shuttle.** { *; }
-keep class com.criteo.** { *; }
-keep class org.apache.commons.lang3.** { *; }
-keep class com.google.** { *; }
-keep class com.mmc.** { *; }
-keep class com.rake.android.rkmetrics.** { *; }


#-dontwarn com.enliple.keyboard.CKeyboard
#-keep class com.enliple.keyboard.CKeyboard { *; }
#-keep class com.skplanet.pdp.sentinel.shuttle.** { *; }

