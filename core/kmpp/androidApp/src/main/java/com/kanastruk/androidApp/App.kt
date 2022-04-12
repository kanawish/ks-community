package com.kanastruk.androidApp

import android.app.Application
import com.kanastruk.mvi.AndroidFbServiceModel
import com.kanastruk.mvi.fb.FirebaseModel
import com.kanastruk.mvi.UserProfileEditorModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import timber.log.Timber.DebugTree

/**
 * https://medium.com/androiddevelopers/coroutines-patterns-for-work-that-shouldnt-be-cancelled-e26c40f142ad
 */
class App : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    private val applicationScope = CoroutineScope(SupervisorJob())// + otherConfig)

    // TODO: Injection & scope handling
    val firebaseModel by lazy { FirebaseModel() }
    val editUserProfileModel by lazy {
        UserProfileEditorModel(
            AndroidFbServiceModel(firebaseModel.store),
            applicationScope
        )
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}