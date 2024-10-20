package stupidrepo.classuncharted.data.mine

import kotlinx.serialization.Serializable

@Serializable
data class Account(
    var code: String,
    val dob: String
)
