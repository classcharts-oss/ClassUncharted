package stupidrepo.classuncharted.data.api

import kotlinx.serialization.Serializable

@Serializable
data class HomeworkStatus(
    val state: String?,
    val ticked: String,
) {
    val isTicked
        get() = ticked == "yes"
}