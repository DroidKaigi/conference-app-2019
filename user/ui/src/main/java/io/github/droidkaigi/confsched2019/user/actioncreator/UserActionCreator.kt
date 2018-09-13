package io.github.droidkaigi.confsched2019.user.actioncreator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import confsched2018.droidkaigi.github.io.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.session.model.Action
import kotlinx.coroutines.experimental.launch
import nl.adaptivity.android.coroutines.activityResult
import javax.inject.Inject
import javax.inject.Named

class UserActionCreator @Inject constructor(
        val activity: AppCompatActivity,
        val dispatcher: Dispatcher,
        @Named("defaultFirebaseWebClientId")
        val defaultFirebaseWebClientId: Int
) {
    val googleSignInClient: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(activity, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(defaultFirebaseWebClientId))
                .requestEmail()
                .build())
    }


    val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun setupUser() {
        launch {
            if (firebaseAuth.currentUser == null) {
                val activityResult = activity.activityResult(googleSignInClient.signInIntent)
                activityResult.onError { e ->
                    e.printStackTrace()
                }
                activityResult.onOk { resultIntent ->
                    onSignIn(resultIntent)
                }
            } else {
                dispatcher.send(Action.UserRegisteredEvent)
            }
        }
    }

    fun onSignIn(resultIntent: Intent?) {
        launch {
            val task = GoogleSignIn.getSignedInAccountFromIntent(resultIntent)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(activity) { task ->
                            launch {
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
//                                    val user = firebaseAuth.currentUser
                                    dispatcher.send(Action.UserRegisteredEvent)
                                } else {
                                    // If sign in fails, display a message to the user.
                                }
                            }
                        }
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
            }
        }

    }
}
