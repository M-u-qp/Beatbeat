package com.muqp.core_ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muqp.beatbeat.ui.R
import com.muqp.beatbeat.ui.databinding.PlaylistItemV2Binding

class PlaylistSelectionDialog(
    private val context: Context,
    private val playlists: List<PlaylistSelectionItem>,
) {
    private var dialog: AlertDialog? = null
    var onPlaylistSelected: ((PlaylistSelectionItem) -> Unit)? = null
    var onCreatePlaylistClicked: (() -> Unit)? = null

    data class PlaylistSelectionItem(
        val id: Long,
        val name: String,
        val description: String? = null
    )

    fun show() {
        val recyclerView = RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = PlaylistsAdapter(playlists) { playlist ->
                onPlaylistSelected?.invoke(playlist)
                dialog?.dismiss()
            }
        }
        dialog = MaterialAlertDialogBuilder(context)
            .setTitle(R.string.selecting_playlist)
            .setView(recyclerView)
            .setPositiveButton(R.string.create_playlist) { _, _ ->
                onCreatePlaylistClicked?.invoke()
            }
            .setNegativeButton(R.string.cancel, null)
            .create()

        dialog?.show()
    }

    private inner class PlaylistsAdapter(
        private val items: List<PlaylistSelectionItem>,
        private val onItemClick: (PlaylistSelectionItem) -> Unit
    ) : RecyclerView.Adapter<PlaylistsAdapter.ViewHolder>() {
        inner class ViewHolder(val binding: PlaylistItemV2Binding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = PlaylistItemV2Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            with(holder.binding) {
                playlistTitle.text = item.name
                playlistDescription.text = item.description ?: ""
                root.setOnClickListener { onItemClick(item) }
            }
        }
    }
}