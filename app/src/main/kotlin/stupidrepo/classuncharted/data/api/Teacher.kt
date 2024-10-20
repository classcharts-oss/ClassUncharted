package stupidrepo.classuncharted.data.api

import kotlinx.serialization.Serializable

@Serializable
data class Teacher(
    val id: Int,

    val title: String,
    val first_name: String,
    val last_name: String
) {
    val full_name: String
        get() = "$title $first_name $last_name"
}
