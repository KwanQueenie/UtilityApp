

package au.edu.jcu.assessment.utilityapp.model

/** A place the user can view weather for. Region is a short state code such as "QLD". */
data class City(
    val name: String,
    val region: String,
    val latitude: Double,
    val longitude: Double,
) {
    /** Text shown in lists, e.g. "Townsville, QLD". */
    val label: String
        get() = if (region.isBlank()) name else "$name, $region"

    /** Geocoded results have slightly different coordinates, so compare by name and state. */
    fun isSameAs(other: City): Boolean =
        name.equals(other.name, ignoreCase = true) && region == other.region
}

/** Capital cities plus the two JCU campus cities, used as quick picks in Settings. */
object AustralianCities {
    val Townsville = City("Townsville", "QLD", -19.2590, 146.8169)
    val Cairns = City("Cairns", "QLD", -16.9186, 145.7781)
    val Brisbane = City("Brisbane", "QLD", -27.4698, 153.0251)
    val Sydney = City("Sydney", "NSW", -33.8688, 151.2093)
    val Canberra = City("Canberra", "ACT", -35.2809, 149.1300)
    val Melbourne = City("Melbourne", "VIC", -37.8136, 144.9631)
    val Hobart = City("Hobart", "TAS", -42.8821, 147.3272)
    val Adelaide = City("Adelaide", "SA", -34.9285, 138.6007)
    val Perth = City("Perth", "WA", -31.9523, 115.8613)
    val Darwin = City("Darwin", "NT", -12.4634, 130.8456)

    val all = listOf(Townsville, Cairns, Brisbane, Sydney, Canberra, Melbourne, Hobart, Adelaide, Perth, Darwin)

    /** Cities available in the main-screen city switcher when the app starts. */
    val defaultSaved = listOf(Townsville, Cairns, Brisbane)

    private val stateCodes = mapOf(
        "queensland" to "QLD",
        "new south wales" to "NSW",
        "australian capital territory" to "ACT",
        "victoria" to "VIC",
        "tasmania" to "TAS",
        "south australia" to "SA",
        "western australia" to "WA",
        "northern territory" to "NT",
    )

    /** Turns "Queensland" into "QLD"; unknown regions are returned unchanged. */
    fun stateCode(region: String?): String =
        region?.let { stateCodes[it.trim().lowercase()] ?: it.trim() } ?: ""
}