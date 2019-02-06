package publish

import org.slf4j.Logger
import java.io.File

class PromoteApk(
    private val logger: Logger
) {
    fun execute(
        packageName: String,
        serviceAccountJson: File,
        editId: String
    ) {
        if (packageName.isEmpty()) {
            error("packageName must not be empty")
        }

        val editsService = androidPublisher(packageName, serviceAccountJson).edits()

        editsService.runInTransaction(packageName, editId, action = { editId ->
            logger.debug("The current edit transaction id is $editId")

            // Use `alpha`
            val existingTrack =
                editsService.tracks().get(packageName, editId, DistributionTrack.Alpha.name)
                    .execute()

            // Use `production`
            val updatedTrack =
                editsService.tracks()
                    .update(packageName, editId, DistributionTrack.Production.name, existingTrack)
                    .execute()

            logger.info("Update ${DistributionTrack.Alpha.name} to ${updatedTrack.track}")
        }) { th ->
            logger.error("while edit transaction", th)
            throw th
        }
    }
}
