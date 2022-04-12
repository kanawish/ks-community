package com.kanastruk.mvi

fun UserProfileEditorModel.dispatch(userProfileViewEvent: UserProfileViewEvent) {
    when (userProfileViewEvent) {
        is UserProfileViewEvent.EditField -> {
            val (field, newValue) = userProfileViewEvent
            process(editIntent(field, newValue))
        }
        UserProfileViewEvent.Cancel -> process(cancelIntent())
        UserProfileViewEvent.OpenEditor -> process(openIntent())
        UserProfileViewEvent.Save -> process(saveIntent())
    }
}