package publish

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.AndroidPublisherScopes
import java.io.File

fun androidPublisher(packageName: String, serviceAccountJson: File): AndroidPublisher {
    val credential = serviceAccountJson.asCredential()

    return AndroidPublisher.Builder(
        GoogleNetHttpTransport.newTrustedTransport(),
        JacksonFactory.getDefaultInstance(),
        credential
    )
        .setApplicationName(packageName)
        .build()
}

fun File.asCredential(): GoogleCredential {
    if (!exists()) {
        error("$this does not exist")
    }

    return inputStream().use { credentialStream ->
        GoogleCredential.fromStream(credentialStream)
            .createScoped(
                listOf(AndroidPublisherScopes.ANDROIDPUBLISHER)
            )
    }
}

fun AndroidPublisher.Edits.runInTransaction(
    packageName: String,
    editId: String = insert(packageName, null).execute().id,
    action: (editId: String) -> Unit = {},
    errorHandler: (error: Throwable) -> Unit = {}
) {
    try {
        action(editId)

        commit(packageName, editId).execute()
    } catch (th: Throwable) {
        errorHandler(th)
    }
}

