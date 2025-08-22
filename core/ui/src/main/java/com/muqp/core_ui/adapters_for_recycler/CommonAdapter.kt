package com.muqp.core_ui.adapters_for_recycler

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class CommonAdapter(
    private val layoutResId: Int,
    private val itemCount: Int? = null
) : ListAdapter<RecyclerBindable, CommonAdapter.Holder>(Comparator()) {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: RecyclerBindable) {
            item.bind(itemView)
        }
    }

    class Comparator : DiffUtil.ItemCallback<RecyclerBindable>() {
        override fun areItemsTheSame(
            oldItem: RecyclerBindable,
            newItem: RecyclerBindable
        ): Boolean {
            return oldItem.getId() == newItem.getId()
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: RecyclerBindable,
            newItem: RecyclerBindable
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return if (itemCount != null) {
            minOf(itemCount, super.getItemCount())
        } else {
            super.getItemCount()
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}