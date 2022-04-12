package utils

import kotlinx.browser.document
import kotlinx.html.dom.append
import kotlinx.html.js.link
import kotlinx.html.js.script

fun headAppendCss(href:String) {
    document.head?.append { link(href,"stylesheet","text/css") }
}

fun bar() {
    document.append { script{
        this.type = ""
        this.src = ""
    } }
}
