package com.kanastruk.mvi.ui

import kotlinx.html.A

sealed class NavbarBrand {
    data class Block(val block: A.()->Unit): NavbarBrand()
    data class Image(val src:String): NavbarBrand()
    data class Text(val text:String): NavbarBrand()
    data class ImageText(val src: String, val text: String): NavbarBrand()
    data class TextImage(val text:String, val src: String): NavbarBrand()
}