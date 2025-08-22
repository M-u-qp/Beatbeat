package com.muqp.core_ui.downloader

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.io.File
import java.lang.ref.WeakReference

object CreateFolderIntent {
    fun createViewFolderIntent(context: Context, uri: Uri): PendingIntent? {
        return try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(
                    getParentUri(context, uri) ?: uri,
                    DocumentsContract.Document.MIME_TYPE_DIR
                )
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                addCategory(Intent.CATEGORY_DEFAULT)
            }

            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun getParentUri(context: Context, uri: Uri): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            val path = getPathFromUri(context, uri) ?: return null
            File(path).parentFile?.let { parent ->
                Uri.fromFile(parent)
            }
        }
    }

    private fun getPathFromUri(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        return context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getString(0)
            } else {
                null
            }
        }
    }

    fun createViewFilePendingIntent(context: Context, uri: Uri): PendingIntent? {
        val contextRef = WeakReference(context)
        return contextRef.get()?.let { ctx ->
            val file = getFileFromUri(ctx, uri) ?: return null

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(
                    Uri.fromFile(file.parentFile),
                    "resource/folder"
                )
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                putExtra("select", true)
            }

            PendingIntent.getActivity(
                ctx,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        val contextRef = WeakReference(context)
        return when (uri.scheme) {
            "file" -> uri.path?.let { File(it) }
            "content" -> {
                val cursor = contextRef.get()?.contentResolver?.query(
                    uri,
                    arrayOf(MediaStore.Audio.Media.DATA),
                    null,
                    null,
                    null
                )
                cursor?.use {
                    if (it.moveToFirst()) {
                        val path = it.getString(0)
                        if (path != null) File(path) else null
                    } else null
                }
            }

            else -> null
        }
    }

    fun generateUniqueFileName(baseName: String): String {
        val timestamp = System.currentTimeMillis()
        val cleanName = baseName
            .replace("[^a-zA-Z0-9а-яА-Я\\-_. ]".toRegex(), "")
            .replace(" ", "_")
            .trim()
        return "${cleanName.removeSuffix(".mp3")}_$timestamp.mp3"
    }
}