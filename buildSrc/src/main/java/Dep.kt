package dependencies

private object Versions {
    val androidCompileSdkVersion = 28
    val androidMinSdkVersion = 21
    val retrofit = "2.4.0"
    val kotlin = "1.3.10"
    val stetho = "1.5.0"
    val aac = "2.0.0"
    val dagger = "2.16"
    val coroutines = "1.0.0"
    val navigation = "1.0.0-alpha06"
}

object Dep {
    object Kotlin {
        val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        val serializationPlugin = "org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}"
        val stdlibCommon = "org.jetbrains.kotlin:kotlin-stdlib-common:${Versions.kotlin}"
        val stdlibJvm = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
        val androidCoroutinesDispatcher = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
        val coroutinesRx2 = "org.jetbrains.kotlinx:kotlinx-coroutines-rx2:${Versions.coroutines}"
        val coroutinesReactive = "org.jetbrains.kotlinx:kotlinx-coroutines-reactive:${Versions.coroutines}"
        val androidCoroutines = "net.devrieze:android-coroutines:0.7.0"
        val coroutinesPlayServices = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.coroutines}"
        val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.8.1-rc13"
        val jvmModuleTest = "org.jetbrains.kotlin:kotlin-test"
        val jvmModuleTestJunit = "org.jetbrains.kotlin:kotlin-test-junit"
        val commonModuleTest = "org.jetbrains.kotlin:kotlin-test-common"
        val commonModuleTestAnnotations = "org.jetbrains.kotlin:kotlin-test-annotations-common"
    }

    object RxJava {
    }

    object Arch {
        val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.aac}"
        val lifecycleLiveData = "androidx.lifecycle:lifecycle-livedata:${Versions.aac}"
        val roomCompiler = "androidx.room:room-compiler:${Versions.aac}"
        val roomRuntime = "androidx.room:room-runtime:${Versions.aac}"
        val roomRxJava = "androidx.room:room-rxjava2:${Versions.aac}"

        object Navigation {
            val runtime = "android.arch.navigation:navigation-runtime:${Versions.navigation}"
            val runtimeKtx = "android.arch.navigation:navigation-runtime-ktx:${Versions.navigation}"
            val fragment = "android.arch.navigation:navigation-fragment:${Versions.navigation}"
            val ui = "android.arch.navigation:navigation-ui:${Versions.navigation}"
            val fragmentKtx = "android.arch.navigation:navigation-fragment-ktx:${Versions.navigation}"
            val uiKtx = "android.arch.navigation:navigation-ui-ktx:${Versions.navigation}"
            val safeArgsPlugin = "android.arch.navigation:navigation-safe-args-gradle-plugin:${Versions.navigation}"
            val testingKtx = "android.arch.navigation:navigation-testing-ktx:${Versions.navigation}"
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
        val core = "com.google.dagger:dagger:${Versions.dagger}"
        val compiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
        val androidSupport = "com.google.dagger:dagger-android-support:${Versions.dagger}"
        val android = "com.google.dagger:dagger-android:${Versions.dagger}"
        val androidProcessor = "com.google.dagger:dagger-android-processor:${Versions.dagger}"
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

    object Retrofit {
        val client = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        val adapterKotlinCoroutines = "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2"
        val converterKotlinxSerialization = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.1.0"
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
        val stetho = "com.facebook.stetho:stetho:${Versions.stetho}"
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
}
