package com.kanastruk.mvi.ui

import com.kanastruk.data.core.Address
import com.kanastruk.data.core.Contact
import com.kanastruk.i18n.client.ClientStrings
import com.kanastruk.mvi.*
import com.kanastruk.mvi.UserProfileField.*
import injectors
import kotlinx.browser.document
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.injector.inject
import kotlinx.html.js.div
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onSubmitFunction
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.HTMLInputElement
import unaryPlus
import utils.debug
import utils.mapDistinct
import utils.target
import kotlin.properties.Delegates

class UserProfileEditView(labels:Map<UserProfileField,String?>, i18n:ClientStrings) :
    ViewComponent<UserProfileEditorState, UserProfileViewEvent>(){

    private val _events = MutableSharedFlow<UserProfileViewEvent>()

    private var form: HTMLFormElement by Delegates.notNull()
    private var companyName: HTMLInputElement by Delegates.notNull()
    private var firstName: HTMLInputElement by Delegates.notNull()
    private var lastName: HTMLInputElement by Delegates.notNull()
    private var email: HTMLInputElement by Delegates.notNull()
    private var phone: HTMLInputElement by Delegates.notNull()

    private var address1: HTMLInputElement by Delegates.notNull()
    private var address2: HTMLInputElement by Delegates.notNull()
    private var city: HTMLInputElement by Delegates.notNull()
    private var countrySub: HTMLInputElement by Delegates.notNull()
    private var postalCode: HTMLInputElement by Delegates.notNull()
    private var country: HTMLInputElement by Delegates.notNull()

    private var cancelButton: HTMLButtonElement by Delegates.notNull()
    private var saveButton: HTMLButtonElement by Delegates.notNull()

    private val parentInjectors = injectors(
        UserProfileEditView::form,
        UserProfileEditView::firstName,
        UserProfileEditView::lastName,
        UserProfileEditView::companyName,
        UserProfileEditView::email,
        UserProfileEditView::phone,
        UserProfileEditView::address1,
        UserProfileEditView::address2,
        UserProfileEditView::city,
        UserProfileEditView::countrySub,
        UserProfileEditView::postalCode,
        UserProfileEditView::country,
        UserProfileEditView::cancelButton,
        UserProfileEditView::saveButton,
    )

    /**
     *  This type of builder mostly makes things easier to read.
     */
    private fun DIV.formFieldDiv(
        columns: Int = 6,
        inputType: InputType = InputType.text,
        inputClassKey: String,
        label: String? = null,
        isRequired: Boolean = true,
        inputPattern: String? = null,
        invalidFeedback: String? = null,
        changeHandler: (HTMLInputElement) -> Unit
    ) {
        div("mb-1 col-$columns") {
            label?.also {
                label("mb-0") { htmlFor = inputClassKey; small { +label } }
            }
            input(inputType, classes = "form-control") {
                classes += inputClassKey
                if( isRequired ) required = true
                inputPattern?.also { p -> this@input.pattern = p }
                onChangeFunction = target(changeHandler)
            }
            invalidFeedback?.also {
                this@formFieldDiv.div(classes = "invalid-feedback") { +invalidFeedback }
            }
        }
    }

    private fun emitEdit(field: UserProfileField): (HTMLInputElement) -> Unit {
        return { launch { _events.emit(UserProfileViewEvent.EditField(field, it.value)) } }
    }

    /**
     * TODO: Convert to Compose, we'll be able to react on the fly to label changes.
     *   We can't in this current version.
     */
    val parentElement: HTMLElement = document.create.inject(this, parentInjectors).div {
        classes+="p-3"
        // CONTACT SECTION
        form {
            classes += +UserProfileEditView::form
            onSubmitFunction = { event -> event.preventDefault() }

            div("row") {
                formFieldDiv(
                    columns = 6,
                    inputClassKey = +UserProfileEditView::firstName,
                    label = labels.getValue(FIRST),
                    invalidFeedback = "Requis",
                    changeHandler = emitEdit(FIRST)
                )
                formFieldDiv(
                    columns = 6,
                    inputClassKey = +UserProfileEditView::lastName,
                    label = labels.getValue(LAST),
                    invalidFeedback = "Requis",
                    changeHandler = emitEdit(LAST)
                )
            }
            div("row") {
                formFieldDiv(
                    columns = 12,
                    inputClassKey = +UserProfileEditView::companyName,
                    label = labels.getValue(COMPANY),
                    isRequired = false,
                    invalidFeedback = "Requis",
                    changeHandler = emitEdit(COMPANY)
                )
            }
            div("row mb-1") {
                formFieldDiv(
                    columns = 6,
                    inputClassKey = +UserProfileEditView::phone,
                    label = labels.getValue(PHONE),
                    inputPattern = "[0-9()*# -]{10,30}",
                    invalidFeedback = "No. téléphone valide requis",
                    changeHandler = emitEdit(PHONE)
                )
                formFieldDiv(
                    columns = 6,
                    inputClassKey = +UserProfileEditView::email,
                    label = labels.getValue(EMAIL),
                    invalidFeedback = "Courriel valide requis",
                    changeHandler = emitEdit(EMAIL)
                )
            }
            // ADDRESS SECTION
            div("row mb-1") {
                formFieldDiv(
                    columns = 12,
                    inputClassKey = +UserProfileEditView::address1,
                    label = labels.getValue(ADDRESS1),
                    invalidFeedback = "Requis",
                    changeHandler = emitEdit(ADDRESS1)
                )
                formFieldDiv(
                    columns = 12,
                    inputClassKey = +UserProfileEditView::address2,
                    isRequired = false,
                    changeHandler = emitEdit(ADDRESS2)
                )
            }
            div("row") {
                formFieldDiv(
                    columns = 6,
                    inputClassKey = +UserProfileEditView::city,
                    label = labels.getValue(CITY),
                    invalidFeedback = "Requis",
                    changeHandler = emitEdit(CITY)
                )
                // TODO: Country Sub...
                formFieldDiv(
                    columns = 6,
                    inputClassKey = +UserProfileEditView::postalCode,
                    label = labels.getValue(POSTAL),
                    inputPattern = "[A-Za-z][0-9][A-Za-z][ ]?[0-9][A-Za-z][0-9]",
                    invalidFeedback = "Code postal valide requis",
                    changeHandler = emitEdit(POSTAL)
                )
            }
            // TODO: Country...
            div("row") {
                div("col-12 mt-1 d-flex justify-content-end") {
                    button(classes = "btn btn-secondary me-2") {
                        +i18n.profileEditor.cancel
                        classes += +UserProfileEditView::cancelButton
                        onClickFunction = target<HTMLButtonElement>{ e ->
                            launch {
                                _events.emit(UserProfileViewEvent.Cancel)
                            }
                        }
                    }
                    button(classes = "btn btn-primary") {
                        // type = ButtonType.submit
                        +i18n.profileEditor.save
                        classes += +UserProfileEditView::saveButton
                        onClickFunction = target<HTMLButtonElement>{
                            launch {
                                _events.emit(UserProfileViewEvent.Save)
                            }
                        }
                    }
                }
            }
        } // form
    }

    override fun attach(model: StateFlow<UserProfileEditorState>, handler: (UserProfileViewEvent) -> Unit) {
        // TODO: Add a 'spinner' handling when not Editing.
        model
            .filterIsInstance<UserProfileEditorState.Editing>()
            .apply {
                // TODO: Attach model error feedback instead.
                mapDistinct { it.fieldMap[FIRST]?.errorMsg }.launchCollect {
                    when {
                        it == null -> {
                            firstName.removeClass("is-valid")
                            firstName.removeClass("is-invalid")
                        }
                        it.isNotBlank() -> {
                            firstName.removeClass("is-valid")
                            firstName.addClass("is-invalid")
                        }
                        else -> {
                            firstName.addClass("is-valid")
                            firstName.removeClass("is-invalid")
                        }
                    }
                }
                mapDistinct { it.toContact() }.attach()
                mapDistinct { it.toAddress() }.attach()
                debug {
                    launchCollect { state ->
                        console.log("state: %o", state)
                        state.fieldMap.forEach { (k,v) ->
                            console.log("k: %o, v: %o", k, v)
                        }
                    }
                }
            }

        launch { _events.collect { handler(it) } }
    }

    /**
     * Assign each distinct entry to an html input field.
     */
    private fun Flow<Contact?>.attach() {
        assignDistinct(Contact::firstName, firstName)
        assignDistinct(Contact::lastName, lastName)
        assignDistinct(Contact::companyName,companyName )
        assignDistinct(Contact::email,email)
        assignDistinct(Contact::phone,phone)
    }

    private fun Flow<Address?>.attach() {
        assignDistinct(Address::address1,address1)
        assignDistinct(Address::address2,address2)
        assignDistinct(Address::city,city)
//        assignDistinct(Address::countrySub,countrySub)
        assignDistinct(Address::postalCode,postalCode)
//        assignDistinct(Address::country,country)
    }

}