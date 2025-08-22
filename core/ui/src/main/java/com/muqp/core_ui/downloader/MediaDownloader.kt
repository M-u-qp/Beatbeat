package com.muqp.core_ui.downloader

import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.format.Formatter
import androidx.core.app.NotificationCompat
import com.muqp.beatbeat.ui.R
import com.muqp.core_ui.app_permission.AppPermission
import com.muqp.core_ui.app_permission.hasPermission
import com.muqp.core_ui.downloader.CreateFolderIntent.generateUniqueFileName
import com.muqp.core_ui.downloader.notification.NotificationDownloadHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.math.max

class MediaDownloader(
    context: Context,
    private val notificationDownloadHelper: NotificationDownloadHelper,
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
) {
    private val contextRef = WeakReference(context)
    private val context: Context?
        get() = contextRef.get()

    private val logger = Logger.getLogger("MediaDownloader")

    suspend fun downloadMedia(
        url: String,
        fileName: String,
        mimeType: String = "audio/mpeg"
    ): Result<Uri> = withContext(Dispatchers.IO) {
        logger.info("Starting download: $fileName from $url")

        showInitialNotification(fileName)

        return@withContext try {
            val result = downloadFileWithProgress(url, fileName, mimeType)
            when {
                result.isSuccess -> {
                    showCompletionNotification(fileName, result.getOrNull())
                    result
                }

                else -> {
                    showErrorNotification(fileName)
                    result
                }
            }
        } catch (e: Exception) {
            logger.log(Level.SEVERE, "Download failed", e)
            showErrorNotification(fileName)
            Result.failure(e)
        }
    }

    private suspend fun downloadFileWithProgress(
        url: String,
        fileName: String,
        mimeType: String
    ): Result<Uri> = withContext(Dispatchers.IO) {
        val resolver = context?.contentResolver
            ?: return@withContext Result.failure(IOException("No content resolver"))

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            } else {
                val path =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                put(MediaStore.MediaColumns.DATA, "${path.absolutePath}/$fileName")
            }
        }

        val uri = resolver.insert(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            },
            contentValues
        ) ?: return@withContext Result.failure(IOException("Failed to create media entry"))

        try {
            resolver.openOutputStream(uri)?.use { output ->
                val request = Request.Builder().url(url).build()
                val response = okHttpClient.newCall(request).execute()

                if (!response.isSuccessful) {
                    throw IOException("HTTP ${response.code()}")
                }

                val body = response.body() ?: throw IOException("Empty response body")
                val contentLength = body.contentLength()
                var bytesDownloaded = 0L

                body.byteStream().use { input ->
                    val buffer = ByteArray(8 * 1024)
                    var bytesRead: Int

                    updateProgressNotification(fileName, 0, contentLength, uri)

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        bytesDownloaded += bytesRead

                        if (contentLength > 0) {
                            val progress = (100 * bytesDownloaded / contentLength).toInt()
                            if (bytesDownloaded % max(512_000, contentLength / 20) == 0L) {
                                updateProgressNotification(fileName, progress, contentLength, uri)
                            }
                        }
                    }
                }

                if (contentLength > 0) {
                    updateProgressNotification(fileName, 100, contentLength, uri)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }

            Result.success(uri)
        } catch (e: Exception) {
            logger.log(Level.SEVERE, "Download failed", e)
            resolver.delete(uri, null, null)
            Result.failure(e)
        }
    }

    private fun showInitialNotification(fileName: String) {
        context?.let {
            notificationDownloadHelper.showNotification(
                icon = R.drawable.icons8_download_v2,
                title = it.getString(R.string.downloading),
                content = fileName
            )
        }
    }

    private fun updateProgressNotification(
        fileName: String,
        progress: Int,
        totalBytes: Long,
        fileUri: Uri? = null
    ) {
        context?.let { ctx ->
            val formattedSize = if (totalBytes > 0) {
                " (${Formatter.formatFileSize(ctx, totalBytes)})"
            } else {
                ""
            }

            val builder =
                NotificationCompat.Builder(ctx, NotificationDownloadHelper.DOWNLOAD_CHANNEL_ID)
                    .setSmallIcon(R.drawable.icons8_download_v2)
                    .setContentTitle(ctx.getString(R.string.downloading))
                    .setContentText("$fileName$formattedSize")
                    .setProgress(100, progress, false)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setOngoing(true)

            if (fileUri != null && progress == 100) {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(fileUri, "audio/*")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                val pendingIntent = PendingIntent.getActivity(
                    ctx,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                builder.setContentIntent(pendingIntent)
            }

            notificationDownloadHelper.notificationManager?.notify(
                NotificationDownloadHelper.DOWNLOAD_NOTIFICATION_ID,
                builder.build()
            )
        }
    }

    private fun showCompletionNotification(fileName: String, fileUri: Uri?) {
        context?.let {
            notificationDownloadHelper.showNotification(
                icon = R.drawable.icons8_completed,
                title = it.getString(R.string.download_complete),
                content = fileName,
                fileUri = fileUri,
                autoCancel = true
            )
        }
    }

    private fun showErrorNotification(fileName: String) {
        context?.let {
            notificationDownloadHelper.showNotification(
                icon = R.drawable.icons8_error,
                title = it.getString(R.string.download_error),
                content = fileName,
                autoCancel = true
            )
        }
    }
}

suspend fun Context.downloadMediaFile(
    url: String,
    fileName: String,
    mimeType: String = "audio/mpeg"
): Result<Uri> {
    val uniqueFileName = generateUniqueFileName(fileName)

    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            if (!hasPermission(AppPermission.MediaAudio)) {
                Result.failure(Exception("Permission denied: READ_MEDIA_AUDIO"))
            } else {
                MediaDownloader(this, NotificationDownloadHelper(this))
                    .downloadMedia(url, uniqueFileName, mimeType)
            }
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            if (!hasPermission(AppPermission.Storage)) {
                Result.failure(Exception("Permission denied: MANAGE_EXTERNAL_STORAGE"))
            } else {
                MediaDownloader(this, NotificationDownloadHelper(this))
                    .downloadMedia(url, uniqueFileName, mimeType)
            }
        }

        else -> {
            MediaDownloader(this, NotificationDownloadHelper(this))
                .downloadMedia(url, uniqueFileName, mimeType)
        }
    }
}
