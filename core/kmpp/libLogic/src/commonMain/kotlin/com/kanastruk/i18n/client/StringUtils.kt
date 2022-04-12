package com.kanastruk.i18n.client

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun String.toClientStrings(): ClientStrings =
    Json.decodeFromString(this)
