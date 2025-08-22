package com.muqp.core_ui.adapters_for_recycler

object AdapterFactory {
    fun createPagingAdapter(layoutResId: Int): CommonPagingAdapter {
        return CommonPagingAdapter(layoutResId)
    }

    fun createAdapter(layoutResId: Int, itemCount: Int? = null): CommonAdapter {
        return CommonAdapter(layoutResId, itemCount)
    }
}