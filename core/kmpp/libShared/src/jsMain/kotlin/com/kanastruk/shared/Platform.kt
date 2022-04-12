package com.kanastruk.shared

import kotlinx.browser.window

actual class Platform actual constructor() {
    actual val platform: String = "JS ${window.location}"
    actual fun foo(): String = platform
}