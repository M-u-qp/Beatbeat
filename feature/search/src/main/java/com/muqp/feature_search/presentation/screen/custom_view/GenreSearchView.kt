package com.muqp.feature_search.presentation.screen.custom_view

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import kotlin.math.min
import com.muqp.beatbeat.ui.R as CoreUi

class GenreSearchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private val titleView: TextView = TextView(context).apply {
        textSize = 30f
        setTypeface(typeface, Typeface.BOLD)
        text = context.getString(CoreUi.string.genres)
    }
    private val genreViews = mutableListOf<TextView>()
    private var itemClickListener: ((String) -> Unit)? = null

    private val itemSpacing = 4.dp
    private val titleMargin = 8.dp
    private val animDuration = 300L

    init {
        addView(titleView)
        setupGenreViews()
    }

    private fun setupGenreViews() {
        GenreSearch.entries.forEach { genre ->
            TextView(context).apply {
                setupGenreViewAppearance(genre)
                setupGenreClickListener(genre)
                genreViews.add(this)
                addView(this)
            }
        }
    }

    private fun TextView.setupGenreViewAppearance(genre: GenreSearch) {
        text = context.getString(genre.displayNameRes)
        background = ContextCompat.getDrawable(context, CoreUi.drawable.rounded_corner_background)
        backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(context, getGenreColorRes(genre))
        )
        setTextColor(ContextCompat.getColor(context, CoreUi.color.dark_brown))
        textSize = 20f
        setTypeface(typeface, Typeface.BOLD)
        gravity = Gravity.CENTER
        isClickable = true
        isFocusable = true
    }

    private fun TextView.setupGenreClickListener(genre: GenreSearch) {
        setOnClickListener { view ->
            animateGenreSelection(view, genre)
        }
    }

    private fun animateGenreSelection(view: View, genre: GenreSearch) {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val (x, y) = location

        val animView = createAnimView(view)
        val (centerX, centerY) = calculateAnimationCenter(view)

        val rootView = findRootView()
        rootView.addView(animView)
        animView.x = x.toFloat()
        animView.y = y.toFloat()

        startAnimation(animView, view, centerX, centerY, genre)
        view.animate().scaleX(0.9f).scaleY(0.9f).setDuration(animDuration / 2).start()
    }

    private fun createAnimView(sourceView: View): TextView {
        return TextView(context).apply {
            background = sourceView.background
            backgroundTintList = sourceView.backgroundTintList
            layoutParams = LayoutParams(sourceView.width, sourceView.height)
        }
    }

    private fun calculateAnimationCenter(view: View): Pair<Float, Float> {
        val displayMetrics = resources.displayMetrics
        return Pair(
            displayMetrics.widthPixels / 2f - view.width / 2f,
            displayMetrics.heightPixels / 2f - view.height / 2f
        )
    }

    private fun findRootView(): ViewGroup {
        return (context as? Activity)?.window?.decorView?.findViewById(android.R.id.content)
            ?: this
    }

    private fun startAnimation(
        animView: View,
        sourceView: View,
        centerX: Float,
        centerY: Float,
        genre: GenreSearch
    ) {
        animView.animate()
            .x(centerX)
            .y(centerY)
            .scaleX(resources.displayMetrics.widthPixels.toFloat() / sourceView.width)
            .scaleY(resources.displayMetrics.heightPixels.toFloat() / sourceView.height)
            .alpha(0.5f)
            .setDuration(animDuration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                (animView.parent as? ViewGroup)?.removeView(animView)
                itemClickListener?.invoke(context.getString(genre.displayNameRes))
            }
            .start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var totalHeight = 0

        measureChild(titleView, widthMeasureSpec, heightMeasureSpec)
        totalHeight += titleView.measuredHeight + titleMargin * 2

        val itemWidth = (width - paddingStart - paddingEnd - itemSpacing) / 2

        genreViews.forEach { view ->
            view.measure(
                MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY)
            )
        }

        totalHeight += (itemWidth + itemSpacing) * 3

        val finalHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(heightMeasureSpec)
            MeasureSpec.AT_MOST -> min(totalHeight, MeasureSpec.getSize(heightMeasureSpec))
            else -> totalHeight
        }

        setMeasuredDimension(width, finalHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val availableWidth = r - l - paddingStart - paddingEnd
        val itemWidth = (availableWidth - itemSpacing) / 2

        titleView.layout(
            paddingStart,
            paddingTop + titleMargin,
            paddingStart + titleView.measuredWidth,
            paddingTop + titleMargin + titleView.measuredHeight
        )

        var currentTop = paddingTop + titleView.measuredHeight + titleMargin * 2

        genreViews.chunked(2).forEach { rowViews ->
            var currentLeft = paddingStart

            rowViews.forEach { view ->
                view.layout(
                    currentLeft,
                    currentTop,
                    currentLeft + itemWidth,
                    currentTop + view.measuredHeight
                )
                currentLeft += itemWidth + itemSpacing
            }

            currentTop += rowViews.first().measuredHeight + itemSpacing
        }
    }

    fun setOnGenreClickListener(listener: (String) -> Unit) {
        itemClickListener = listener
    }

    fun show() {
        visibility = View.VISIBLE
    }

    fun hide() {
        visibility = View.GONE
    }

    private fun getGenreColorRes(genre: GenreSearch): Int {
        return when (genre) {
            GenreSearch.ROCK -> CoreUi.color.genre_rock
            GenreSearch.POP -> CoreUi.color.genre_pop
            GenreSearch.JAZZ -> CoreUi.color.genre_jazz
            GenreSearch.CLASSICAL -> CoreUi.color.genre_classical
            GenreSearch.HIPHOP -> CoreUi.color.genre_hiphop
            GenreSearch.ELECTRONIC -> CoreUi.color.genre_electronic
        }
    }

    private enum class GenreSearch(@StringRes val displayNameRes: Int) {
        ROCK(CoreUi.string.genre_rock),
        POP(CoreUi.string.genre_pop),
        JAZZ(CoreUi.string.genre_jazz),
        CLASSICAL(CoreUi.string.genre_classical),
        HIPHOP(CoreUi.string.genre_hiphop),
        ELECTRONIC(CoreUi.string.genre_electronic)
    }

    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()
}