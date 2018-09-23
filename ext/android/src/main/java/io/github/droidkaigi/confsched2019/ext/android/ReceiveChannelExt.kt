package io.github.droidkaigi.confsched2019.ext.android

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.consume
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

fun <T> ReceiveChannel<T>.toLiveData(defaultValue: T? = null): LiveData<T> = object : LiveData<T>(), CoroutineScope {
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onActive() {
        super.onActive()
        job = Job()
        launch {
            if (defaultValue != null) {
                value = defaultValue
            }
            consume {
                for (element in this) {
                    Log.d("ReceiveChannel", "postValue" + element)
                    value = element
                }
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        job.cancel()
    }
}
