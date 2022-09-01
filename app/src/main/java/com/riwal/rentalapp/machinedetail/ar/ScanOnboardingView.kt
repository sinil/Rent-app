package com.riwal.rentalapp.machinedetail.ar

import android.animation.ObjectAnimator
import android.animation.ValueAnimator.INFINITE
import android.content.Context
import android.graphics.Color.TRANSPARENT
import android.graphics.Path
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.*
import com.riwal.rentalapp.common.extensions.android.graphics.size
import com.riwal.rentalapp.common.extensions.animation.ViewProperties
import com.riwal.rentalapp.common.extensions.animation.setDuration
import com.riwal.rentalapp.common.extensions.datetime.seconds


class ScanOnboardingView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {


    /*--------------------------------------- Properties -----------------------------------------*/


    private var isAnimating = false

    private val scanOnboardingImageView = ImageView(context).apply {
        setImageDrawable(context.getDrawable(R.drawable.img_nexus))
    }


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        addView(scanOnboardingImageView)

        scanOnboardingImageView.apply {
            val imageSize = drawable.size
            val desiredHeight = 105 // 105 just happens to look good
            layoutWidth = dp((desiredHeight / imageSize.height * imageSize.width).toInt())
            layoutHeight = dp(desiredHeight)
        }

        // TODO: Without setting the background, the image gets cut off at the bottom, maybe there is a reason for that and a better solution
        backgroundColor = TRANSPARENT
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (!isAnimating) {
            startAnimating()
        }
    }


    /*------------------------------------- Private methods --------------------------------------*/


    private fun startAnimating() {

        isAnimating = true

        val animationRect = bounds.insetBy(dx = scanOnboardingImageView.width.toFloat() / 2, dy = scanOnboardingImageView.height.toFloat() / 2)

        val path = Path()
        path.addOval(animationRect, Path.Direction.CW)

        val animator = ObjectAnimator.ofObject(scanOnboardingImageView, ViewProperties.CENTER, null, path)
        animator.setDuration(4.seconds())
        animator.interpolator = LinearInterpolator()
        animator.repeatCount = INFINITE
        animator.start()
    }

}