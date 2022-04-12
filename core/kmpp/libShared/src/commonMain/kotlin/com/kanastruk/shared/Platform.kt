package com.kanastruk.shared

expect class Platform() {
    val platform: String
    fun foo(): String
}