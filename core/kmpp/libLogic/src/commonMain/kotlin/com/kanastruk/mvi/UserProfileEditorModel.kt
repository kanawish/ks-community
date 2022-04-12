package com.kanastruk.mvi

import com.kanastruk.data.core.Profile
import com.kanastruk.mvi.UserProfileEditorState.*
import com.kanastruk.mvi.fb.FbServiceModel
import com.kanastruk.mvi.intent.Intent
import com.kanastruk.mvi.intent.expectingIntent
import com.kanastruk.shared.logE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 'Transactional' version
 *
 * NOTE: For this Model, I'm trying a bit of a mix of programming styles for
 *   our state machine. First, state transitions will be defined as
 *   'intent builder' functions. See openIntent() and saveIntent() as
 *   examples. Second, when I need some state to be 'captured', I think
 *   the "Editing" state is a good example of doing that via passing
 *   in a function, instead of polluting the Editing data class with
 *   a bunch of private values.
 *
 */
class UserProfileEditorModel(
    private val fbServiceModel: FbServiceModel,
    private val scope: CoroutineScope = MainScope()
) {
    private val _store: MutableStateFlow<UserProfileEditorState> = MutableStateFlow(Closed)
    val store: StateFlow<UserProfileEditorState> = _store.asStateFlow()

    /**
     * For now, open intent will get UID from an assumed Auth 'signedIn' state from
     * the FirebaseServiceModel.
     */
    fun openIntent() = expectingIntent { _: Closed ->
        scope.launch {
            // TODO: Use srcProfile and add it to editing.
            fbServiceModel.loadProfile()?.let { (srcProfile, srcContact, srcAddress) ->
                val opened = expectingIntent { _: Opening ->
                    Editing(srcContact, srcAddress) {
                        // Compare with "source" contact & address.
                        srcContact != toContact() || srcAddress != toAddress()
                    }
                }
                process(opened)
            } ?: process { Closed } // TODO: Silent fail, better user feedback would be nice.
        }
        Opening
    }

    /**
     * Create an intent to edit a field.
     */
    fun editIntent(targetField: UserProfileField, newValue: String)
            : Intent<UserProfileEditorState> =
        expectingIntent { editing: Editing ->
            val oldFieldState = editing.fieldMap[targetField]
                ?: throw Exception("UserProfileField not found in fieldMap.") // Fail fast.
            val newEntry = targetField to oldFieldState.copy(text = newValue)
            // Copy, then apply validation steps.
            editing.copy(fieldMap = editing.fieldMap + newEntry).validateFields().validateForm()
        }

    /**
     * Launches a save process, transitions us to 'Saving' while we wait for results.
     */
    fun saveIntent(): Intent<UserProfileEditorState> =
        expectingIntent { editing: Editing ->
            if (editing.valid) {
                // Launches coroutine to save the profile, transitioning to "Closed" when done.
                scope.launch {
                    editing.apply {
                        // TODO: Add Profile instance to editing model.
                        fbServiceModel.saveUserProfile(Profile(), toAddress(), toContact())
                        // And we're done.
                        val saved = expectingIntent { _: Saving -> Closed }
                        process(saved)
                    }
                }
                Saving
            } else {
                logE("ERROR: Save attempt was allowed with editing.valid == false.")
                // NOTE: We just 'ignore' here, but this case would be a programming error.
                editing
            }
        }

    /**
     * Drop everything to the floor, transitions to "Closed" and doesn't save the edit form contents.
     */
    fun cancelIntent(): Intent<UserProfileEditorState> = expectingIntent { _:Editing -> Closed }

    fun process(intent: Intent<UserProfileEditorState>) {
        _store.value = intent.reduce(_store.value)
    }
}

