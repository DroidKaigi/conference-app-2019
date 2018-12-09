package dependencies

object Dep {
    object Kotlin {
        val version = "1.3.11"
        val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        val serializationPlugin = "org.jetbrains.kotlin:kotlin-serialization:$version"
        val stdlibCommon = "org.jetbrains.kotlin:kotlin-stdlib-common:$version"
        val stdlibJvm = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$version"
        val coroutinesVersion = "1.0.1"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"
        val androidCoroutinesDispatcher = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion"
        val coroutinesRx2 = "org.jetbrains.kotlinx:kotlinx-coroutines-rx2:$coroutinesVersion"
        val coroutinesReactive = "org.jetbrains.kotlinx:kotlinx-coroutines-reactive:$coroutinesVersion"
        val androidCoroutines = "net.devrieze:android-coroutines:0.7.0"
        val coroutinesPlayServices = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$coroutinesVersion"
        val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.8.1-rc13"
        val jvmModuleTest = "org.jetbrains.kotlin:kotlin-test"
        val jvmModuleTestJunit = "org.jetbrains.kotlin:kotlin-test-junit"
        val commonModuleTest = "org.jetbrains.kotlin:kotlin-test-common"
        val commonModuleTestAnnotations = "org.jetbrains.kotlin:kotlin-test-annotations-common"
    }

    object RxJava {
    }

    object Arch {
        val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:2.0.0"
        val lifecycleLiveData = "androidx.lifecycle:lifecycle-livedata:2.0.0"
        val coreTesting = "androidx.arch.core:core-testing:2.0.0"

        object Room {
            val version = "2.1.0-alpha03"
            val compiler = "androidx.room:room-compiler:$version"
            val runtime = "androidx.room:room-runtime:$version"
            val rxJava = "androidx.room:room-rxjava2:$version"
        }

        object Navigation {
            val version = "1.0.0-alpha08"
            val runtime = "android.arch.navigation:navigation-runtime:$version"
            val runtimeKtx = "android.arch.navigation:navigation-runtime-ktx:$version"
            val fragment = "android.arch.navigation:navigation-fragment:$version"
            val ui = "android.arch.navigation:navigation-ui:$version"
            val fragmentKtx = "android.arch.navigation:navigation-fragment-ktx:$version"
            val uiKtx = "android.arch.navigation:navigation-ui-ktx:$version"
            val safeArgsPlugin = "android.arch.navigation:navigation-safe-args-gradle-plugin:$version"
            val testingKtx = "android.arch.navigation:navigation-testing-ktx:$version"
        }
    }

    object Firebase {
        val core = "com.google.firebase:firebase-core:16.0.4"
        val fireStore = "com.google.firebase:firebase-firestore:17.1.3"
        val auth = "com.google.firebase:firebase-auth:16.0.5"
    }

    object PlayServices {
        val plugin = "com.google.gms:google-services:4.1.0"
        val auth = "com.google.android.gms:play-services-auth:16.0.1"
    }

    object Dagger {
        val version = "2.16"
        val core = "com.google.dagger:dagger:$version"
        val compiler = "com.google.dagger:dagger-compiler:$version"
        val androidSupport = "com.google.dagger:dagger-android-support:$version"
        val android = "com.google.dagger:dagger-android:$version"
        val androidProcessor = "com.google.dagger:dagger-android-processor:$version"
        val assistedInjectAnnotations = "com.squareup.inject:assisted-inject-annotations-dagger2:0.3.0"
        val assistedInjectProcessor = "com.squareup.inject:assisted-inject-processor-dagger2:0.3.0"
    }

    object Android {
        val gradlePlugin = "com.android.tools.build:gradle:3.2.0"
        val appCompat = "androidx.appcompat:appcompat:1.0.0"
        val recyclerView = "androidx.recyclerview:recyclerview:1.0.0"
        val constraint = "androidx.constraintlayout:constraintlayout:1.1.2"
        val testRunner = "androidx.test:runner:1.1.0-alpha3"
        val emoji = "androidx.emoji:emoji-appcompat:1.0.0"
        val espressoCore = "androidx.test.espresso:espresso-core:3.1.0-alpha3"
        val design = "com.google.android.material:material:1.1.0-alpha01"
    }

    object Ktor {
        val clientAndroid = "io.ktor:ktor-client-android:1.0.1"
        val jsonJvm = "io.ktor:ktor-client-json-jvm:1.0.1"
    }

    object AndroidKtx {
        val core = "androidx.core:core-ktx:1.0.0-alpha1"
    }

    object OkHttp {
        val client = "com.squareup.okhttp3:okhttp:3.11.0"
    }

    val liveDataKtx = "com.shopify:livedata-ktx:2.0.1"

    object LeakCanary {
        val leakCanary = "com.squareup.leakcanary:leakcanary-android:1.6.2"
        val leakCanaryNoOp = "com.squareup.leakcanary:leakcanary-android-no-op:1.6.2"
        val leakCanaryFragment = "com.squareup.leakcanary:leakcanary-support-fragment:1.6.2"
    }

    object Stetho {
        val stetho = "com.facebook.stetho:stetho:1.5.0"
    }

    object Hyperion {
        val hyperionPlugins = listOf(
            "com.willowtreeapps.hyperion:hyperion-core:0.9.24",
            "com.willowtreeapps.hyperion:hyperion-attr:0.9.24",
            "com.willowtreeapps.hyperion:hyperion-measurement:0.9.24",
            "com.willowtreeapps.hyperion:hyperion-disk:0.9.24",
            "com.willowtreeapps.hyperion:hyperion-recorder:0.9.24",
            "com.willowtreeapps.hyperion:hyperion-phoenix:0.9.24",
            "com.willowtreeapps.hyperion:hyperion-crash:0.9.24",
            "com.willowtreeapps.hyperion:hyperion-shared-preferences:0.9.24",
            "com.willowtreeapps.hyperion:hyperion-geiger-counter:0.9.24",
            "com.willowtreeapps.hyperion:hyperion-build-config:0.9.24"
        )
    }

    object Groupie {
        val groupie = "com.xwray:groupie:2.1.0"
        val databinding = "com.xwray:groupie-databinding:2.1.0"
    }

    object Klock {
        val common = "com.soywiz:klock:1.0.0"
        val jvm = "com.soywiz:klock-jvm:1.0.0"
    }

    object KotlinLogging {
        val kotlinLogging = "io.github.microutils:kotlin-logging:1.6.20"
    }

    val junit = "junit:junit:4.12"

    object KotlinTest {
        val assertions = "io.kotlintest:kotlintest-assertions:3.1.10"
    }

    object MockK {
        val jvm = "io.mockk:mockk:1.8.13.kotlin13"
        val common = "io.mockk:mockk-common:1.8.13.kotlin13"
    }

    object InjectedVmProvider {
        val injectedVmProvider = "me.tatarka.injectedvmprovider:injectedvmprovider:2.0"
        val extension = "me.tatarka.injectedvmprovider:injectedvmprovider-extensions:2.0"
        val ktx = "me.tatarka.injectedvmprovider:injectedvmprovider-ktx:2.0"
    }

    object Picasso {
        val picasso = "com.squareup.picasso:picasso:2.71828"
        val picassoTransformation = "jp.wasabeef:picasso-transformations:2.2.1"
    }
}
