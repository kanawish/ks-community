import kotlinx.html.*
import org.w3c.dom.HTMLElement

sealed class DropDownItem {
    abstract fun TagConsumer<HTMLElement>.render()
    data class Item(val text:String): DropDownItem() {
        override fun TagConsumer<HTMLElement>.render() {
            // TODO: Some action needed on click...
            li { a(classes = "dropdown-item") { +text } }
        }
    }
    object Divider: DropDownItem() {
        override fun TagConsumer<HTMLElement>.render() {
            li { hr(classes = "dropdown-divider")}
        }
    }
    data class Header(val text:String): DropDownItem() {
        override fun TagConsumer<HTMLElement>.render() {
            li { h6(classes = "dropdown-header") { +text } }
        }
    }
    // TODO: More types
}

fun TagConsumer<HTMLElement>.dropdown(ddId: String, text: String, items: List<DropDownItem>) {
    div("dropdown") {
        button(classes = "btn btn-secondary dropdown-toggle", type = ButtonType.button) {
            id = ddId
            attributes["data-bs-toggle"] = "dropdown"
            attributes["aria-expanded"] = "false"
            +text
        }

        ul(classes="dropdown-menu") {
            attributes["aria-labelledby"] = ddId
            fun DropDownItem.ddRender() = run { render() }
            items.forEach(DropDownItem::ddRender)
        }
    }
}
