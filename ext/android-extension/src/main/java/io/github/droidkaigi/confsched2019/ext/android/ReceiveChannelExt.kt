package io.github.droidkaigi.confsched2019.ext.android

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import com.shopify.livedataktx.SingleLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

@MainThread fun <T> ReceiveChannel<T>.toSingleLiveData(defaultValue: T? = null): SingleLiveData<T> {
    return object : SingleLiveData<T>(), CoroutineScope by GlobalScope {
        lateinit var job: Job

        init {
            if (defaultValue != null) {
                value = defaultValue
            }
        }

        override fun onActive() {
            super.onActive()
            job = launch(Dispatchers.Main) {
                for (element in this@toSingleLiveData) {
                    postValue(element)
                }
            }
        }

        override fun onInactive() {
            super.onInactive()
            job.cancel()
        }
    }
}

@MainThread fun <T> ReceiveChannel<T>.toLiveData(
    defaultValue: T? = null
): LiveData<T> {
    return object : LiveData<T>(), CoroutineScope by GlobalScope {
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
                    postValue(element)
                }
            }
        }

        override fun onInactive() {
            super.onInactive()
            job.cancel()
        }
    }
}
