@file:JsQualifier("firebase.functions")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package firebase.functions

import kotlin.js.Promise

external interface HttpsCallableResult {
    var data: Any
}

external interface HttpsCallable {
    @nativeInvoke
    operator fun invoke(data: Any = definedExternally): Promise<HttpsCallableResult>
}

external interface HttpsCallableOptions {
    var timeout: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface Functions {
    fun useEmulator(host: String, port: Number)
    fun useFunctionsEmulator(url: String)
    fun httpsCallable(name: String, options: HttpsCallableOptions = definedExternally): HttpsCallable
}