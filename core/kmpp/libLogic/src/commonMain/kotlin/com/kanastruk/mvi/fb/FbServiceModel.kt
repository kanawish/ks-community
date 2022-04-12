package com.kanastruk.mvi.fb

import com.kanastruk.data.core.Address
import com.kanastruk.data.core.Contact
import com.kanastruk.data.core.Profile
import kotlinx.coroutines.flow.StateFlow

/**
 * ProfileService interface, each platform implements their own version of this.
 */
interface FbServiceModel {
    /**
     * Model users will need up-to-date Auth State to interact with Firebase.
     */
    val fbAuthStore: StateFlow<FbAuthState>

    /**
     * Loads a profile in whatever way fits the target platform.
     */
    suspend fun loadProfile(): Triple<Profile, Contact, Address>?
    /**
     * Saves a profile in whatever way fits the target platform.
     */
    suspend fun saveUserProfile(profile: Profile, address: Address, contact: Contact)

}