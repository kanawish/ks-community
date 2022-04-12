package mvi.fb

sealed class FbAuthViewEvent {
   // No 'SignIn' since firebase-ui is independent.
   object SignOut: FbAuthViewEvent()
}