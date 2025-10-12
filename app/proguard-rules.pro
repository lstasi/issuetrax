# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep inherited services.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Keep generic signatures for Response types - CRITICAL for Retrofit
-keepattributes Signature
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep class retrofit2.Response { *; }

# Keep our API service interface explicitly to prevent method obfuscation issues
-keep interface com.issuetrax.app.data.api.GitHubApiService { *; }
-keep class com.issuetrax.app.data.api.GitHubApiService { *; }

# Keep all API model DTOs to prevent obfuscation issues
-keep class com.issuetrax.app.data.api.model.** { *; }

# Keep request/response classes for Retrofit
-keep class com.issuetrax.app.data.api.CreateReviewRequest { *; }
-keep class com.issuetrax.app.data.api.ReviewCommentRequest { *; }

# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# Kotlinx Serialization
-keepattributes InnerClasses # Needed for `getDeclaredClasses`.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
   static <1>$Companion Companion;
}
-if @kotlinx.serialization.Serializable class ** {
   static **$* *;
}
-keepnames class <2>$$serializer { # -keepnames suffices; class is kept when serializer() is kept.
   static <2>$$serializer INSTANCE;
}
-if @kotlinx.serialization.Serializable class ** {
   public static ** serializer();
}
-keepclassmembers class <1> {
   public static <1>$$serializer serializer();
}
-if @kotlinx.serialization.Serializable class ** {
   static kotlinx.serialization.KSerializer serializer();
}
-keepclassmembers class <1> {
   static kotlinx.serialization.KSerializer serializer();
}
