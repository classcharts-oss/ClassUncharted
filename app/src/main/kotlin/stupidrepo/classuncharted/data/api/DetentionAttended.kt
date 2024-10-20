package stupidrepo.classuncharted.data.api

import kotlinx.serialization.Serializable

@Serializable
enum class DetentionAttended {
    yes, no, upscaled, pending, unknown
}