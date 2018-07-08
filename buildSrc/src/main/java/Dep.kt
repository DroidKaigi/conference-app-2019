package dependencies

private object Versions {
    val retrofit = "2.4.0"
    val kotlin = "1.2.50"
    val stetho = "1.5.0"
    val aac = "2.0.0-beta01"
    val dagger = "2.16"
}

object Dep {
    object Kotlin {
        object Stdlib {
            val jdk = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
        }

        val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:0.23.3"
        val serialization =  "org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.5.1"
    }

    object Arch {
        val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.aac}"
        val lifecycleLiveData = "androidx.lifecycle:lifecycle-livedata:${Versions.aac}"
        val roomCompiler = "androidx.room:room-compiler:${Versions.aac}"
        val roomRuntime = "androidx.room:room-runtime:${Versions.aac}"
    }

    object Dagger {
        val core = "com.google.dagger:dagger:${Versions.dagger}"
        val compiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
        val android = "com.google.dagger:dagger-android:${Versions.dagger}"
        val androidSupport = "com.google.dagger:dagger-android-support:${Versions.dagger}"
        val androidProcessor = "com.google.dagger:dagger-android-processor:${Versions.dagger}"
    }

    object Android {
        val gradlePlugin = "com.android.tools.build:gradle:3.2.0-beta01"
        val appCompat = "androidx.appcompat:appcompat:1.0.0-beta01"
        val constraint = "androidx.constraintlayout:constraintlayout:1.1.2"
        val testRunner = "androidx.test:runner:1.1.0-alpha3"
        val espressoCore = "androidx.test.espresso:espresso-core:3.1.0-alpha3"
    }

    object Retrofit {
        val client = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        val adapterKotlinCoroutines = "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-experimental-adapter:1.0.0"
        val converterKotlinxSerialization = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.0.1"
    }

    val threetenabp = "com.jakewharton.threetenabp:threetenabp:1.0.5"
    object Stetho {
        val stetho = "com.facebook.stetho:stetho:${Versions.stetho}"
    }

    val junit = "junit:junit:4.12"
}
