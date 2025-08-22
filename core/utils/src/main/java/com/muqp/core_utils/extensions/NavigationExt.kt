package com.muqp.core_utils.extensions

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.muqp.core_utils.extensions.BundleExt.getParcelableCompat

const val NAV_DATA = "navigationData"
const val NAV_DATA_INT = "navigationDataInt"
const val NAV_DATA_STRING = "navigationDataString"

object NavigationExt {
    fun Fragment.navigate(
        actionId: Int,
        hostId: Int? = null,
        navOptions: NavOptions? = null,
        data: Parcelable? = null,
        intData: Int? = null,
        stringData: String? = null
    ) {
        val navController = if (hostId == null) {
            findNavController()
        } else {
            (parentFragmentManager.findFragmentById(hostId))?.findNavController()
        }

        val bundle = Bundle().apply {
            putParcelable(NAV_DATA, data)
            if (intData != null) {
                putInt(NAV_DATA_INT, intData)
            }
            putString(NAV_DATA_STRING, stringData)
        }
        navController?.navigate(actionId, bundle, navOptions)
    }

    fun Fragment.navigateBack() {
        val navController = findNavController()
        if (!navController.popBackStack()) {
            navController.navigateUp()
        }
    }

    val Fragment.navigationData: Parcelable?
        get() = arguments?.getParcelableCompat<Parcelable>(NAV_DATA)

    val Fragment.navigationDataInt: Int?
        get() = arguments?.getInt(NAV_DATA_INT)

    val Fragment.navigationDataString: String?
        get() = arguments?.getString(NAV_DATA_STRING)
}
