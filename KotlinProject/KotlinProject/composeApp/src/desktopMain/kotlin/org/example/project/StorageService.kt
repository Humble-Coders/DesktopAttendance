package org.example.project

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.file.Path
import java.util.UUID

/**
 * Utility class for handling Firebase Storage operations in desktop environment.
 */
class StorageService(private val storage: Storage, private val bucketName: String) {

    /**
     * Uploads a file to Firebase Storage with progress tracking.
     *
     * @param filePath Path to the local file to upload
     * @param directoryPath Optional directory path within the storage bucket
     * @param progressFlow Flow to emit upload progress (0.0f to 1.0f)
     * @return The gs:// URL of the uploaded file, or null if upload failed
     */
    suspend fun uploadFile(
        filePath: Path,
        directoryPath: String = "products",
        progressFlow: MutableStateFlow<Float>? = null
    ): String? = withContext(Dispatchers.IO) {
        try {
            // Generate a unique filename to avoid conflicts
            val uniqueId = UUID.randomUUID().toString()
            val originalFilename = filePath.fileName.toString()
            val extension = originalFilename.substringAfterLast('.', "")
            val storageFilePath = if (extension.isNotEmpty()) {
                "$directoryPath/$uniqueId.$extension"
            } else {
                "$directoryPath/$uniqueId"
            }

            // Create a blob ID and info
            val blobId = BlobId.of(bucketName, storageFilePath)
            val blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(getContentType(extension))
                .build()

            // Use FileChannel for better upload progress tracking
            val fileSize = filePath.toFile().length()
            val chunkSize = 256 * 1024 // 256KB chunks

            FileInputStream(filePath.toFile()).use { fis ->
                val channel = fis.channel
                val buffer = ByteBuffer.allocate(chunkSize)

                var bytesUploaded = 0L
                var bytesRead: Int

                // Create a writer for the upload
                storage.writer(blobInfo).use { writer ->
                    while (channel.read(buffer).also { bytesRead = it } > 0) {
                        buffer.flip()
                        val bytes = ByteArray(buffer.limit())
                        buffer.get(bytes)
                        writer.write(ByteBuffer.wrap(bytes))
                        buffer.clear()

                        bytesUploaded += bytesRead
                        val progress = bytesUploaded.toFloat() / fileSize.toFloat()
                        progressFlow?.emit(progress)
                    }
                }
            }

            // Ensure progress is complete
            progressFlow?.emit(1.0f)

            // Return the gs:// URL
            "gs://$bucketName/$storageFilePath"
        } catch (e: Exception) {
            println("Error uploading file: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    /**
     * Deletes a file from Firebase Storage.
     *
     * @param gsUrl The gs:// URL of the file to delete
     * @return true if deletion was successful, false otherwise
     */
    suspend fun deleteFile(gsUrl: String): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!gsUrl.startsWith("gs://")) {
                return@withContext false
            }

            val path = gsUrl.removePrefix("gs://$bucketName/")
            val blobId = BlobId.of(bucketName, path)

            storage.delete(blobId)
            true
        } catch (e: Exception) {
            println("Error deleting file: ${e.message}")
            false
        }
    }

    /**
     * Gets the appropriate content type based on file extension.
     */
    private fun getContentType(extension: String): String {
        return when (extension.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            "svg" -> "image/svg+xml"
            else -> "application/octet-stream"
        }
    }
}