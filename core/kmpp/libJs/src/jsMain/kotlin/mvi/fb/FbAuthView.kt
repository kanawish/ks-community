package mvi.fb

import com.kanastruk.mvi.fb.FbAuthState
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLButtonElement
import com.kanastruk.mvi.ui.ViewComponent
import org.w3c.dom.HTMLAnchorElement
import utils.debug
import utils.hide
import utils.show
import utils.target
import kotlin.js.json

/**
 * This view might be a better example of simplicity.
 *
 * Trying to use my custom Class()/Id() instead of the
 * somewhat complex injector mechanism exposed by kotlinx.html
 *
 * NOTE: 🐍fbAuth🐍 is actually used in this class.
 */
class FbAuthView(fbAuth: dynamic, googleAuthProvider: dynamic):
   ViewComponent<FbAuthState, FbAuthViewEvent>() {

   private val _events = MutableSharedFlow<FbAuthViewEvent>()

   val pleaseWaitDiv = document.create.div {
      div("d-flex m-3 justify-content-center") {
         div("spinner-border") { role = "status" }
         div("ml-3 mt-1") { +"Please wait..." }
      }
   }

   // div target that firebase-ui API will use as fill-target.
   val signInRowDiv = document.create.div("row") {
      id = "$signInRowId"
   }

   val signOutAnchor = document.create.a(classes = "dropdown-item", href = "#") {
      +"Sign-out"
      onClickFunction = target<HTMLAnchorElement> {
         launch {
            println("Sign-out clicked")
            _events.emit(FbAuthViewEvent.SignOut)
         }
      }
   }

   val signOutButton = document.create.button(classes = "btn btn-light") {
      +"Sign-out"
      onClickFunction = target<HTMLButtonElement> {
         launch {
            println("Sign-out clicked")
            _events.emit(FbAuthViewEvent.SignOut)
         }
      }
   }

   // TODO: Eventually make this configurable?
   private val fbAuthUiConfig = json(
      "signInSuccessUrl" to window.location.search,
      "signInOptions" to arrayOf(googleAuthProvider.PROVIDER_ID),
      // tosUrl and privacyPolicyUrl accept either url string or a callback function.
      "tosUrl" to "tos.html",
      "privacyPolicyUrl" to "privacy.html"
   )

   private val fbAuthUi: dynamic

   init {
      debug { console.log("constructed with 1.- %o\n 2.- %o", fbAuth, googleAuthProvider) }
      // NOTE: I think this should work neatly, but keep an eye out.
      kotlinext.js.require("firebaseui/dist/firebaseui.css")

      // **** 🕸🔥🕸 Sign-in section [Firebase UI] 🕸🔥🕸 ****
      val firebaseui = kotlinext.js.require("firebaseui")
      debug {
         console.log("firebaseui:")
         console.log(firebaseui)
      }

      // https://discuss.kotlinlang.org/t/is-there-a-way-to-use-the-new-operator-with-arguments-on-a-dynamic-variable-in-kotlin-javascript/6126
      // NOTE: Using 🐍fbAuth🐍 here via js execution, underneath.
      fbAuthUi = newUi(fbAuth)
      // fbAuthUi = js("new firebaseui.auth.AuthUI(fbAuth)")
   }

   private fun newUi(fbAuth:dynamic):dynamic {
      return js("new firebaseui.auth.AuthUI(fbAuth)")
   }

   private fun attachFirebaseUi() {
      // NOTE: This errors out if signInRow was not added to document somewhere.
      fbAuthUi.start("#$signInRowId", fbAuthUiConfig)
   }

   override fun attach(model: StateFlow<FbAuthState>, handler: (FbAuthViewEvent) -> Unit) {
      attachFirebaseUi()

      // Hide and shows sections as needed
      launch { model.collect(::render) }

      // Only one event, the sign-out click.
      launch { _events.collect { handler(it) } }
   }

   private fun render(state: FbAuthState) {
      debug {
         console.log("FbAuthView.render()")
         console.log(state)
      }
      when(state) {
         FbAuthState.Loading -> {
            pleaseWaitDiv.show()
            signInRowDiv.hide()
            signOutButton.hide()
         }
         FbAuthState.SignedOut -> {
            pleaseWaitDiv.hide()
            signInRowDiv.show()
            signOutButton.hide()
         }
         is FbAuthState.SignedIn -> {
            pleaseWaitDiv.hide()
            signInRowDiv.hide()
            signOutButton.show()
         }
      }
   }

   companion object {
      private const val signInRowId = "signInRow"
   }
}
