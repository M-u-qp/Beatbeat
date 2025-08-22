package com.muqp.core_ui.adapters_for_recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class CommonPagingAdapter(
    private val layoutResId: Int
) : PagingDataAdapter<RecyclerBindable, CommonPagingAdapter.Holder>(Comparator()) {

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

        override fun areContentsTheSame(
            oldItem: RecyclerBindable,
            newItem: RecyclerBindable
        ): Boolean {
            return oldItem.getId() == newItem.getId()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }
}