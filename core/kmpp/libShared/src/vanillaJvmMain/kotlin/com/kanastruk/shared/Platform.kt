package com.kanastruk.shared

actual class Platform actual constructor() {
    actual val platform: String = "Desktop"
    actual fun foo(): String = platform
}