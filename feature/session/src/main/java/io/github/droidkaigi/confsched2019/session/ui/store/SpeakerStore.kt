package io.github.droidkaigi.confsched2019.session.ui.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.toLiveData
import io.github.droidkaigi.confsched2019.model.Speaker
import kotlinx.coroutines.channels.filter
import kotlinx.coroutines.channels.map

class SpeakerStore @AssistedInject constructor(
    dispatcher: Dispatcher,
    @Assisted val speakerId: String
) : ViewModel() {
    @AssistedInject.Factory
    interface Factory {
        fun create(speakerId: String): SpeakerStore
    }

    val speaker: LiveData<Speaker?> = dispatcher
        .subscribe<Action.SpeakerLoaded>()
        .filter { it.speaker.id == speakerId }
        .map { it.speaker }
        .toLiveData(null)
}
