package io.github.droidkaigi.confsched2019.user.actioncreator

import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.await
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.model.Action
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class UserActionCreator @Inject constructor(
    val activity: FragmentActivity,
    val dispatcher: Dispatcher,
    @Named("defaultFirebaseWebClientId") val defaultFirebaseWebClientId: Int
) : CoroutineScope by activity.coroutineScope {
    // val googleSignInClient: GoogleSignInClient by lazy {
    //     GoogleSignIn.getClient(
    //         activity,
    //         GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    //             .requestIdToken(activity.getString(defaultFirebaseWebClientId))
    //             .requestEmail()
    //             .build()
    //     )
    // }

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
            // Google Sign In
            //                val activityResult = activity.activityResult(googleSignInClient.signInIntent)
            //                when (activityResult) {
            //                    is Error -> {
            //                        throw activityResult.e
            //                    }
            //                    is Cancelled -> {
            //                        throw RuntimeException()
            //                    }
            //                    is Ok -> {
            //                        val resultIntent = activityResult.data as Intent
            //                        val account = GoogleSignIn
            //                            .getSignedInAccountFromIntent(resultIntent)
            //                            .await()
            //                        onSignIn(account)
            //                    }
            //                }
        }
    }

    // private suspend fun onSignIn(account: GoogleSignInAccount) {
    //     val credential = GoogleAuthProvider.getCredential(account.idToken, null)
    //     firebaseAuth.signInWithCredential(credential).await()
    //     val user = result.user
    //     dispatcher.send(Action.UserRegistered)
    // }

    private fun onError(e: Exception? = null) {
        e?.printStackTrace()
    }
}
