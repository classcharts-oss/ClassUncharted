package stupidrepo.classuncharted.data.mine

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class SavedAccount(
    @Contextual val account: Account,
    val name: String
)