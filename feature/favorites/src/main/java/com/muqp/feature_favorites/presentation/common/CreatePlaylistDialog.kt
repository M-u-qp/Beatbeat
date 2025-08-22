package com.muqp.feature_favorites.presentation.common

import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muqp.core_utils.extensions.IntExt.dpToPx
import com.muqp.beatbeat.ui.R as CoreUi

class CreatePlaylistDialog(
    private val context: Context
) {
    var onCreatePlaylist: ((name: String, description: String) -> Unit)? = null
    var onDismiss: (() -> Unit)? = null

    fun show() {
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32.dpToPx(context), 16.dpToPx(context), 32.dpToPx(context), 0)
        }

        val inputName = EditText(context).apply {
            hint = context.getString(CoreUi.string.enter_name)
            isSingleLine = true
        }

        val inputDescription = EditText(context).apply {
            hint = context.getString(CoreUi.string.enter_description)
            isSingleLine = true
        }

        container.addView(inputName)
        container.addView(inputDescription)

        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(CoreUi.string.creating_playlist)
            .setView(container)
            .setPositiveButton(CoreUi.string.create) { _, _ ->
                val name = inputName.text.toString().trim()
                val description = inputDescription.text.toString().trim()
                if (name.isNotEmpty()) {
                    onCreatePlaylist?.invoke(name, description)
                }
            }
            .setNegativeButton(CoreUi.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.setOnDismissListener {
            onDismiss?.invoke()
        }

        dialog.show()
    }
}