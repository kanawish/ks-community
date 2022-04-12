package mvi.fb

import firebase.database.DataSnapshot
import firebase.database.Database
import firebase.database.Query
import firebase.database.Reference
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onClosed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import utils.debug
import utils.verbose
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.js.Json
import kotlin.js.Promise

/**
 * Creates a Flow<T> for a given Firebase reference.
 */
fun <T> Database.refFlow(path: String): Flow<T> = ref(path).asFlow()

fun <T> Query.asFlow(): Flow<T> = callbackFlow {
   val onValueChange = on(
      "value",
      { snap, _ ->
         val value = snap.`val`().unsafeCast<T>()
         trySend(value)
            .onClosed { throw it ?: ClosedSendChannelException("Channel was closed normally") }
            .isSuccess
      },
      { err: Error -> console.error("Caught error for ref %o: %o.", this@asFlow.ref, err) }
   )

   awaitClose {
      verbose { console.log("awaitClose called for ${this@asFlow.ref} as Flow") }
      off("value", onValueChange)
   }
}

/**
 * see https://discuss.kotlinlang.org/t/how-do-you-handle-undefined-on-dynamic-value/9247/9
 * re: handling of dynamic null/undefined.
 *
 * NOTE: Perf improvements possible if we also the `child_*` event types.
 *   For now, complexity doesn't seem worth it.
 */
fun <T> Query.asMapFlow(): Flow<Map<String, T>> = callbackFlow {
   val onValueChange = on(
      "value",
      { snap, _ ->
         val value = snap.`val`()
         if (value) { // Javascriptism
            val map: Map<String, T> = asTypedMap(snap.`val`())
            trySend(map)
               .onClosed { throw it ?: ClosedSendChannelException("Channel was closed normally") }
               .isSuccess
         } else {
            verbose { println("snap was null/undefined for ${snap.ref.key} / ${snap.ref}") }
         }
      },
      { err: Error -> console.error("Caught error for ref %o: %o.", this@asMapFlow.ref, err) }
   )

   awaitClose {
      verbose { console.log("awaitClose called for ${this@asMapFlow.ref} asMapFlow()") }
      off("value", onValueChange)
   }
}

fun <T> Query.asMapOrEmptyFlow(): Flow<Map<String, T>> = callbackFlow {
   val onValueChange = on(
      "value",
      { snap, _ ->
         val value = snap.`val`()
         if (value) { // Javascriptism
            val map: Map<String, T> = asTypedMap(snap.`val`())
            trySend(map)
               .onClosed { throw it ?: ClosedSendChannelException("Channel was closed normally") }
               .isSuccess
         } else {
            verbose { println("snap was null/undefined for ${snap.ref.key} / ${snap.ref}, returning emptyMap()") }
            trySend(emptyMap<String, T>())
               .onClosed { throw it ?: ClosedSendChannelException("Channel was closed normally") }
               .isSuccess
         }
      },
      { err: Error -> console.error("Caught error for ref %o: %o.", this@asMapOrEmptyFlow.ref, err) }
   )

   awaitClose {
      verbose { log("awaitClose called for ${this@asMapOrEmptyFlow.ref} asMapOrEmptyFlow()") }
      off("value", onValueChange)
   }
}

suspend fun Reference.suspendSet(value: Json):Boolean {
   return suspendCoroutine { continuation ->
      debug { console.log("for ref %o", this@suspendSet) }
      set(value) { err: Error? ->
         if (err != null) {
            console.error("suspendSet(%o) failed with:", value)
            console.error(err)
            continuation.resume(false)
         } else {
            debug { console.log("suspendSet %o", value) }
            continuation.resume(true)
         }
      }
   }
}

suspend fun <T> Query.suspendGet():T = suspendCoroutine { continuation ->
   get()
      .then { snap ->
         val value = snap.`val`().unsafeCast<T>()
         continuation.resume(value)
      }.catch { throwable ->
         continuation.resumeWithException(throwable)
      }
}

suspend fun <T> Query.suspendGetAsMap():Map<String,T> = suspendCoroutine { continuation ->
   get()
      .then { snap ->
         continuation.resume(snap.toTypedMap())
      }.catch { throwable ->
         continuation.resumeWithException(throwable)
      }
}

fun <T> DataSnapshot.toTypedMap(): Map<String, T> = if (`val`() != null) {
   asTypedMap(`val`())
} else {
   mapOf()
}

suspend fun <T> Query.suspendOnce():T = suspendCoroutine { continuation ->
   val onceValueChange = once("value") { snap, _ ->
      val value = snap.`val`().unsafeCast<T>()
      continuation.resume(value)
   }.catch { throwable ->
      continuation.resumeWithException(throwable)
   }
}

suspend fun <T> Promise<Any>.suspendOnce():T = suspendCoroutine { continuation ->
   this.then { response ->
      continuation.resume(response.unsafeCast<T>())
   }.catch { throwable ->
      continuation.resumeWithException(throwable)
   }
}

suspend fun <T> Query.suspendOnceAsMap():Map<String,T> = suspendCoroutine { continuation ->
   val onceValueChange = once("value") { snap, _ ->
      continuation.resume(snap.toTypedMap())
   }.catch { throwable ->
      continuation.resumeWithException(throwable)
   }
}

fun <T> Query.onceMap(eventType:String="value",block:(Map<String,T>)->Unit):Promise<DataSnapshot> {
   return once(eventType) { snap, _ ->
      if(snap.`val`()!=null) {
         block(asTypedMap(snap.`val`()))
      } else {
         block(mapOf())
      }
   }
}

fun <T> Query.onMap(eventType:String="value",block:(Map<String,T>)->Unit):(DataSnapshot?, String?) -> Any {
   return on(eventType, { snap, _ ->
      if (snap.`val`() != null) {
         block(asTypedMap(snap.`val`()))
      } else {
         block(mapOf())
      }
   })
}

/**
 * Helper to parse incoming data from Firebase
 *
 * see:
 * https://discuss.kotlinlang.org/t/js-interop-for-in-and-for-of-loop-of-dynamic-objects/7507/5
 * also see:
 * https://stackoverflow.com/questions/52247111/modelling-external-js-objects
 */
fun <T> asTypedMap(source:dynamic): Map<String, T> = js("Object")
   .entries(source)
   .unsafeCast<Array<Array<dynamic>>>()
   .map {
      it[0].unsafeCast<String>() to it[1].unsafeCast<T>()
   }.toMap()

fun dumpJson(source:dynamic) = js("Object")
   .entries(source)
   .unsafeCast<Array<Array<dynamic>>>()
   .forEach { console.log(it) }