package com.muqp.core_ui.common

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.view.isVisible

object AnimationExpandedHelper {

    fun expandViewWithAnimation(
        view: View,
        arrowView: ImageView? = null,
        animationResId: Int? = null,
        onEnd: (() -> Unit)? = null,
        shouldRotateArrow: Boolean = true
    ) {
        view.isVisible = true

        if (animationResId != null) {
            val anim = AnimationUtils.loadAnimation(view.context, animationResId)
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {}
                override fun onAnimationEnd(p0: Animation?) {
                    onEnd?.invoke()
                }

                override fun onAnimationRepeat(p0: Animation?) {}
            })
            view.startAnimation(anim)
        } else {
            view.alpha = 0f
            view.translationY = -20f
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .withEndAction { onEnd?.invoke() }
                .start()
        }
        if (shouldRotateArrow) {
            arrowView?.animate()?.rotation(180f)?.setDuration(300)?.start()
        }
    }

    fun collapseViewWithAnimation(
        view: View,
        arrowView: ImageView? = null,
        animationResId: Int? = null,
        onEnd: (() -> Unit)? = null,
        shouldRotateArrow: Boolean = true
    ) {
        if (animationResId != null) {
            val anim = AnimationUtils.loadAnimation(view.context, animationResId)
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    view.isVisible = false
                    onEnd?.invoke()
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
            view.startAnimation(anim)
        } else {
            view.animate()
                .alpha(0f)
                .translationY(-20f)
                .setDuration(300)
                .withEndAction {
                    view.isVisible = false
                    view.alpha = 1f
                    view.translationY = 0f
                    onEnd?.invoke()
                }
                .start()
        }

        if (shouldRotateArrow) {
            arrowView?.animate()?.rotation(0f)?.setDuration(300)?.start()
        }
    }
}