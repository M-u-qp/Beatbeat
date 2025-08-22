package com.muqp.beatbeat.dependencies_impl

import androidx.lifecycle.ViewModelProvider
import com.muqp.core_utils.has_dependencies.GetViewModelFactory
import javax.inject.Inject

class GetViewModelFactoryImpl @Inject constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : GetViewModelFactory {
    override fun provideViewModelFactory(): ViewModelProvider.Factory {
        return viewModelFactory
    }
}