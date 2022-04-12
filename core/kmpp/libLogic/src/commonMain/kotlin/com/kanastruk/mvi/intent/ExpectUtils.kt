package com.kanastruk.mvi.intent

import com.kanastruk.shared.logW

/**
 * E:Expected child, SC: Sealed class parent.
 * When building a state machine using the sealed class approach, 'expecting()'
 * allows you to create Intents that will log a warning and no-op when
 * trying to execute an intent off of the wrong 'parent' (or 'old') state.
 */
inline fun <reified E : SC,SC> expectingIntent(crossinline block: (E) -> SC): Intent<SC> {
   return Intent { old ->
      when (old) {
         is E -> block(old)
         else -> {
            logW("Expecting ${E::class.simpleName}, got ${old!!::class.simpleName}")
            old
         }
      }
   }
}

inline fun <SC, reified E : SC> expectingEffect(crossinline block: E.() -> Unit): Effect<SC> {
   return Effect {
      when (it) {
         is E -> it.block()
         else -> {
            logW("Expecting ${E::class.simpleName}, got ${it!!::class.simpleName}")
         }
      }
   }
}
