import io.kvision.jquery.JQueryStatic
import io.kvision.jquery.invoke
import kotlinx.html.*
import org.w3c.dom.HTMLElement

fun TagConsumer<HTMLElement>.bootstrapTableTest() {
    table {
        id = "bootstrapTestTable"
        attributes["data-toggle"]="table"
        attributes["data-url"]="json/data1.json"
        attributes["data-pagination"]="true"
        attributes["data-search"]="true"
        attributes["data-use-row-attr-func"]="true"
        attributes["data-reorderable-rows"]="true"
        thead {
            tr {
                th {
                    +"Item ID"
                    attributes["data-field"] = "id"
                    attributes["data-sortable"] = "true"
                }
                th {
                    +"Item Name"
                    attributes["data-field"] = "name"
                }
                th {
                    +"Item Price"
                    attributes["data-field"] = "price"
                }
            }
        }
    }
}

fun initBootstrapTableTest() {
    val bar = io.kvision.jquery.jQuery.invoke("#bootstrapTestTable")
    console.log("bootstrapTestTable: %o", bar)
    val baz = bar.asDynamic().bootstrapTable("getData").unsafeCast<JQueryStatic>()
    console.log("getData(): %o", io.kvision.jquery.jQuery.makeArray(baz))
}
