package stupidrepo.classuncharted.data.api

data class Teacher(
    val id: Int,

    private val title: String,
    private val first_name: String,
    private val last_name: String
) {
    val full_name: String
        get() = "$title $first_name $last_name"
}
