package com.kanastruk.i18n.client

@kotlinx.serialization.Serializable
data class ClientStrings(
    val header: Header,
    val profileEditor:ProfileEditor,
    val index: Index,
    val lang: String,
    val langSelector: LangSelector,
    val reserve: Reserve
) {
    @kotlinx.serialization.Serializable
    data class Header(
        val en: String,
        val fr: String
    )

    @kotlinx.serialization.Serializable
    data class ProfileEditor(
        val address: String,
        val address1: String,
        val address2: String,
        val anonymous: String,
        val cancel: String,
        val city: String,
        val companyName: String,
        val country: String,
        val country_sub: String,
        val crash_test: String,
        val delete: String,
        val email: String,
        val email_sign_in: String,
        val firstName: String,
        val lastName: String,
        val ok: String,
        val phone: String,
        val postal: String,
        val save: String
    )

    @kotlinx.serialization.Serializable
    data class Index(
        val about: About,
        val button: Button,
        val footer: Footer,
        val homepage: Homepage,
        val nav: Nav,
        val ramasseCa: String,
        val service: Service,
        val title: String,
        val what: What
    ) {
        @kotlinx.serialization.Serializable
        data class About(
            val header: String,
            val p1B: String,
            val p2: String,
            val p3: String
        )

        @kotlinx.serialization.Serializable
        data class Button(
            val callUs: String,
            val collection: Collection,
            val phoneNumber: String,
            val rental: Rental,
            val reserve: String
        ) {
            @kotlinx.serialization.Serializable
            data class Collection(
                val body: String,
                val subject: String
            )

            @kotlinx.serialization.Serializable
            data class Rental(
                val body: String,
                val subject: String
            )
        }

        @kotlinx.serialization.Serializable
        data class Footer(
            val address1: String,
            val address2: String,
            val email: String,
            val phone: String,
            val questions: String,
            val reach: String,
            val zones: String
        )

        @kotlinx.serialization.Serializable
        data class Homepage(
            val header: String,
            val subtext: String
        )

        @kotlinx.serialization.Serializable
        data class Nav(
            val about: String,
            val services: String,
            val toggle: Toggle,
            val what: String
        ) {
            @kotlinx.serialization.Serializable
            data class Toggle(
                val href: String,
                val language: String
            )
        }

        @kotlinx.serialization.Serializable
        data class Service(
            val collection: Collection,
            val header: String,
            val rental: Rental
        ) {
            @kotlinx.serialization.Serializable
            data class Collection(
                val action: String,
                val body: String,
                val header: String
            )

            @kotlinx.serialization.Serializable
            data class Rental(
                val action: String,
                val body: String,
                val header: String
            )
        }

        @kotlinx.serialization.Serializable
        data class What(
            val appliances: Appliances,
            val commercial: Commercial,
            val construction: Construction,
            val headerA: String,
            val headerB: String,
            val residential: Residential
        ) {
            @kotlinx.serialization.Serializable
            data class Appliances(
                val description: String,
                val title: String
            )

            @kotlinx.serialization.Serializable
            data class Commercial(
                val description: String,
                val title: String
            )

            @kotlinx.serialization.Serializable
            data class Construction(
                val description: String,
                val title: String
            )

            @kotlinx.serialization.Serializable
            data class Residential(
                val description: String,
                val title: String
            )
        }
    }

    @kotlinx.serialization.Serializable
    data class LangSelector(
        val en: Boolean,
        val fr: Boolean
    )

    @kotlinx.serialization.Serializable
    data class Reserve(
        val address1: String,
        val address2: String,
        val addressHeader: String,
        val afternoon: String,
        val city: String,
        val collectionCard: CollectionCard,
        val contactDetails: String,
        val containerCard: ContainerCard,
        val email: String,
        val evening: String,
        val firstName: String,
        val fullName: String,
        val lastName: String,
        val morning: String,
        val nav: Nav,
        val phone: String,
        val pleaseWait: String,
        val postalCode: String,
        val required: Required,
        val stepContactAddress: StepContactAddress,
        val stepRecap: StepRecap,
        val stepSchedule: StepSchedule,
        val stepService: StepService,
        val thanks: Thanks,
        val title: String
    ) {
        @kotlinx.serialization.Serializable
        data class CollectionCard(
            val button: String,
            val subtitle: String,
            val text: String,
            val title: String
        )

        @kotlinx.serialization.Serializable
        data class ContainerCard(
            val button: String,
            val subtitle: String,
            val text: String,
            val title: String
        )

        @kotlinx.serialization.Serializable
        data class Nav(
            val href: String,
            val language: String
        )

        @kotlinx.serialization.Serializable
        data class Required(
            val email: String,
            val generic: String,
            val phone: String,
            val postal: String
        )

        @kotlinx.serialization.Serializable
        data class StepContactAddress(
            val submitButton: String
        )

        @kotlinx.serialization.Serializable
        data class StepRecap(
            val bookButton: String,
            val editButton: String,
            val header: String
        )

        @kotlinx.serialization.Serializable
        data class StepSchedule(
            val back: String,
            val calLoading: String
        )

        @kotlinx.serialization.Serializable
        data class StepService(
            val header: String
        )

        @kotlinx.serialization.Serializable
        data class Thanks(
            val questions: String,
            val subtext: String,
            val title: String
        )
    }
}