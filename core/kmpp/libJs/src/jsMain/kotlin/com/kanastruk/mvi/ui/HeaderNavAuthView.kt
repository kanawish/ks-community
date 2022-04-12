package com.kanastruk.mvi.ui

import ModalLabels
import com.kanastruk.mvi.fb.FbAuthState
import injectors
import kotlinx.browser.document
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.dom.clear
import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.dom.create
import kotlinx.html.injector.inject
import kotlinx.html.js.onClickFunction
import modal
import modalTrigger
import org.w3c.dom.*
import unaryPlus
import utils.hide
import utils.show
import kotlin.properties.Delegates

sealed class HeaderNavAuthEvent {
    data class SignUp(val email:String):HeaderNavAuthEvent()
    data class Login(val email:String):HeaderNavAuthEvent()

    object SignOut:HeaderNavAuthEvent()

    // NOTE: For now we do a 'full page rebuild' when navigating.
    // TODO: Eventually, I believe we'll want one-page dynamic app for performance reasons.
}

data class NavItem(
    val navEntry:Pair<String,String>,
    val signedInOnly:Boolean = false
)

class HeaderNavAuthView(
    brand: NavbarBrand,
    private val navItems: List<NavItem>,
    search: Boolean = false,
    private val selected: (Pair<String, String>) -> Boolean = { false },
):ViewComponent<FbAuthState, HeaderNavAuthEvent>() {

    // Used when
    private val _events = MutableSharedFlow<HeaderNavAuthEvent>()

    private var navItemsUl: HTMLUListElement by Delegates.notNull()

    // Any other call to action we want can be prepended
    private var controlsDiv: HTMLDivElement by Delegates.notNull()

    var loginButton:HTMLButtonElement by Delegates.notNull()
    var signUpButton:HTMLButtonElement by Delegates.notNull()

    var sessionAvatar:HTMLImageElement by Delegates.notNull()
    var sessionDropDown:HTMLUListElement by Delegates.notNull()

    var busyDiv: HTMLDivElement by Delegates.notNull()

    // With Firebase UI being used, we inject the button inside the LI
    var profileDiv: HTMLDivElement by Delegates.notNull()

    var modalHiddenDiv:HTMLDivElement by Delegates.notNull()

    var signupEmail:HTMLInputElement by Delegates.notNull()
    var loginEmail:HTMLInputElement by Delegates.notNull()

    private val headerInjectors = injectors(
        HeaderNavAuthView::navItemsUl,
        HeaderNavAuthView::controlsDiv,
        HeaderNavAuthView::loginButton,
        HeaderNavAuthView::signUpButton,
        HeaderNavAuthView::sessionAvatar,
        HeaderNavAuthView::sessionDropDown,
        HeaderNavAuthView::busyDiv,
        HeaderNavAuthView::profileDiv,
        HeaderNavAuthView::modalHiddenDiv,
        HeaderNavAuthView::signupEmail,
        HeaderNavAuthView::loginEmail
    )

    val header: HTMLElement
    init {
        val signUpModalId = "signupModal"
        val loginModalId = "loginModal"

        header = document.create.inject(this, headerInjectors)
            .header("bg-dark text-white") {
                div(classes = "container") {
                    div(classes = "navbar navbar-dark d-flex flex-wrap align-items-center justify-content-center justify-content-md-start") {
                        a(classes = "navbar-brand me-0", href = "#") {// TODO: Fix me-?-?
                            when (brand) {
                                is NavbarBrand.Block -> brand.block(this@a)
                                is NavbarBrand.Image -> img(src = brand.src) { style = "height:1.75rem" }
                                is NavbarBrand.Text -> +brand.text
                                is NavbarBrand.ImageText -> {
                                    img(src = brand.src) { style = "height:1.75rem" }
                                    +brand.text
                                }
                                is NavbarBrand.TextImage -> {
                                    +brand.text
                                    img(src = brand.src) { style = "height:1.75rem" }
                                }
                            }
                        }

                        ul("nav col-12 col-md-auto me-md-auto mb-2 justify-content-center mb-md-0") {
                            classes += +HeaderNavAuthView::navItemsUl
                        }

                        // TODO: Probably want to review the 'md' vs 'lg' layout stuff when search is enabled.
                        if (search) {
                            form(classes = "col-12 col-md-auto mb-3 mb-md-0 me-md-3") {
                                input(InputType.search, classes = "form-control form-control-dark") {
                                    placeholder = "Search..." // TODO: i18n
                                }
                            }
                        }

                        div("pt-1") {
                            classes+= +HeaderNavAuthView::busyDiv
                            div("spinner-border") { role = "status" }
                        }

                        // TODO: placeholders, implement for real.
                        div("text-end") {

                            classes += +HeaderNavAuthView::controlsDiv
                            button(classes = "btn btn-outline-light me-2", type = ButtonType.button) {
                                classes += +HeaderNavAuthView::loginButton
                                modalTrigger(loginModalId)
                                +"Login" // TODO: i18n
                            }
                            button(classes = "btn btn-warning", type = ButtonType.button) {
                                classes += +HeaderNavAuthView::signUpButton
                                modalTrigger(signUpModalId)
                                +"Sign-up"
                            }
                            div("d-inline-block dropdown ms-2 text-end") {
                                classes += +HeaderNavAuthView::profileDiv
                                a(href = "#", classes = "d-block link-light text-decoration-none dropdown-toggle") {
                                    id = "navUserDropdown"
                                    attributes["data-bs-toggle"] = "dropdown"
                                    attributes["aria-expanded"] = "false"
                                    img(alt = "mdo", classes = "rounded-circle") {
                                        classes += +HeaderNavAuthView::sessionAvatar
                                        src = "https://github.com/kanawish.png"
                                        width = "32"
                                        height = "32"
                                    }
                                }
                                ul(classes = "dropdown-menu dropdown-menu-end text-small") {
                                    // TODO: Prepend to this component could be done at construction?
                                    classes += +HeaderNavAuthView::sessionDropDown
                                    attributes["aria-labelledby"] = "navUserDropdown"
                                    li { hr(classes = "dropdown-divider") { +"" } }
                                    // TODO: Remove?
                                    li(classes = "dropdown-item") {
                                        +"Sign out"
                                        onClickFunction = {
                                            launch { _events.emit(HeaderNavAuthEvent.SignOut) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                div(classes=+HeaderNavAuthView::modalHiddenDiv) {
                    div {
                        val loginLabels = ModalLabels("Login", "Ok", "Annuler")
                        val loginDiv: DIV.() -> Unit = {
                            div("mb-3") {
                                val loginInputId = "loginInput"
                                label("form-label") {
                                    +"Email address"
                                    htmlFor = loginInputId
                                }
                                input(type = InputType.email, classes = "form-control") {
                                    classes += +HeaderNavAuthView::loginEmail
                                    id = loginInputId
                                }
                            }
                        }
                        modal(loginModalId, loginLabels, loginDiv) {
                            launch {
                                _events.emit(HeaderNavAuthEvent.Login(loginEmail.value))
                            }
                        }
                    }

                    div{
                        val signUpLabels = ModalLabels("Sign-Up", "Ok", "Annuler")
                        val signUpDiv: DIV.() -> Unit = {
                            div("mb-3") {
                                val signUpInputId = "signUpInput"
                                label("form-label") {
                                    +"Email address"
                                    htmlFor = signUpInputId
                                }
                                input(type = InputType.email, classes = "form-control") {
                                    classes+=+HeaderNavAuthView::signupEmail
                                    id = signUpInputId
                                }
                                div("form-text") { +"We'll never share your email with anyone else." }
                            }
                        }
                        modal(signUpModalId, signUpLabels, signUpDiv) {
                            launch {
                                _events.emit(HeaderNavAuthEvent.SignUp(signupEmail.value))
                            }
                        }
                    }
                }
            }

    }

    fun renderNavItems(signedIn: Boolean) {
        navItemsUl.clear()
        navItemsUl.append {
            navItems
                .filter { navItem -> !navItem.signedInOnly || signedIn }
                .forEach { navItem ->
                    li("nav-item") {
                        a(classes = "nav-link px-2") {
                            val (label, link) = navItem.navEntry
                            if (selected(navItem.navEntry)) {
                                classes += "text-secondary"
                                href = "#"
                            } else {
                                classes += "text-white"
                                href = link
                            }
                            +label
                        }
                    }
                }
        }
    }

    override fun attach(model: StateFlow<FbAuthState>, handler: (HeaderNavAuthEvent) -> Unit) {
        model.launchCollect { authState ->
            renderNavItems(authState is FbAuthState.SignedIn)

            when(authState) {
                FbAuthState.Loading -> {
                    busyDiv.show()
                    loginButton.hide()
                    signUpButton.hide()
                    profileDiv.hide()
                }
                is FbAuthState.AuthError,
                FbAuthState.SignedOut -> {
                    busyDiv.hide()
                    loginButton.show()
                    signUpButton.show()
                    profileDiv.hide()
                }
                is FbAuthState.SignedIn -> {
                    busyDiv.hide()
                    loginButton.hide()
                    signUpButton.hide()
                    profileDiv.show()
                }
            }
        }

        launch {
            _events.collect { handler(it) }
        }
    }

}