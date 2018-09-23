package io.github.droidkaigi.confsched2019.ext.android

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.channels.ReceiveChannel

@MainThread
fun <T> ReceiveChannel<T>.toLiveData(defaultValue: T? = null):
        LiveData<T> = object : LiveData<T>(),
        CoroutineScope by GlobalScope {
    lateinit var job: Job
    init {
        if (defaultValue != null) {
            value = defaultValue
        }
    }

    override fun onActive() {
        super.onActive()
        job = launch(Dispatchers.Main) {
            for (element in this@toLiveData) {
                Log.d("ReceiveChannel", "postValue:" + element)
                postValue(element)
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        job.cancel()
    }
}
