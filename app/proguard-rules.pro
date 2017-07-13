# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/rabiu/Library/Android/sdk/tools/proguard/proguard-android.txt
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

# Save the obfuscation mapping to a file, so we can de-obfuscate any stack
# traces later on. Keep a fixed source file attribute and all line number
# tables to get line numbers in the stack traces.
# You can comment this out if you're not interested in stack traces.

# Save the obfuscation mapping to a file, so we can de-obfuscate any stack
# traces later on. Keep a fixed source file attribute and all line number
# tables to get line numbers in the stack traces.
# You can comment this out if you're not interested in stack traces.


-dontwarn okio.**
-dontwarn retrofit2.Platform$Java8

-dontwarn org.hamcrest.**
-dontwarn android.test.**
-dontwarn android.support.test.**

-keep class org.hamcrest.** {
   *;
}

-keep class org.junit.** { *; }
-dontwarn org.junit.**

-keep class junit.** { *; }
-dontwarn junit.**

-keep class sun.misc.** { *; }
-dontwarn sun.misc.**
-dontwarn com.squareup.javawriter.JavaWriter

-dontwarn android.databinding.**
-keep class android.databinding.** { *; }

-keep class dmax.dialog.** { *;}

#-keep class com.google.zxing.client.android.** {*;}
#-dontwarn  com.google.zxing.client.android.**


#start
#
#-keep class com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner.** { *;}
#
## EventBus 3.0
#-keepclassmembers class ** {
#    public void onEvent*(**);
#}
#
## EventBus 3.0 annotation
#-keepclassmembers class * {
#    @de.greenrobot.event.Subscribe <methods>;
#}
#-keep enum de.greenrobot.event.ThreadMode { *; }
#
#-keepattributes *Annotation*
#
## Understand the @Keep support annotation.
#-keep class android.support.annotation.Keep
#
#-keep @android.support.annotation.Keep class * {*;}
#
#-keepclasseswithmembers class * {
#    @android.support.annotation.Keep <methods>;
#}
#
#-keepclasseswithmembers class * {
#    @android.support.annotation.Keep <fields>;
#}
#
#-keepclasseswithmembers class * {
#    @android.support.annotation.Keep <init>(...);
#}

#end here

# Your library may contain more items that need to be preserved;
# typically classes that are dynamically created using Class.forName:

# -keep public class mypackage.MyClass
# -keep public interface mypackage.MyInterface
# -keep public class * implements mypackage.MyInterface