package com.kanastruk.mvi

private const val EMAIL_VALIDATION_REGEX = "^(.+)@(.+)\$"
/**
 * Will refresh the error message associated with textFieldState
 */
fun TextFieldState.validateEmail(): TextFieldState {
    val regex = Regex(EMAIL_VALIDATION_REGEX)
    val valid = regex.matches(text)
    val msg = if (!valid) "Invalid email: $text" else ""
    return this.copy(errorMsg = msg)
}

private const val POSTAL_CODE_VALIDATION_REGEX = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]\$"
/**
 * Will refresh the error message associated with textFieldState
 */
fun TextFieldState.validatePostal(): TextFieldState {
    val regex = Regex(POSTAL_CODE_VALIDATION_REGEX)
    val valid = regex.matches(text)
    val msg = if (!valid) "Invalid postal code: $text" else ""
    return this.copy(errorMsg = msg)
}