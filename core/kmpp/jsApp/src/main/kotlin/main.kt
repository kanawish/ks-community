import com.kanastruk.i18n.client.toClientStrings
import com.kanastruk.mvi.*
import com.kanastruk.mvi.UserProfileField.*
import com.kanastruk.mvi.fb.FbAuthState.SignedOut
import com.kanastruk.mvi.UserProfileEditorModel
import com.kanastruk.mvi.ui.HeaderNavAuthView
import com.kanastruk.mvi.ui.OldHeaderNavAuthView
import com.kanastruk.mvi.ui.UserProfileEditView
import com.kanastruk.mvi.ui.ViewComponent
import io.kvision.jquery.jQuery
import kotlinext.js.require
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.dom.create
import kotlinx.html.js.onClickFunction
import mvi.fb.FbServices
import mvi.fb.FbServicesDispatch
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement
import utils.*
import kotlin.js.Json

/**
 * NOTE: console.log() bug https://youtrack.jetbrains.com/issue/KT-47811
 */
suspend fun fetch(input:String): String {
    return window
        .fetch(input).await()
        .text().await()
}

@JsName("jQuery")
val jQuery = jQuery

fun bootstrapInit() {
    console.log("bootstrapInit() jQuery == %o", jQuery)

    // NOTE: https://getbootstrap.com/docs/5.1/getting-started/webpack/ ...
    require("bootstrap/dist/css/bootstrap.css")
    require("bootstrap") // needed for dropdowns and so on.
    // NOTE: Fix for v5 detection in bootstrap tables below.
    //  https://github.com/wenzhixin/bootstrap-table/issues/5837
    js("window.bootstrap = {\n" +
            "  Tooltip: {\n" +
            "    VERSION: \"5\"\n" +
            "  }\n" +
            "}")

    headAppendCss("https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.0/font/bootstrap-icons.css")
    require("font-awesome/all")

    headAppendCss("css/custom.css")
    require("bootstrap-table/dist/bootstrap-table.min.css")

    require("jquery/dist/jquery.js")

    require("bootstrap-table/dist/bootstrap-table.js")
    console.log("last stage")
}

val labelMapFr = mapOf(
    FIRST to "Prénom",
    LAST to "Nom de famille",
    COMPANY to "Compagnie",
    EMAIL to "Courriel",
    PHONE to "No. téléphone",
    ADDRESS1 to "Adresse",
    CITY to "Ville",
    COUNTRY_SUB to "Province",
    POSTAL to "Code postal",
    COUNTRY to "Pays"
)

@DelicateCoroutinesApi
fun main() {
    console.log("main() start.")

    GlobalScope.launch {
        val config: Json =
            // Running hosted, or with local config?
            suspendFetch("/__/firebase/init.json") ?: suspendFetch("./fbConfig.json")
            ?: throw RuntimeException("I need my config.")

        console.log("fetched config: %o", config)

        // Quick test of grabbing client strings. Early shared code concept.
        val clientStrings = fetch("json/i18n/en.json").toClientStrings()
        console.log("Fetched i18n key-values: %o", clientStrings)

        bootstrapInit()
        headAppendCss("css/table-fixed.css")

        val fbServices = FbServices(config)

        launch {
            fbServices.authStateStore.collect { s ->
                console.log("fbServices authState: %o", s)
            }
        }

        // When signedOut, opens anonymous session automatically.
        fbServices.apply {
            launch {
                authStateStore
                    .filterIsInstance<SignedOut>()
                    .collect {
                        process(anonymousSignInIntent())
                    }
            }
        }

        val fbDispatch = FbServicesDispatch(fbServices)

        val fbServiceModel = JsFbServiceModel(fbServices,fbServices.authStateStore)
        val profileModel = UserProfileEditorModel(fbServiceModel, GlobalScope)

        // TODO: AuthView I believe should not be used as is, would be nice to override with our UI.
        // TODO: Should wait on full auth init being complete before using this?
        // TODO: Seems to break in new setup, investigate.
        val headerNavAuthView = OldHeaderNavAuthView()
        val fbAuthView = fbServices.buildFbAuthView()

        // Pre-document UI assembling
        headerNavAuthView.signOutLI.append(fbAuthView.signOutAnchor)

        // TODO: Import from i18n json
        val labelMap = clientStrings.profileEditor.run {
            mapOf(
                FIRST to firstName,
                LAST to lastName,
                COMPANY to companyName,
                EMAIL to email,
                PHONE to phone,
                ADDRESS1 to address,
                CITY to city,
                COUNTRY_SUB to country_sub,
                POSTAL to postal,
                COUNTRY to country
            )
        }
        val profileEditView = UserProfileEditView(labelMap,clientStrings)

        class UserProfileView(profileEditView: HTMLElement) : ViewComponent<UserProfileEditorState, UserProfileViewEvent>() {
            private val _events = MutableSharedFlow<UserProfileViewEvent>()
            val closed = document.create.div( "d-flex p-3 justify-content-center" ) {
                button(classes = "btn btn-primary") {
                    +"Open Editor"
                    onClickFunction = target<HTMLButtonElement>{
                        launch {
                            _events.emit(UserProfileViewEvent.OpenEditor)
                        }
                    }
                }
            }
            val loading = spinner( "Loading" )
            val editing = profileEditView
            val saving = spinner( "Saving" )

            val parent = document.create.div().apply { append(closed,loading,editing,saving) }

            fun spinner(label:String) = document.create.div("d-flex p-3 justify-content-center") {
                div("spinner-border") { role = "status" }
                div("ms-3 mt-1") { +label }
            }

            fun List<HTMLElement>.onlyShow(element:HTMLElement) {
                forEach { if( element == it ) it.show() else it.hide() }
            }

            override fun attach(
                model: StateFlow<UserProfileEditorState>,
                handler: (UserProfileViewEvent) -> Unit
            ) {
                val views = listOf(closed,loading,editing,saving)
                model.launchCollect { editorState ->
                    when(editorState) {
                        UserProfileEditorState.Closed -> views.onlyShow(closed)
                        UserProfileEditorState.Opening -> views.onlyShow(loading)
                        is UserProfileEditorState.Editing -> views.onlyShow(editing)
                        UserProfileEditorState.Saving -> views.onlyShow(saving)
                    }
                }
                launch {
                    _events.collect {
                        debug { console.log("?? %o", it) }
                        handler(it)
                    }
                }
            }
        }

        val profileView = UserProfileView(profileEditView.parentElement)

        document.body
            ?.apply {
                append(headerNavAuthView.header)
            }
            ?.append {
                main("container-fluid").apply {
                    append {
                        h1(classes = "p-3") { +"Edit User Profile" }
                        append(profileView.parent)
                        append(fbAuthView.signInRowDiv)
                        append(fbAuthView.pleaseWaitDiv)
                    }
                }

                div { id = "root" }

            } ?: console.error("No body?")

        fbAuthView.attach(fbServices.authStateStore, fbDispatch::handle)

        profileView.attach(profileModel.store, profileModel::dispatch)
        profileEditView.attach(profileModel.store, profileModel::dispatch)
    }

    console.log("Main completed.")
}