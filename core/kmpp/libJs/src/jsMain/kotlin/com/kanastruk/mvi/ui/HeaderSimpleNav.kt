package com.kanastruk.mvi.ui

import injectors
import kotlinx.browser.document
import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.injector.inject
import org.w3c.dom.HTMLDivElement
import unaryPlus
import kotlin.properties.Delegates

class HeaderSimpleNav(brand: NavbarBrand, navItems:List<Pair<String,String>> ) {
    var calloutDiv: HTMLDivElement by Delegates.notNull()

    private val headerInjectors = injectors(
        HeaderSimpleNav::calloutDiv,
    )

    val header = document.create.inject(this,headerInjectors).header {
        nav(classes = "navbar navbar-expand-md navbar-dark fixed-top bg-dark") {
            div(classes = "container-fluid") {
                a(classes="navbar-brand", href = "#") {
                    when(brand) {
                        is NavbarBrand.Image -> img(src = brand.src) { style="height:1.75rem" }
                        is NavbarBrand.Text -> +brand.text
                        is NavbarBrand.ImageText -> {
                            img(src = brand.src)  { style="height:1.75rem" }
                            +brand.text
                        }
                        is NavbarBrand.TextImage -> {
                            +brand.text
                            img(src = brand.src) { style="height:1.75rem" }
                        }
                    }
                }
                // Adaptive toggler button.
                button(classes = "navbar-toggler", type = ButtonType.button
                ) {
                    attributes["data-bs-toggle"]="collapse"
                    attributes["data-bs-target"]="#navbarCollapse"
                    span(classes = "navbar-toggler-icon")
                }
                div("collapse navbar-collapse") {
                    id="navbarCollapse"
                    ul(classes = "navbar-nav me-auto") {
                        navItems.forEach {
                            li("nav-item") {
                                a(classes = "nav-link") {
                                    if (it == navItems.first()) classes += "active"
                                    val (label, link) = it
                                    href = link
                                    +label
                                }
                            }
                        }
                    }
                    div(classes="d-flex") {
                        classes += +HeaderSimpleNav::calloutDiv
                    }
                }
            }
        }
    }

}