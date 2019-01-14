package io.github.droidkaigi.confsched2019

import android.graphics.Typeface
import android.os.Handler
import androidx.core.content.res.ResourcesCompat
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.github.droidkaigi.confsched2019.announcement.ui.subscribeAnnouncementTopic
import io.github.droidkaigi.confsched2019.di.createAppComponent
import io.github.droidkaigi.confsched2019.ext.android.changedForever
import io.github.droidkaigi.confsched2019.system.actioncreator.SystemActionCreator
import io.github.droidkaigi.confsched2019.system.store.SystemStore
import timber.log.Timber
import timber.log.debug
import javax.inject.Inject

open class App : DaggerApplication() {
    @Inject lateinit var systemStore: SystemStore
    @Inject lateinit var systemActionCreator: SystemActionCreator

    override fun onCreate() {
        super.onCreate()

        setupFont()
        setupEmojiCompat()
        setupFirestore()
        systemStore.systemProperty.changedForever {
            // listening
            subscribeAnnouncementTopic(it.lang)
        }
        systemActionCreator.setupSystem()
    }

    private fun setupFont() {
        // fetch font for cache
        ResourcesCompat.getFont(this, R.font.lekton, object : ResourcesCompat.FontCallback() {
            override fun onFontRetrievalFailed(reason: Int) {
                Timber.debug { "onFontRetrievalFailed$reason" }
            }

            override fun onFontRetrieved(typeface: Typeface) {
                // do nothing
            }
        }, Handler())
    }

    private fun setupFirestore() {
        FirebaseApp.initializeApp(this)
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .setPersistenceEnabled(true)
            .build()
        firestore.setFirestoreSettings(settings)
    }

    fun setupEmojiCompat() {
        val fontRequest = FontRequest(
            "com.google.android.gms.fonts",
            "com.google.android.gms",
            "Noto Color Emoji Compat",
            R.array.com_google_android_gms_fonts_certs
        )
        val config = FontRequestEmojiCompatConfig(applicationContext, fontRequest)
            .setReplaceAll(true)
            .registerInitCallback(object : EmojiCompat.InitCallback() {
                override fun onInitialized() {
                }

                override fun onFailed(throwable: Throwable?) {
                }
            })
        EmojiCompat.init(config)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return createAppComponent()
    }
}
