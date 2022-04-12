package com.kanastruk.mvi

sealed class UserProfileViewEvent {
    object OpenEditor: UserProfileViewEvent()
    data class EditField(val userProfileField: UserProfileField, val value: String) : UserProfileViewEvent()
    object Save: UserProfileViewEvent()
    object Cancel: UserProfileViewEvent()
}