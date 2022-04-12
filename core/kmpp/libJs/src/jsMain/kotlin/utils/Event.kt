package utils

import org.w3c.dom.events.Event

inline fun <reified T> target(crossinline block:(T)->Unit):(Event)->Unit = { e ->
    when(val target = e.currentTarget) {
        is T -> block(target)
        else -> console.error("Wrong target type.")
    }
}

inline fun <reified T> eventTarget(crossinline block:(Event, T)->Unit):(Event)->Unit = { e ->
    when(val target = e.currentTarget) {
        is T -> block(e,target)
        else -> console.error("Wrong target type.")
    }
}
