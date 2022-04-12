package com.kanastruk.mvi.ui

import com.kanastruk.mvi.ui.OldHeaderNavAuthView.Type.*
import injectors
import kotlinx.browser.document
import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.injector.inject
import org.w3c.dom.HTMLLIElement
import unaryPlus
import kotlin.properties.Delegates

class OldHeaderNavAuthView(val type: Type = MINIMAL) {
    enum class Type {
        FULL,
        MINIMAL
    }

    var signOutLI: HTMLLIElement by Delegates.notNull()
    private val headerInjectors = injectors(
        OldHeaderNavAuthView::signOutLI,
    )

    val header = document.create.inject(this,headerInjectors).header("p-3 bg-dark text-white") {
        div("container") {
            div("d-flex flex-wrap align-items-center justify-content-center justify-content-lg-start") {
                a(href = "/", classes = "d-flex align-items-center mb-2 mb-lg-0 text-white text-decoration-none") {
                    i("me-2 bi-bootstrap") {
                        style = "font-size: 2.5rem;"
                    }
                }
                ul("nav col-12 col-lg-auto me-lg-auto mb-2 justify-content-center mb-md-0") {
                    when(type) {
                        MINIMAL -> {}
                        else -> {
                            li { a(href="#", classes = "nav-link px-2 text-secondary") {+"Home"} }
                            li { a(href="#", classes = "nav-link px-2 text-white") {+"Features"} }
                            li { a(href="#", classes = "nav-link px-2 text-white") {+"Pricing"} }
                            li { a(href="#", classes = "nav-link px-2 text-white") {+"FAQs"} }
                        }
                    }
                }
                if( type != MINIMAL ) {
                    form(classes = "col-12 col-lg-auto mb-3 mb-lg-0 me-lg-3") {
                        input(InputType.search, classes = "form-control form-control-dark") {
                            placeholder = "Search..."
                        }
                    }
                }
                div("text-end") {
                    button(classes = "btn btn-outline-light me-2", type = ButtonType.button) { +"Login" }
                    button(classes = "btn btn-warning", type = ButtonType.button) { +"Sign-up" }
                }
                div("dropdown ms-2 text-end") {
                    a(href = "#", classes = "d-block link-light text-decoration-none dropdown-toggle") {
                        id = "dropdownUser1"
                        attributes["data-bs-toggle"] = "dropdown"
                        attributes["aria-expanded"] = "false"
                        img( alt ="mdo", classes="rounded-circle") {
                            src="https://github.com/kanawish.png"
                            width="32"
                            height="32"
                        }
                    }
                    ul(classes="dropdown-menu text-small") {
                        attributes["aria-labelledby"] = "dropdownUser1"
                        style=""
                        li { a(classes="dropdown-item", href="#") {+"New project..."} }
                        li { a(classes="dropdown-item", href="#") {+"Settings"} }
                        li { a(classes="dropdown-item", href="#") {+"Profile"} }
                        li { hr(classes="dropdown-divider") {+""} }
                        li(classes=+OldHeaderNavAuthView::signOutLI) { } // a(classes="dropdown-item", href="#") {+"Sign out"} }
                    }
                }
            }
        }
    }
}