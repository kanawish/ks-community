package utils

import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import org.w3c.dom.Element

fun hide(e: Element) = e.addClass("d-none")
fun show(e: Element) = e.removeClass("d-none")
fun Element.hide() = this.addClass("d-none")
fun Element.show() = this.removeClass("d-none")
