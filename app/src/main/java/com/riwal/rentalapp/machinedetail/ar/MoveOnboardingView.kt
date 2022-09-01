package com.riwal.rentalapp.machinedetail.ar

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color.TRANSPARENT
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.Vector2
import com.riwal.rentalapp.common.extensions.android.backgroundColor
import com.riwal.rentalapp.common.extensions.android.dp
import com.riwal.rentalapp.common.extensions.android.graphics.size
import com.riwal.rentalapp.common.extensions.android.layoutHeight
import com.riwal.rentalapp.common.extensions.android.layoutWidth
import com.riwal.rentalapp.common.extensions.animation.*
import com.riwal.rentalapp.common.extensions.datetime.seconds


class MoveOnboardingView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {


    /*--------------------------------------- Properties -----------------------------------------*/


    private var isAnimating = false

    private val moveOnboardingImageView = ImageView(context).apply {
        setImageDrawable(context.getDrawable(R.drawable.img_swipe))
    }


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        addView(moveOnboardingImageView)

        moveOnboardingImageView.apply {
            val imageSize = drawable.size
            val desiredHeight = 215 // 215 just happens to look good
            layoutWidth = dp((desiredHeight / imageSize.height * imageSize.width).toInt())
            layoutHeight = dp(desiredHeight)
        }

        clipChildren = false
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

        val scaleAnimation = ObjectAnimator.ofObject(moveOnboardingImageView, ViewProperties.SCALE, Vector2Interpolator, Vector2(x = 1.2f, y = 1.2f), Vector2(x = 1.0f, y = 1.0f))
        scaleAnimation.setDuration(0.5.seconds())
        scaleAnimation.interpolator = AccelerateInterpolator()

        val moveAnimation = ObjectAnimator.ofFloat(moveOnboardingImageView, View.TRANSLATION_X, 0f, width.toFloat() - moveOnboardingImageView.width)
        moveAnimation.setStartDelay(0.75.seconds())
        moveAnimation.setDuration(2.seconds())
        moveAnimation.interpolator = AccelerateDecelerateInterpolator()

        val idleAnimation = IdleAnimator()
        idleAnimation.setStartDelay(2.75.seconds())
        idleAnimation.setDuration(0.75.seconds())

        val animators = AnimatorSet()
        animators.playTogether(scaleAnimation, moveAnimation, idleAnimation)
        animators.onEnd { animators.start() }
        animators.start()
    }

}

