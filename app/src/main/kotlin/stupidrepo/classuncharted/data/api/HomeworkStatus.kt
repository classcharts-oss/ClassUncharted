package stupidrepo.classuncharted.data.api

data class HomeworkStatus(
    val state: String?,
    val ticked: String,
) {
    val isTicked
        get() = ticked == "yes"
}