import kotlinx.browser.document
import kotlinx.html.TagConsumer
import kotlinx.html.id
import kotlinx.html.li
import kotlinx.html.ul
import org.w3c.dom.HTMLElement
import kotlin.js.json

// https://stackoverflow.com/questions/46250613/how-to-convert-js-code-to-kotlin-without-the-new-keyword

@JsModule("sortablejs")
external object Sortable {
    val Sortable:dynamic
}

fun TagConsumer<HTMLElement>.sortableJsElements() {
    ul("list-group") {
        id = "items"
        li("list-group-item"){+"item 1"}
        li("list-group-item"){+"item 2"}
        li("list-group-item"){+"item 3"}
    }
}

fun initSortableJs() {
    val el = document.getElementById("items") as HTMLElement
    val p1 = kotlinext.js.js {
        animation = 150
        ghostClass = "list-group-item-primary"
    }
    val p2 = json(
        "animation" to 150,
        "ghostClass" to "list-group-item-primary"
    )
    console.log("p1 %o, p2 %o", p1, p2)
    val s = Sortable.Sortable.create(el, p2)
    console.log("%o", s)
}
