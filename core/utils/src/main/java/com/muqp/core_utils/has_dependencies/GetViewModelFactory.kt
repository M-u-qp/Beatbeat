package com.muqp.core_utils.has_dependencies

import androidx.lifecycle.ViewModelProvider

interface GetViewModelFactory {
    fun provideViewModelFactory(): ViewModelProvider.Factory
}