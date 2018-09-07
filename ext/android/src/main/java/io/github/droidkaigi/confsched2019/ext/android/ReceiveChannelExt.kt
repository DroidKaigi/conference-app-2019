package io.github.droidkaigi.confsched2019.ext.android

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.consume
import kotlinx.coroutines.experimental.launch

fun <T> ReceiveChannel<T>.toLiveData() = object : LiveData<T>() {
    lateinit var job: Job
    override fun onActive() {
        super.onActive()
        job = launch(UI) {
            consume {
                for (element in this) {
                    Log.d("ReceiveChannel", "postValue" + element)
                    postValue(element)
                }
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        job.cancel()
    }
}
