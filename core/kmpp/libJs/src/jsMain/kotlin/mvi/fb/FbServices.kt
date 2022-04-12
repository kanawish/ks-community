package mvi.fb

import com.kanastruk.mvi.fb.FbAuthState
import com.kanastruk.mvi.fb.FbAuthIntent
import firebase.auth.ActionCodeSettings
import firebase.database.Database
import firebase.functions.Functions
import firebase.storage.Storage
import kotlinx.browser.window
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.w3c.fetch.RequestInit
import utils.SearchParamHelper
import utils.debug
import kotlin.js.Json
import kotlin.js.json

class FbServices(fbConfig: Json) : CoroutineScope
by CoroutineScope(Dispatchers.Default + Job()) {
   private val _store = MutableStateFlow<FbAuthState>(FbAuthState.Loading)
   val authStateStore = _store.asStateFlow()

   val firebaseDb: Database // always on
   val firebaseFunctions: Functions
   val firebaseStorage: Storage

   private val firebaseAuth: dynamic
   private val googleAuthProvider: dynamic

   init {
      debug { console.log("FirebaseServices.init {}") }

      // 🔥🔥 Firebase dependencies.
      val firebase: dynamic = kotlinext.js.require("firebase/app").default
      debug { console.log(firebase) }
      kotlinext.js.require("firebase/auth")
      kotlinext.js.require("firebase/database")
      kotlinext.js.require("firebase/functions")
      kotlinext.js.require("firebase/storage")

      // 🔥🔥 For Firebase JS SDK v7.20.0 and later, measurementId is optional
      firebase.initializeApp(fbConfig)

      // ***** 🔥🔥 FIREBASE AUTH INIT 🔥🔥 *****
      firebaseAuth = firebase.auth()
      // ***** 🧪 localhost emulators, setup before usage. 🧪 *****
      if (SearchParamHelper.isEmulator()) {
         firebaseAuth.useEmulator("http://localhost:9099")
      }
      googleAuthProvider = firebase.auth.GoogleAuthProvider
      firebaseAuth.onAuthStateChanged(::fbAuthHandler, ::fbAuthErrorHandler)

      // ***** 🔥🔥 FIREBASE DB INIT 🔥🔥 *****
      // https://github.com/Kotlin/dukat
      // https://levelup.gitconnected.com/ticket-to-kotlin-using-dukat-to-bridge-kotlin-with-the-js-world-431a2458b95c
      @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
      firebaseDb = firebase.database() as Database

      // ***** 🔥🔥 FIREBASE FUNCTIONS INIT 🔥🔥 *****
      @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
      firebaseFunctions = firebase.functions() as Functions

      // ***** 🔥🔥 FIREBASE STORAGE INIT 🔥🔥 *****
      @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
      firebaseStorage = firebase.storage() as Storage
      debug { console.log("firebaseStorage: %o", firebaseStorage) }

      // ***** 🧪 localhost emulators 🧪 *****
      if(SearchParamHelper.isEmulator()) {
         debug { console.log("SearchParamHelper.isEmulator() == true") }
         firebaseDb.useEmulator("localhost", 9000)
         firebaseFunctions.useEmulator("localhost", 5001)
         firebaseStorage.useEmulator("localhost", 9199)
      }
   }

   fun buildFbAuthView(): FbAuthView {
      return FbAuthView(
         firebaseAuth,
         googleAuthProvider
      )
   }

   private fun getCurrentUserIdToken():dynamic {
      debug { console.log("getCurrentUserIdToken()") }
      return firebaseAuth.currentUser.getIdToken()
   }

   /**
    * We can do fetches on fb functions in the `onRequest(req,res)`
    * style while staying authenticated.
    */
   fun fetchAuthorized(endpoint:String, block:(Json?)->Unit) {
      debug { console.log("fetchAuthorized %o", endpoint) }
      val onRejected: (Throwable) -> Unit = { throwable ->
         console.log(throwable)
         block(null)
      }

      getCurrentUserIdToken()
         .then { jwtToken ->
            val requestInit = RequestInit(
               method = "GET",
               headers = json(
                  "Accept" to "application/json",
                  "Authorization" to "Bearer $jwtToken",
                  "Content-Type" to "application/json"
               )
            )
            debug { console.log("window.fetch %o", endpoint) }
            window.fetch(endpoint, requestInit)
               .then { res ->
                  res.json()
                     .then { json -> block(json.unsafeCast<Json>()) }
                     .catch(onRejected) // Failed to case/parse
               }
               .catch(onRejected) // Failed to fetch
         }
         .catch(onRejected) // Couldn't get jwtToken
   }

   private fun fbAuthHandler(user: dynamic) {
      debug {
         console.log("fbAuthHandler(user)")
         console.log(user)
      }
      if (user != null) {
         // NOTE: When time comes, upgrade this to full user data, with extern class.
         process { FbAuthState.SignedIn(currentAuthUid()) }
      } else {
         process { FbAuthState.SignedOut }
      }
   }

   private fun currentAuthUid():String {
      return firebaseAuth.getUid().unsafeCast<String>()
   }

   // NOTE: Equivalent to firebaseAuth.getUid(), but currentUser holds more interesting info.
   fun getCurrentUserUid():String {
      val uid = firebaseAuth.currentUser.uid.unsafeCast<String>()
      debug { console.log("getCurrentUserUid($uid)") }
      return uid
   }


   private fun fbAuthErrorHandler(error: dynamic) {
      console.error("FB Auth Error")
      console.error(error)
      process { FbAuthState.SignedOut }
   }

   fun ActionCodeSettings(): ActionCodeSettings = js("{}")

   private val emailSignInKey = "emailForSignIn"
   fun sendSignInLink(url:String, email:String) {
      val acs = ActionCodeSettings().let { acs ->
         acs.handleCodeInApp = true
         acs.url = url // just for first draft.
         acs
      }

      debug {
         console.log("HeaderNavAuthEvent.SignUp $email")
         console.log("acs: %o", acs)
      }

      firebaseAuth.sendSignInLinkToEmail(email,acs)
         .then {
            debug { console.log("Sending a sign-up/sign-in email.") }
            window.localStorage.setItem(emailSignInKey, email)
            // Link sent, save email locally, let use know we're pending.
         }
         .catch { err -> console.error("Caught error %o", err)  }
   }

   fun getSavedSignInEmail():String? {
      return window.localStorage.getItem(emailSignInKey)
   }

   /**
    * Sign-in email link processing.
    *
    * Everything else should fire off of regular auth-handling.
    */
   fun processSignInEmailLink(email:String) {
      firebaseAuth.signInWithEmailLink(email,window.location.href)
         .then { _ ->
            console.log("signInWithEmailLink for $email worked.")
            window.localStorage.removeItem(emailSignInKey)
         }
         .catch { err -> console.error("Caught error %o", err)  }
   }

   fun isSignInWithEmailLink() = firebaseAuth.isSignInWithEmailLink(window.location.href) as Boolean

   fun anonymousSignInIntent(): FbAuthIntent {
      return FbAuthIntent { oldAuthState ->
         when( oldAuthState ) {
            FbAuthState.SignedOut -> {
               debug { console.log("Launching anonymous sign-in") }
               // Handlers don't change state, the auth state change listeners will catch this one.
               firebaseAuth.signInAnonymously()
                  .then { debug { console.log("Anonymous sign-in completed.") } }
                  .catch { err -> console.error("Caught error %o", err) }

               FbAuthState.Loading
            }
            else -> {
               debug { console.log("No-op, we're signed-in or waiting.") }
               oldAuthState
            }
         }
      }
   }

   val signOutIntent = FbAuthIntent {
      firebaseAuth.signOut()
         .then {
            debug { console.log("Signed out.") }
            process { FbAuthState.SignedOut }
         }
         .catch { err ->
            console.error("Caught error %o", err)
            process { FbAuthState.SignedOut }
         }
      FbAuthState.Loading
   }

   // TODO: simplify
   fun process(intent: FbAuthIntent) {
      launch { _store.emit(intent.reduce(_store.value)) }
   }
}