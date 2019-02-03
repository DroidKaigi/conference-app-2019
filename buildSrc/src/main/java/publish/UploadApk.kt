package publish

import com.google.api.client.http.FileContent
import com.google.api.services.androidpublisher.model.LocalizedText
import com.google.api.services.androidpublisher.model.Track
import com.google.api.services.androidpublisher.model.TrackRelease
import org.slf4j.Logger
import java.io.File

class UploadApk(
    private val logger: Logger
) {
    fun execute(
        packageName: String,
        serviceAccountJson: File,
        apkFile: File,
        track: DistributionTrack,
        releaseName: String,
        editStatus: EditStatus,
        releaseNoteMap: Map<String, File>
    ) {
        if (packageName.isEmpty()) {
            error("packageName must not be empty")
        }

        val notFoundReleaseNotes = releaseNoteMap.filterValues { !it.exists() }

        if (notFoundReleaseNotes.isNotEmpty()) {
            val message = releaseNoteMap.filterValues { !it.exists() }
                .map { (lang, noteFile) ->
                    "$lang at $noteFile "
                }
                .joinToString(", ")

            error("ReleaseNote not found: $message")
        }

        val editsService = androidPublisher(packageName, serviceAccountJson).edits()

        editsService.runInTransaction(packageName, { editId ->
            logger.debug("New edit transaction id is $editId")

            val apkContent = apkFile.asApkContent()

            val apkResult =
                editsService.apks().upload(packageName, editId, apkContent).execute()

            logger.info("$packageName (${apkResult.versionCode}) has been uploaded")

            val releaseContent = TrackRelease().apply {
                name = releaseName
                versionCodes = listOf(apkResult.versionCode.toLong())
                status = editStatus.status
                releaseNotes = releaseNoteMap.map { (lang, noteFile) ->
                    LocalizedText().setLanguage(lang).setText(noteFile.readText())
                }
            }

            val updatedTrack = editsService.tracks().update(
                packageName,
                editId,
                track.name,
                Track().setReleases(listOf(releaseContent))
            ).execute()

            logger.info("Update ${updatedTrack.track} and the status has been ${editStatus.status}")
        }) { th ->
            logger.error("while edit transaction", th)
            throw th
        }
    }

    private fun File.asApkContent(): FileContent {
        if (!exists()) {
            error("$this does not exist")
        }

        return FileContent("application/vnd.android.package-archive", this)
    }
}
