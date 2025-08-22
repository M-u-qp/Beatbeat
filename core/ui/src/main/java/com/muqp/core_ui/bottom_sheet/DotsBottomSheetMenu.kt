package com.muqp.core_ui.bottom_sheet

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.muqp.beatbeat.ui.R
import com.muqp.beatbeat.ui.databinding.BottomSheetDotsMenuBinding
import com.muqp.beatbeat.ui.databinding.BottomSheetListItemBinding

object DotsBottomSheetMenu {
    fun Context.showDotsMenu(
        title: String? = null,
        imageUrl: String? = null,
        name: String? = null,
        actions: List<BottomSheetAction>,
        onDismiss: () -> Unit = {}
    ) {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme).apply {
            val binding = BottomSheetDotsMenuBinding.inflate(LayoutInflater.from(this@showDotsMenu))
            setContentView(binding.root)

            behavior.apply {
                skipCollapsed = true
                isFitToContents = true
            }

            with(binding) {
                titleTv.text = title ?: ""
                nameTv.text = name ?: ""

                Glide.with(this@showDotsMenu)
                    .load(imageUrl ?: R.drawable.icons8_beatbeat)
                    .placeholder(R.drawable.icons8_beatbeat)
                    .error(R.drawable.icons8_broken_image)
                    .into(imageIv)

                actionsRv.layoutManager = LinearLayoutManager(this@showDotsMenu)
                actionsRv.adapter = BottomSheetActionAdapter(actions) { action ->
                    action.action()
                    dismiss()
                }
            }
            setOnDismissListener { onDismiss() }
        }
        dialog.show()
    }

    data class BottomSheetAction(
        val iconRes: Int,
        val title: String,
        val action: () -> Unit
    )

    private class BottomSheetActionAdapter(
        private val actions: List<BottomSheetAction>,
        private val onActionClicked: (BottomSheetAction) -> Unit
    ) : RecyclerView.Adapter<BottomSheetActionAdapter.ActionViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ) = ActionViewHolder(
            BottomSheetListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        override fun onBindViewHolder(
            holder: BottomSheetActionAdapter.ActionViewHolder,
            position: Int
        ) {
            holder.bind(actions[position])
        }

        override fun getItemCount(): Int = actions.size

        inner class ActionViewHolder(
            private val binding: BottomSheetListItemBinding
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(action: BottomSheetAction) = with(binding) {
                icon.setImageResource(action.iconRes)
                title.text = action.title
                root.setOnClickListener { onActionClicked(action) }
            }
        }
    }
}