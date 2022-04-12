package utils

import kotlinx.browser.window
import org.w3c.fetch.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.js.Json

suspend fun suspendFetch(input: String): Json? {
   verbose { console.log("suspendFetch($input)") }
   return suspendCoroutine { continuation ->
      window.fetch(input)
         .then { res: Response -> res.json().unsafeCast<Json>() }
         .then { json: Json -> continuation.resume(json) }
         .catch { throwable ->
            verbose { console.log(throwable) }
            continuation.resume(null)
         }
   }
}
