-dontshrink
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-ignorewarnings
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.support.v4.app.Fragment{
    !private <methods>;

}
-keep public class com.android.vending.licensing.ILicensingService
-keepnames public class *

-keepclasseswithmembers class * { native <methods>; }
-keepclasseswithmembers class * { public <init>(android.content.Context); }
-keepclasseswithmembers class * { public <init>(android.content.Context, android.util.AttributeSet); }
-keepclasseswithmembers class * { public <init>(android.content.Context, android.util.AttributeSet, int); }

-keepclasseswithmembers class * extends android.view.View
-keepclasseswithmembers class * extends android.view.ViewGroup

-keep class com.zeus.**{
    *;
}

-keepattributes *Annotation*
-keepattributes Signature






