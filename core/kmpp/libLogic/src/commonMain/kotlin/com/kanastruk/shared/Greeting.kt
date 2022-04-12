package com.kanastruk.shared

import kotlinx.serialization.encodeToString

class Greeting {
    fun greeting(): String {
        return "Hello from ${Platform().platform}!"
    }

    @kotlinx.serialization.Serializable
    data class Bar(val name:String, val language:String)

    fun fooJson():String {
        val data = Bar("Etienne","fr_ca")
        return "Json? " + kotlinx.serialization.json.Json.encodeToString(data)
    }
}