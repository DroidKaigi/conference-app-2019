package io.github.droidkaigi.confsched2019.user.actioncreator

import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.await
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.action.Action
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserActionCreator @Inject constructor(
    val activity: FragmentActivity,
    val dispatcher: Dispatcher
) : CoroutineScope by activity.coroutineScope {

    val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun setupUser() {
        if (firebaseAuth.currentUser == null) {
            signIn()
        } else {
            dispatcher.launchAndSend(Action.UserRegistered)
        }
    }

    private fun signIn() {
        launch {
            try {
                firebaseAuth.signInAnonymously().await()
                dispatcher.send(Action.UserRegistered)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    private fun onError(e: Exception? = null) {
        e?.printStackTrace()
    }
}
