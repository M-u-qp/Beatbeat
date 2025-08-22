package com.muqp.core_ui.adapters_for_recycler

import android.view.View

interface RecyclerBindable {
    fun bind(view: View)
    fun getId(): String
}