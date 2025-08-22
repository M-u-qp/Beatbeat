package com.muqp.beatbeat.ui.utils

import android.view.View
import android.view.Window
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import java.lang.ref.WeakReference

class SystemBarsManager(window: Window) {
    private val windowRef = WeakReference(window)

    fun hideSystemBars() {
        windowRef.get()?.let { window ->
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, window.decorView).apply {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    fun setupDisplayCutout(rootView: View) {
        val rootViewRef = WeakReference(rootView)
        rootViewRef.get()?.let { safeRootView ->
            ViewCompat.setOnApplyWindowInsetsListener(safeRootView) { view, insets ->
                val bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() or
                            WindowInsetsCompat.Type.displayCutout()
                )
                view.updatePadding(
                    left = bars.left,
                    top = bars.top,
                    right = bars.right,
                    bottom = bars.bottom
                )
                WindowInsetsCompat.CONSUMED
            }
        }
    }
}