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
 * With enhanced debugging and error handling.
 */
class StorageService(private val storage: Storage, private val bucketName: String) {

    /**
     * Uploads a file to Firebase Storage with progress tracking and debugging.
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
        println("DEBUG: Starting file upload from: ${filePath}")

        try {
            // Verify the file exists and is readable
            val file = filePath.toFile()
            if (!file.exists() || !file.canRead()) {
                println("ERROR: File doesn't exist or can't be read: ${filePath}")
                return@withContext null
            }

            println("DEBUG: File size: ${file.length()} bytes")

            // Generate a unique filename to avoid conflicts
            val uniqueId = UUID.randomUUID().toString()
            val originalFilename = filePath.fileName.toString()
            val extension = originalFilename.substringAfterLast('.', "")
            val storageFilePath = if (extension.isNotEmpty()) {
                "$directoryPath/$uniqueId.$extension"
            } else {
                "$directoryPath/$uniqueId"
            }

            println("DEBUG: Storage path will be: $storageFilePath")
            println("DEBUG: Bucket name: $bucketName")

            // Create a blob ID and info
            val blobId = BlobId.of(bucketName, storageFilePath)
            val blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(getContentType(extension))
                .build()

            println("DEBUG: Created BlobInfo with content type: ${getContentType(extension)}")

            // Use a simple approach for smaller files
            val fileBytes = file.readBytes()
            println("DEBUG: Read ${fileBytes.size} bytes from file")

            // Update progress to indicate reading is complete
            progressFlow?.emit(0.2f)

            // Upload directly for simplicity
            println("DEBUG: Starting upload to Firebase Storage")
            try {
                val blob = storage.create(blobInfo, fileBytes)
                println("DEBUG: Upload completed. Blob size: ${blob.size}")

                // Ensure progress is complete
                progressFlow?.emit(1.0f)

                // Return the gs:// URL
                val resultUrl = "gs://$bucketName/$storageFilePath"
                println("DEBUG: Generated URL: $resultUrl")
                return@withContext resultUrl
            } catch (e: Exception) {
                println("ERROR during storage.create: ${e.javaClass.simpleName}: ${e.message}")
                e.printStackTrace()
                return@withContext null
            }
        } catch (e: Exception) {
            println("ERROR in uploadFile: ${e.javaClass.simpleName}: ${e.message}")
            e.printStackTrace()
            return@withContext null
        }
    }

    /**
     * Deletes a file from Firebase Storage.
     *
     * @param gsUrl The gs:// URL of the file to delete
     * @return true if deletion was successful, false otherwise
     */
    suspend fun deleteFile(gsUrl: String): Boolean = withContext(Dispatchers.IO) {
        println("DEBUG: Starting file deletion: $gsUrl")

        try {
            if (!gsUrl.startsWith("gs://")) {
                println("ERROR: URL does not start with gs://: $gsUrl")
                return@withContext false
            }

            val path = gsUrl.removePrefix("gs://$bucketName/")
            println("DEBUG: Extracted storage path: $path")

            val blobId = BlobId.of(bucketName, path)
            println("DEBUG: Created BlobId for bucket: $bucketName, path: $path")

            val result = storage.delete(blobId)
            println("DEBUG: Deletion result: $result")

            result
        } catch (e: Exception) {
            println("ERROR in deleteFile: ${e.javaClass.simpleName}: ${e.message}")
            e.printStackTrace()
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