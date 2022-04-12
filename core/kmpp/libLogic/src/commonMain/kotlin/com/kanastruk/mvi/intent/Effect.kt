package com.kanastruk.mvi.intent

/**
 * AKA side-effect.
 *
 * Useful when we need to read a model's state and generate 'unrelated' work.
 *
 * I often find this 'unrelated work' involves triggering an Intent on a different model.
 */
fun interface Effect<S> {
    fun effect(current:S):Unit
}