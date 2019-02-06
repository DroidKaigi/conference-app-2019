package publish

sealed class DistributionTrack {
    abstract val name: String

    object Internal : DistributionTrack() {
        override val name: String = "internal"
    }

    object Alpha : DistributionTrack() {
        override val name: String = "alpha"
    }

    object Beta : DistributionTrack() {
        override val name: String = "beta"
    }

    object Production : DistributionTrack() {
        override val name: String = "production"
    }
}
