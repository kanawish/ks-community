package com.kanastruk.mvi.fb

sealed class FbAuthState {
    object Loading : FbAuthState()
    object SignedOut : FbAuthState()
    data class SignedIn(val authId: String) : FbAuthState()
    data class AuthError(val msg: String) : FbAuthState()
}
