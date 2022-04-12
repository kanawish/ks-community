package com.kanastruk.mvi.fb

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kanastruk.mvi.fb.FbAuthState.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Start with an Android implementation, and we'll try to extract
 * a "common" interface from it.
 *
 * NOTE: Injecting scope + dispatcher if needed would be more sensible.
 *    see https://medium.com/androiddevelopers/coroutines-patterns-for-work-that-shouldnt-be-cancelled-e26c40f142ad
 *    and https://medium.com/androiddevelopers/exceptions-in-coroutines-ce8da1ec060c
 */
class FirebaseModel(
    private val scope:CoroutineScope = MainScope()
) {
    private val firebaseAuth = Firebase.auth

    private val _store = MutableStateFlow<FbAuthState>(Loading)
    val store: StateFlow<FbAuthState> = _store.asStateFlow()

    init {
        Timber.d("🔥 init")
        firebaseAuth.addAuthStateListener { auth ->
            Timber.d("🔥 AuthStateListener : ${auth.currentUser}")
            _store.value = when(auth.currentUser) {
                null -> {
                    Timber.d("🔥 Signed Out")
                    SignedOut
                }
                else -> {
                    Timber.d("🔥 Signed In $auth")
                    auth.uid?.let { SignedIn(it) }
                        ?: AuthError("No uid for received auth?")
                }
            }
        }

        scope.launch {
            _store.collect { fbAuthState ->
                Timber.d("🔥 fbAuthState: $fbAuthState")
                when(fbAuthState) {
                    Loading -> Timber.d("🔥 Loading")
                    is AuthError -> Timber.d("🔥 Caught an error: ${fbAuthState.msg}")
                    SignedOut -> {
                        Timber.d("🔥 SignedOut: calling signInAnonymously() to proceed.")
                        // Auth state listener is charged with mapping state changes to _store.
                        firebaseAuth.signInAnonymously()
                    }
                    is SignedIn -> Timber.d("🔥 Signed In")
                    else -> { TODO("Add error handling here, ...")}
                }
            }
        }
    }

    // TODO: A quick example of fetching user Id token, probably needed for authorized FB fetches.
    private fun foo() {
        firebaseAuth.currentUser
            ?.getIdToken(false)
            ?.addOnCompleteListener { task ->
                when {
                    task.isComplete && task.isSuccessful -> {
                        Timber.d("${task.result}")
                    }
                    else -> {
                        TODO("Handle failure state.")
                    }
                }
            }
    }

    fun process(intent: FbAuthIntent) {
        _store.value = intent.reduce(_store.value)
    }
}