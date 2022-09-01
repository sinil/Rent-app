package com.riwal.rentalapp.common.extensions.widgets.viewpager

import androidx.viewpager.widget.ViewPager

fun ViewPager.onStateChange(handler: (state: Int) -> Unit) {
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
            handler(state)
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {}
    })
}

fun ViewPager.onPageScrolled(handler: (position: Int, positionOffset: Float, positionOffsetPixels: Int) -> Unit) {
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            handler(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {}
    })
}

fun ViewPager.onPageChanged(handler: (position: Int) -> Unit) {
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            handler(position)
        }
    })
}

fun ViewPager.onUserScroll(handler: () -> Unit) = onStateChange { state ->
    if (state == ViewPager.SCROLL_STATE_DRAGGING) {
        handler()
    }
}