package com.kanastruk.mvi

import com.kanastruk.data.core.Address
import com.kanastruk.data.core.Contact
import com.kanastruk.data.core.Profile
import com.kanastruk.mvi.fb.FbAuthState
import com.kanastruk.mvi.fb.FbServiceModel
import firebase.database.Query
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.StateFlow
import mvi.fb.FbServices
import mvi.fb.suspendGet
import mvi.fb.suspendSet
import utils.debug
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.js.Json
import kotlin.js.json

/**
 * NOTE: Comparing Android and Js implementations, we can see they're very similar looking.
 *   That's a side effect of Firebase having very closely matching APIs between JS and JVM
 *   implementations.
 *
 * TODO: To improve code re-use consider writing a 'common' shim over
 *   Firebase libraries, when we have enough time to do so.
 */
class JsFbServiceModel(
    private val fbServices: FbServices,
    override val fbAuthStore: StateFlow<FbAuthState>
) :FbServiceModel {
    private val profileRef by lazy { fbServices.firebaseDb.ref("userProfile") }
    private val contactRef by lazy { fbServices.firebaseDb.ref("userContact") }
    private val addressRef by lazy { fbServices.firebaseDb.ref("userAddress") }

    override suspend fun loadProfile(): Triple<Profile, Contact, Address>? {
        console.log("JsFbServiceModel.loadProfile() called")
        return ifSignedIn { signedIn ->
            coroutineScope {
                val profile = async { profileRef.child(signedIn.authId).suspendGet<Json?>()?.toProfile() }
                val contact = async { contactRef.child(signedIn.authId).suspendGet<Json?>()?.toContact() }
                val address = async { addressRef.child(signedIn.authId).suspendGet<Json?>()?.toAddress() }

                Triple(
                    profile.await() ?: Profile(),
                    contact.await() ?: Contact(),
                    address.await() ?: Address()
                ).also { (p,c,a) ->
                    debug { console.log("🐞 %o, %o, %o", p, c, a) }
                }
            }
        }
    }

    private fun Json.toProfile():Profile = Profile(
        this.get("locale") as? String ?: ""
    )
    fun Profile.toJson():Json = json(
        "locale" to locale
    )

    private fun Json.toContact():Contact = Contact(
        this["firstName"] as? String ?: "",
        this["lastName"] as? String ?:"",
        this["companyName"] as? String ?:"",
        this["email"] as? String ?:"",
        this["phone"] as? String ?:""
    )
    fun Contact.toJson():Json = json(
        "firstName" to firstName,
        "lastName" to lastName,
        "companyName" to companyName,
        "email" to email,
        "phone" to phone
    )

    private fun Json.toAddress():Address = Address(
        this["address1"] as? String ?: "",
        this["address2"] as? String ?:"",
        this["city"] as? String ?:"",
        this["countrySub"] as? String ?:"",
        this["postalCode"] as? String ?:"",
        this["country"] as? String ?:"",
    )
    fun Address.toJson():Json = json(
        "address1" to address1,
        "address2" to address2,
        "city" to city,
        "countrySub" to countrySub,
        "postalCode" to postalCode,
        "country" to country
    )

/*
    suspend fun Query.suspendGet():Json? = suspendCoroutine { continuation ->
        get()
            .then { snap ->
                val json = snap.`val`().unsafeCast<Json?>()
                continuation.resume(json)
            }.catch { throwable ->
                continuation.resumeWithException(throwable)
            }
    }
*/

    fun foo() {

        val default = Address()

    }

    override suspend fun saveUserProfile(profile: Profile, address: Address, contact: Contact) {
        console.log("JsFbServiceModel.saveUserProfile() called")
        console.log("🐞 %o, %o, %o", profile, contact, address)

        ifSignedIn { signedIn ->
            coroutineScope {
                val results = awaitAll(
                    async { profileRef.child(signedIn.authId).suspendSet(profile.toJson()) },
                    async { contactRef.child(signedIn.authId).suspendSet(contact.toJson()) },
                    async { addressRef.child(signedIn.authId).suspendSet(address.toJson()) }
                )
                if( !results.all { success -> success } ) {
                    console.error("Failed to save part(s) of the user profile.")
                }
            }
        }
    }

    private suspend fun <T> ifSignedIn(block:suspend (FbAuthState.SignedIn)->T):T? {
        return when ( val state = fbAuthStore.value ) {
            is FbAuthState.SignedIn -> {
                block(state)
            }
            else -> {
                console.error(
                    "Can't complete request, expecting FbAuthState.SignedIn, was %o.",
                    fbAuthStore.value
                )
                null
            }
        }
    }

}
