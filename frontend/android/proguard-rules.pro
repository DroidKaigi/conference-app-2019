# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

## Extra

# Generate merge config list
-printconfiguration proguard-merged-config.txt
-printmapping proguard-merged.map

## General

# Do not remove annotations
-keepattributes *Annotation*
-keepattributes EnclosingMethod,Signature

# Replace source file attributes by SourceFile to reduce the size
# report system can de-obfuscate them
-renamesourcefileattribute SourceFile
# To see readable stacktraces
-keepattributes SourceFile,LineNumberTable

-dontwarn org.jetbrains.annotations.**

## Kotlin Coroutine stuff

# ServiceLoader-related stuff should be kept unless we use Dispatchers.Main
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# AFU stuff
-dontwarn kotlinx.atomicfu.**

## OkHttp

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

## Ktor

-dontwarn com.typesafe.**
-dontwarn org.slf4j.**
