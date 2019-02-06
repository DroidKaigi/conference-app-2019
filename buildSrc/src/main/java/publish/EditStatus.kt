package publish

sealed class EditStatus {
    abstract val status: String

    object InProgress : EditStatus() {
        override val status: String = "inProgress"
    }

    object Draft : EditStatus() {
        override val status: String = "draft"
    }

    object Completed : EditStatus() {
        override val status: String = "completed"
    }

    object Halted : EditStatus() {
        override val status: String = "halted"
    }
}
