# ProGuard rules for Zenith26 Fest Android Application
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.zenith26.fest.data.model.** { *; }
