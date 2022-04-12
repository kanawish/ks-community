import kotlinx.browser.document
import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.injector.InjectByClassName
import kotlinx.html.injector.InjectCapture
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import kotlin.reflect.KMutableProperty1

/**
 * Allows for easy classes += +Foo::bar wrt injectors
 */
operator fun <T, V> KMutableProperty1<T, V>.unaryPlus(): String = this.name

/**
 * Per-item mapper
 */
fun <T:Any> KMutableProperty1<T, out HTMLElement>.injector(): Pair<InjectCapture, KMutableProperty1<T, out HTMLElement>> =
    InjectByClassName(this.name) to this

/**
 * Small helper that maps mutable property accessors to a List of name-accessor pairs.
 */
fun <T:Any> injectors(vararg elements: KMutableProperty1<T, out HTMLElement>): List<Pair<InjectCapture, KMutableProperty1<T, out HTMLElement>>> =
    elements.map { it.injector() }

data class ModalLabels(
    val title:String,
    val ok:String,
    val cancel:String
)

/**
 * modalId needs to be used to trigger modal, via attribues:
 *
 * attributes["data-toggle"]="modal"
 * attributes["data-target"]="#$modalReplacementId"
 */
fun modalBuilder(modalId: String, labels: ModalLabels, contentDiv: DIV.()->Unit, okHandler: (Any) -> Unit): HTMLDivElement {
    return document.create.div { modal(modalId,labels,contentDiv,okHandler) }
}

fun DIV.modal(modalId: String, labels: ModalLabels, contentDiv: DIV.()->Unit, okHandler: (Any) -> Unit) {
    classes+="modal fade text-dark"
    id = modalId
    div("modal-dialog modal-dialog-centered") {
        div("modal-content") {
            div("modal-header") {
                h5("modal-title") { +labels.title }
                button(type = ButtonType.button, classes = "btn-close") {
                    attributes["data-bs-dismiss"] = "modal"
                }
            }
            div("modal-body", contentDiv)
            div("modal-footer") {
                button(classes = "btn btn-secondary") {
                    +labels.cancel
                    attributes["data-bs-dismiss"] = "modal"
                }
                button(classes = "btn btn-primary") {
                    +labels.ok
                    attributes["data-bs-dismiss"] = "modal"
                    onClickFunction = okHandler
                }
            }
        }
    }
}

fun HTMLTag.modalTrigger(modalId:String) {
    attributes["data-bs-toggle"]="modal"
    attributes["data-bs-target"]="#$modalId"
}