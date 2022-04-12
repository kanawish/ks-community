@file:JsQualifier("firebase.storage")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package firebase.storage

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*
//import firebase.performance.`T$7`
//import firebase.app.App
import firebase.FirebaseError
import firebase.NextFn
import firebase.CompleteFn
import firebase.Unsubscribe

external interface FullMetadata : UploadMetadata {
    var bucket: String
    var downloadURLs: Array<String>
    var fullPath: String
    var generation: String
    var metageneration: String
    var name: String
    var size: Number
    var timeCreated: String
    var updated: String
}

external interface Reference {
    var bucket: String
    fun child(path: String): Reference
    fun delete(): Promise<Any>
    var fullPath: String
    fun getDownloadURL(): Promise<Any>
    fun getMetadata(): Promise<Any>
    var name: String
    var parent: Reference?
    fun put(data: Blob, metadata: UploadMetadata = definedExternally): UploadTask
    fun put(data: Blob): UploadTask
    fun put(data: Uint8Array, metadata: UploadMetadata = definedExternally): UploadTask
    fun put(data: Uint8Array): UploadTask
    fun put(data: ArrayBuffer, metadata: UploadMetadata = definedExternally): UploadTask
    fun put(data: ArrayBuffer): UploadTask
    fun putString(data: String, format: StringFormat = definedExternally, metadata: UploadMetadata = definedExternally): UploadTask
    var root: Reference
    var storage: Storage
    override fun toString(): String
    fun updateMetadata(metadata: SettableMetadata): Promise<Any>
    fun listAll(): Promise<ListResult>
    fun list(options: ListOptions = definedExternally): Promise<ListResult>
}

external interface ListResult {
    var prefixes: Array<Reference>
    var items: Array<Reference>
    var nextPageToken: String?
}

external interface ListOptions {
    var maxResults: Number?
        get() = definedExternally
        set(value) = definedExternally
    var pageToken: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface SettableMetadata {
    var cacheControl: String?
        get() = definedExternally
        set(value) = definedExternally
    var contentDisposition: String?
        get() = definedExternally
        set(value) = definedExternally
    var contentEncoding: String?
        get() = definedExternally
        set(value) = definedExternally
    var contentLanguage: String?
        get() = definedExternally
        set(value) = definedExternally
    var contentType: String?
        get() = definedExternally
        set(value) = definedExternally
    /*
    var customMetadata: `T$7`?
        get() = definedExternally
        set(value) = definedExternally
    */
}

external interface Storage {
//    var app: App
    var maxOperationRetryTime: Number
    var maxUploadRetryTime: Number
    fun ref(path: String = definedExternally): Reference
    fun refFromURL(url: String): Reference
    fun setMaxOperationRetryTime(time: Number): Any
    fun setMaxUploadRetryTime(time: Number): Any
    fun useEmulator(host: String, port: Number)
}

external interface UploadMetadata : SettableMetadata {
    var md5Hash: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface FirebaseStorageError : FirebaseError {
    var serverResponse: String?
}

external interface StorageObserver<T> {
    var next: NextFn<T>?
        get() = definedExternally
        set(value) = definedExternally
    var error: ((error: FirebaseStorageError) -> Unit?)?
        get() = definedExternally
        set(value) = definedExternally
    var complete: CompleteFn?
        get() = definedExternally
        set(value) = definedExternally
}

external interface UploadTask {
    fun cancel(): Boolean
    fun catch(onRejected: (error: FirebaseStorageError) -> Any): Promise<Any>
    fun on(event: String, nextOrObserver: StorageObserver<UploadTaskSnapshot>? = definedExternally, error: ((error: FirebaseStorageError) -> Any)? = definedExternally, complete: Unsubscribe? = definedExternally): Function<*>
    fun on(event: String): Function<*>
    fun on(event: String, nextOrObserver: StorageObserver<UploadTaskSnapshot>? = definedExternally): Function<*>
    fun on(event: String, nextOrObserver: StorageObserver<UploadTaskSnapshot>? = definedExternally, error: ((error: FirebaseStorageError) -> Any)? = definedExternally): Function<*>
    fun on(event: String, nextOrObserver: ((snapshot: UploadTaskSnapshot) -> Any)? = definedExternally, error: ((error: FirebaseStorageError) -> Any)? = definedExternally, complete: Unsubscribe? = definedExternally): Function<*>
    fun on(event: String, nextOrObserver: ((snapshot: UploadTaskSnapshot) -> Any)? = definedExternally): Function<*>
    fun on(event: String, nextOrObserver: ((snapshot: UploadTaskSnapshot) -> Any)? = definedExternally, error: ((error: FirebaseStorageError) -> Any)? = definedExternally): Function<*>
    fun pause(): Boolean
    fun resume(): Boolean
    var snapshot: UploadTaskSnapshot
    fun then(onFulfilled: ((snapshot: UploadTaskSnapshot) -> Any)? = definedExternally, onRejected: ((error: FirebaseStorageError) -> Any)? = definedExternally): Promise<Any>
}

external interface UploadTaskSnapshot {
    var bytesTransferred: Number
    var downloadURL: String?
    var metadata: FullMetadata
    var ref: Reference
    var state: TaskState
    var task: UploadTask
    var totalBytes: Number
}