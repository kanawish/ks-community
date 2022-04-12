package com.kanastruk.mvi

import com.kanastruk.data.core.Address
import com.kanastruk.data.core.Contact
import com.kanastruk.mvi.UserProfileField.*
import com.kanastruk.shared.logD

sealed class UserProfileEditorState {

    object Closed : UserProfileEditorState()

    object Opening : UserProfileEditorState()

    data class Editing(
        val fieldMap: Map<UserProfileField, TextFieldState> = emptyFields(),
        val valid: Boolean = true,
        // Idea here is, function can carry over source state, and check if copies are dirty.
        private val dirtyCheck: Editing.() -> Boolean = { false }
    ) : UserProfileEditorState() {
        constructor(
            contact: Contact, address: Address, dirtyCheck: Editing.() -> Boolean
        ) : this(editorStateOf(contact, address), true, dirtyCheck)

        fun isDirty(): Boolean = dirtyCheck()
    }

    object Saving: UserProfileEditorState()
}

fun emptyFields(): Map<UserProfileField, TextFieldState> {
    return values().associate { field -> field to TextFieldState() }
}

fun editorStateOf(contact: Contact, address: Address): Map<UserProfileField, TextFieldState> =
    mapOf(
        FIRST to contact.firstName,
        LAST to contact.lastName,
        COMPANY to contact.companyName,
        EMAIL to contact.email,
        PHONE to contact.phone,
        ADDRESS1 to address.address1,
        ADDRESS2 to address.address2,
        CITY to address.city,
        COUNTRY_SUB to address.countrySub,
        POSTAL to address.postalCode,
        COUNTRY to address.country
    ).mapValues { value -> TextFieldState(value.value) }

/**
 * Convert to 'core' Contact data class
 */
fun UserProfileEditorState.Editing.toContact():Contact {
    return Contact(
        fieldMap[FIRST]!!.text, fieldMap[LAST]!!.text,
        fieldMap[COMPANY]!!.text,
        fieldMap[EMAIL]!!.text, fieldMap[PHONE]!!.text
    )
}

/**
 * Convert to 'core' Address data class
 */
fun UserProfileEditorState.Editing.toAddress():Address {
    return Address(
        fieldMap[ADDRESS1]!!.text, fieldMap[ADDRESS2]!!.text,  fieldMap[CITY]!!.text,
        fieldMap[COUNTRY_SUB]!!.text, fieldMap[POSTAL]!!.text, fieldMap[COUNTRY]!!.text
    )
}

/**
 * Validation of individual fields.
 */
fun UserProfileEditorState.Editing.validateFields(): UserProfileEditorState.Editing {
    return copy(
        fieldMap = fieldMap + mapOf(
            EMAIL to (fieldMap[EMAIL]!!.validateEmail()),
            POSTAL to fieldMap[POSTAL]!!.validatePostal() // TODO: should validate in country-specific way.
        )
    )
}

/**
 * Validation of the whole form. (Useful if you need to validate fields against each other)
 */
fun UserProfileEditorState.Editing.validateForm(): UserProfileEditorState.Editing {
    return copy(valid = fieldMap.values.count { it.errorMsg != null } > 0)
}

