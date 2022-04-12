package com.kanastruk.mvi

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.kanastruk.data.core.Address
import com.kanastruk.data.core.Contact
import com.kanastruk.data.core.Profile
import com.kanastruk.mvi.fb.FbAuthState
import com.kanastruk.mvi.fb.FbAuthState.SignedIn
import com.kanastruk.mvi.fb.FbServiceModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * AndroidFbServiceModel
 */
class AndroidFbServiceModel(
    override val fbAuthStore: StateFlow<FbAuthState>
    ) : FbServiceModel {

    override suspend fun loadProfile(): Triple<Profile, Contact, Address>? {
        return ifSignedIn { signedIn ->
            coroutineScope {
                Timber.d("loadProfile(${signedIn.authId})")
                val uid = signedIn.authId
                val ref = Firebase.database.reference
                val profileRef = async {
                    ref.child("userProfile").child(uid).suspendGet { Profile() }
                }
                val contactRef = async {
                    ref.child("userContact").child(uid).suspendGet { Contact() }
                }
                val addressRef = async {
                    ref.child("userAddress").child(uid).suspendGet { Address() }
                }

                Triple(profileRef.await(), contactRef.await(), addressRef.await()).also { (p,c,a) ->
                    Timber.d("🐞 $p, $c, $a")
                }
            }
        }
    }

    override suspend fun saveUserProfile(profile: Profile, address: Address, contact: Contact) {
        // We need to be logged in, otherwise we don't have a UID to work off of.
        ifSignedIn { signedIn ->
            val uid = signedIn.authId
            val ref = Firebase.database.reference
            coroutineScope {
                val results = awaitAll(
                    async { ref.child("userProfile").child(uid).suspendSet(profile) },
                    async { ref.child("userContact").child(uid).suspendSet(contact) },
                    async { ref.child("userAddress").child(uid).suspendSet(address) }
                )
                if( !results.all { success -> success } ) {
                    Timber.e("Failed to save part(s) of the user profile.")
                }
            }
        }
    }

    private suspend fun <T> ifSignedIn(block:suspend (SignedIn)->T):T? {
        return when ( val state = fbAuthStore.value ) {
            is SignedIn -> {
                block(state)
            }
            else -> {
                Timber.e("Can't complete request, expecting FbAuthState.SignedIn, was ${fbAuthStore.value}.")
                null
            }
        }
    }

}

/**
 * Returns true/false depending is set() succeeded.
 */
suspend fun DatabaseReference.suspendSet(value: Any?): Boolean {
    return suspendCoroutine { continuation ->
        setValue(value)
            .addOnSuccessListener { continuation.resume(true) }
            .addOnFailureListener { continuation.resume(false) }
    }
}

suspend inline fun <reified T> DatabaseReference.suspendGet(crossinline default: () -> T): T {
    return suspendCoroutine { continuation ->
        get()
            .addOnSuccessListener { snap ->
                val contact = snap.getValue<T>() ?: default()
                continuation.resume(contact)
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
}

class SimplisticAndroidFbServiceModel:FbServiceModel {
    // Ignored for the example.
    override val fbAuthStore: StateFlow<FbAuthState>
        get() = TODO("Not yet implemented")

    override suspend fun loadProfile(): Triple<Profile, Contact, Address>? {
        // Naively assume everything has been setup elsewhere for us.
        val currentUid = Firebase.auth.uid
        return if( currentUid != null ) {
            coroutineScope {
                Timber.d("loadProfile(${currentUid})")
                val ref = Firebase.database.reference
                val profileRef = async {
                    ref.child("userProfile").child(currentUid).suspendGet { Profile() }
                }
                val contactRef = async {
                    ref.child("userContact").child(currentUid).suspendGet { Contact() }
                }
                val addressRef = async {
                    ref.child("userAddress").child(currentUid).suspendGet { Address() }
                }

                Triple(profileRef.await(), contactRef.await(), addressRef.await()).also { (p,c,a) ->
                    Timber.d("🐞 $p, $c, $a")
                }
            }
        } else {
            null
        }
    }

    override suspend fun saveUserProfile(profile: Profile, address: Address, contact: Contact) {
        // Naively assume everything has been setup elsewhere for us.
        val uid = Firebase.auth.uid
        if (uid != null) {
            val ref = Firebase.database.reference
            coroutineScope {
                val results = awaitAll(
                    async { ref.child("userProfile").child(uid).suspendSet(profile) },
                    async { ref.child("userContact").child(uid).suspendSet(contact) },
                    async { ref.child("userAddress").child(uid).suspendSet(address) }
                )
                if (!results.all { success -> success }) {
                    Timber.e("A problem occurred while save user profile.")
                }
            }
        }
    }

}

