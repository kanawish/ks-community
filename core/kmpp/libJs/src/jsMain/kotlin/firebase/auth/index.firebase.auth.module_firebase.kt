@file:JsQualifier("firebase.auth")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package firebase.auth

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*
/*
import firebase.app.App
import firebase.User
import firebase.Observer__1
*/
import firebase.Unsubscribe

external open class ActionCodeURL {
    open var apiKey: String
    open var code: String
    open var continueUrl: String?
    open var languageCode: String?
    open var operation: Operation
    open var tenantId: String?

    companion object {
        fun parseLink(link: String): ActionCodeURL?
    }
}

external interface `T$30` {
    var email: String?
        get() = definedExternally
        set(value) = definedExternally
    var fromEmail: String?
        get() = definedExternally
        set(value) = definedExternally
    var multiFactorInfo: MultiFactorInfo?
        get() = definedExternally
        set(value) = definedExternally
    var previousEmail: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ActionCodeInfo {
    var data: `T$30`
    var operation: String
}

external interface `T$31` {
    var installApp: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var minimumVersion: String?
        get() = definedExternally
        set(value) = definedExternally
    var packageName: String
}

external interface `T$32` {
    var bundleId: String
}

external interface ActionCodeSettings {
    var android: `T$31`?
        get() = definedExternally
        set(value) = definedExternally
    var handleCodeInApp: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var iOS: `T$32`?
        get() = definedExternally
        set(value) = definedExternally
    var url: String
    var dynamicLinkDomain: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface AdditionalUserInfo {
    var isNewUser: Boolean
    var profile: Any?
    var providerId: String
    var username: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ApplicationVerifier {
    var type: String
    fun verify(): Promise<String>
}

external interface AuthSettings {
    var appVerificationDisabledForTesting: Boolean
}

external interface Auth {
    // var app: App
    fun applyActionCode(code: String): Promise<Unit>
    fun checkActionCode(code: String): Promise<ActionCodeInfo>
    fun confirmPasswordReset(code: String, newPassword: String): Promise<Unit>
    fun createUserWithEmailAndPassword(email: String, password: String): Promise<UserCredential>
    var currentUser: dynamic?
    fun fetchSignInMethodsForEmail(email: String): Promise<Array<String>>
    fun isSignInWithEmailLink(emailLink: String): Boolean
    fun getRedirectResult(): Promise<UserCredential>
    var languageCode: String?
    var settings: AuthSettings
/*
    fun onAuthStateChanged(nextOrObserver: Observer__1<Any>, error: (a: Error) -> Any = definedExternally, completed: Unsubscribe = definedExternally): Unsubscribe
    fun onAuthStateChanged(nextOrObserver: Observer__1<Any>): Unsubscribe
    fun onAuthStateChanged(nextOrObserver: Observer__1<Any>, error: (a: Error) -> Any = definedExternally): Unsubscribe
    fun onAuthStateChanged(nextOrObserver: (a: User?) -> Any, error: (a: Error) -> Any = definedExternally, completed: Unsubscribe = definedExternally): Unsubscribe
    fun onAuthStateChanged(nextOrObserver: (a: User?) -> Any): Unsubscribe
    fun onAuthStateChanged(nextOrObserver: (a: User?) -> Any, error: (a: Error) -> Any = definedExternally): Unsubscribe
    fun onIdTokenChanged(nextOrObserver: Observer__1<Any>, error: (a: Error) -> Any = definedExternally, completed: Unsubscribe = definedExternally): Unsubscribe
    fun onIdTokenChanged(nextOrObserver: Observer__1<Any>): Unsubscribe
    fun onIdTokenChanged(nextOrObserver: Observer__1<Any>, error: (a: Error) -> Any = definedExternally): Unsubscribe
    fun onIdTokenChanged(nextOrObserver: (a: User?) -> Any, error: (a: Error) -> Any = definedExternally, completed: Unsubscribe = definedExternally): Unsubscribe
    fun onIdTokenChanged(nextOrObserver: (a: User?) -> Any): Unsubscribe
    fun onIdTokenChanged(nextOrObserver: (a: User?) -> Any, error: (a: Error) -> Any = definedExternally): Unsubscribe
*/
    fun sendSignInLinkToEmail(email: String, actionCodeSettings: ActionCodeSettings): Promise<Unit>
    fun sendPasswordResetEmail(email: String, actionCodeSettings: ActionCodeSettings? = definedExternally): Promise<Unit>
    fun setPersistence(persistence: Persistence): Promise<Unit>
    fun signInAndRetrieveDataWithCredential(credential: AuthCredential): Promise<UserCredential>
    fun signInAnonymously(): Promise<UserCredential>
    fun signInWithCredential(credential: AuthCredential): Promise<UserCredential>
    fun signInWithCustomToken(token: String): Promise<UserCredential>
    fun signInWithEmailAndPassword(email: String, password: String): Promise<UserCredential>
    fun signInWithPhoneNumber(phoneNumber: String, applicationVerifier: ApplicationVerifier): Promise<ConfirmationResult>
    fun signInWithEmailLink(email: String, emailLink: String = definedExternally): Promise<UserCredential>
    fun signInWithPopup(provider: AuthProvider): Promise<UserCredential>
    fun signInWithRedirect(provider: AuthProvider): Promise<Unit>
    fun signOut(): Promise<Unit>
    var tenantId: String?
//    fun updateCurrentUser(user: User?): Promise<Unit>
    fun useDeviceLanguage()
    fun useEmulator(url: String)
    fun verifyPasswordResetCode(code: String): Promise<String>
}

external open class AuthCredential {
    open var providerId: String
    open var signInMethod: String
    open fun toJSON(): Any

    companion object {
        fun fromJSON(json: Any): AuthCredential?
        fun fromJSON(json: String): AuthCredential?
    }
}

external open class OAuthCredential : AuthCredential {
    open var idToken: String
    open var accessToken: String
    open var secret: String
}

external interface AuthProvider {
    var providerId: String
}

external interface ConfirmationResult {
    fun confirm(verificationCode: String): Promise<UserCredential>
    var verificationId: String
}

external open class EmailAuthProvider : EmailAuthProvider_Instance {
    companion object {
        var PROVIDER_ID: String
        var EMAIL_PASSWORD_SIGN_IN_METHOD: String
        var EMAIL_LINK_SIGN_IN_METHOD: String
        fun credential(email: String, password: String): AuthCredential
        fun credentialWithLink(email: String, emailLink: String): AuthCredential
    }
}

external open class EmailAuthProvider_Instance : AuthProvider {
    override var providerId: String
}

external interface Error {
    var code: String
    var message: String
}

external interface AuthError : Error {
    var credential: AuthCredential?
        get() = definedExternally
        set(value) = definedExternally
    var email: String?
        get() = definedExternally
        set(value) = definedExternally
    var phoneNumber: String?
        get() = definedExternally
        set(value) = definedExternally
    var tenantId: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface MultiFactorError : AuthError {
    var resolver: MultiFactorResolver
}

external open class FacebookAuthProvider : FacebookAuthProvider_Instance {
    companion object {
        var PROVIDER_ID: String
        var FACEBOOK_SIGN_IN_METHOD: String
        fun credential(token: String): OAuthCredential
    }
}

external open class FacebookAuthProvider_Instance : AuthProvider {
    open fun addScope(scope: String): AuthProvider
    override var providerId: String
    open fun setCustomParameters(customOAuthParameters: Any): AuthProvider
}

external open class GithubAuthProvider : GithubAuthProvider_Instance {
    companion object {
        var PROVIDER_ID: String
        var GITHUB_SIGN_IN_METHOD: String
        fun credential(token: String): OAuthCredential
    }
}

external open class GithubAuthProvider_Instance : AuthProvider {
    open fun addScope(scope: String): AuthProvider
    override var providerId: String
    open fun setCustomParameters(customOAuthParameters: Any): AuthProvider
}

external open class GoogleAuthProvider : GoogleAuthProvider_Instance {
    companion object {
        var PROVIDER_ID: String
        var GOOGLE_SIGN_IN_METHOD: String
        fun credential(idToken: String? = definedExternally, accessToken: String? = definedExternally): OAuthCredential
    }
}

external open class GoogleAuthProvider_Instance : AuthProvider {
    open fun addScope(scope: String): AuthProvider
    override var providerId: String
    open fun setCustomParameters(customOAuthParameters: Any): AuthProvider
}

external open class OAuthProvider(providerId: String) : AuthProvider {
    override var providerId: String
    open fun addScope(scope: String): AuthProvider
    open fun credential(optionsOrIdToken: OAuthCredentialOptions?, accessToken: String = definedExternally): OAuthCredential
    open fun credential(optionsOrIdToken: OAuthCredentialOptions?): OAuthCredential
    open fun credential(optionsOrIdToken: String?, accessToken: String = definedExternally): OAuthCredential
    open fun credential(optionsOrIdToken: String?): OAuthCredential
    open fun setCustomParameters(customOAuthParameters: Any): AuthProvider
}

external open class SAMLAuthProvider(providerId: String) : AuthProvider {
    override var providerId: String
}

external interface IdTokenResult {
    var token: String
    var expirationTime: String
    var authTime: String
    var issuedAtTime: String
    var signInProvider: String?
    var signInSecondFactor: String?
    var claims: Json
}

external interface OAuthCredentialOptions {
    var idToken: String?
        get() = definedExternally
        set(value) = definedExternally
    var accessToken: String?
        get() = definedExternally
        set(value) = definedExternally
    var rawNonce: String?
        get() = definedExternally
        set(value) = definedExternally
}

external open class MultiFactorAssertion {
    open var factorId: String
}

external open class PhoneMultiFactorAssertion : MultiFactorAssertion

external open class PhoneMultiFactorGenerator {
    companion object {
        var FACTOR_ID: String
        fun assertion(phoneAuthCredential: PhoneAuthCredential): PhoneMultiFactorAssertion
    }
}

external interface MultiFactorInfo {
    var uid: String
    var displayName: String?
        get() = definedExternally
        set(value) = definedExternally
    var enrollmentTime: String
    var factorId: String
}

external interface PhoneMultiFactorInfo : MultiFactorInfo {
    var phoneNumber: String
}

external interface PhoneSingleFactorInfoOptions {
    var phoneNumber: String
}

external interface PhoneMultiFactorEnrollInfoOptions {
    var phoneNumber: String
    var session: MultiFactorSession
}

external interface PhoneMultiFactorSignInInfoOptions {
    var multiFactorHint: MultiFactorInfo?
        get() = definedExternally
        set(value) = definedExternally
    var multiFactorUid: String?
        get() = definedExternally
        set(value) = definedExternally
    var session: MultiFactorSession
}

external open class MultiFactorResolver {
    open var auth: Auth
    open var session: MultiFactorSession
    open var hints: Array<MultiFactorInfo>
    open fun resolveSignIn(assertion: MultiFactorAssertion): Promise<UserCredential>
}

external open class MultiFactorSession

external open class PhoneAuthCredential : AuthCredential

external open class PhoneAuthProvider(auth: Auth? = definedExternally) : PhoneAuthProvider_Instance {
    companion object {
        var PROVIDER_ID: String
        var PHONE_SIGN_IN_METHOD: String
        fun credential(verificationId: String, verificationCode: String): AuthCredential
    }
}

external open class PhoneAuthProvider_Instance(auth: Auth? = definedExternally) : AuthProvider {
    override var providerId: String
    open fun verifyPhoneNumber(phoneInfoOptions: PhoneSingleFactorInfoOptions, applicationVerifier: ApplicationVerifier): Promise<String>
    open fun verifyPhoneNumber(phoneInfoOptions: PhoneMultiFactorEnrollInfoOptions, applicationVerifier: ApplicationVerifier): Promise<String>
    open fun verifyPhoneNumber(phoneInfoOptions: PhoneMultiFactorSignInInfoOptions, applicationVerifier: ApplicationVerifier): Promise<String>
    open fun verifyPhoneNumber(phoneInfoOptions: String, applicationVerifier: ApplicationVerifier): Promise<String>
}

/*
external open class RecaptchaVerifier : RecaptchaVerifier_Instance {
    constructor(container: Any, parameters: Any? = definedExternally, app: App? = definedExternally)
    constructor(container: Any)
    constructor(container: Any, parameters: Any? = definedExternally)
    constructor(container: String, parameters: Any? = definedExternally, app: App? = definedExternally)
    constructor(container: String)
    constructor(container: String, parameters: Any? = definedExternally)
}

external open class RecaptchaVerifier_Instance : ApplicationVerifier {
    constructor(container: Any, parameters: Any? = definedExternally, app: App? = definedExternally)
    constructor(container: Any)
    constructor(container: Any, parameters: Any? = definedExternally)
    constructor(container: String, parameters: Any? = definedExternally, app: App? = definedExternally)
    constructor(container: String)
    constructor(container: String, parameters: Any? = definedExternally)
    open fun clear()
    open fun render(): Promise<Number>
    override var type: String
    override fun verify(): Promise<String>
}
*/

external open class TwitterAuthProvider : TwitterAuthProvider_Instance {
    companion object {
        var PROVIDER_ID: String
        var TWITTER_SIGN_IN_METHOD: String
        fun credential(token: String, secret: String): OAuthCredential
    }
}

external open class TwitterAuthProvider_Instance : AuthProvider {
    override var providerId: String
    open fun setCustomParameters(customOAuthParameters: Any): AuthProvider
}

external interface UserCredential {
    var additionalUserInfo: AdditionalUserInfo?
        get() = definedExternally
        set(value) = definedExternally
    var credential: AuthCredential?
    var operationType: String?
        get() = definedExternally
        set(value) = definedExternally
    var user: dynamic?
}

external interface UserMetadata {
    var creationTime: String?
        get() = definedExternally
        set(value) = definedExternally
    var lastSignInTime: String?
        get() = definedExternally
        set(value) = definedExternally
}