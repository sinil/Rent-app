package com.riwal.rentalapp.helpmechoose

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.addSubview
import com.riwal.rentalapp.common.extensions.android.resourceIdForAttributeId
import com.riwal.rentalapp.common.extensions.android.dimen
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.helpmechoose.HelpMeChooseOptionCard.Selection.*
import kotlinx.android.synthetic.main.help_me_choose_option_button_contents.view.*

class HelpMeChooseOptionCard @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : CardView(context, attrs, defStyleAttr) {

    var selection = NEUTRAL
        set(value) {
            field = value
            updateUI()
        }

    var text: CharSequence?
        get() = titleTextView.text
        set(value) {
            titleTextView.text = value
        }

    var scaleType
        get() = imageView.scaleType
        set(value) {
            imageView.scaleType = value
        }

    fun setImageResource(@DrawableRes resourceId: Int) {
        imageView.setImageResource(resourceId)
    }

    init {

        addSubview(R.layout.help_me_choose_option_button_contents)

        if (attrs != null) {
            val styledAttributes = context.theme.obtainStyledAttributes(attrs, R.styleable.HelpMeChooseOptionCard, 0, 0)
            val imageResource = styledAttributes.getResourceId(R.styleable.HelpMeChooseOptionCard_android_src, -1)
            val defaultForegroundResource = context.resourceIdForAttributeId(R.attr.selectableItemBackground)
            val foregroundResource = styledAttributes.getResourceId(R.styleable.HelpMeChooseOptionCard_android_foreground, defaultForegroundResource)
            val rawScaleType = styledAttributes.getInt(R.styleable.HelpMeChooseOptionCard_android_scaleType, -1)

            text = styledAttributes.getString(R.styleable.HelpMeChooseOptionCard_android_text)

            if (imageResource != -1) {
                setImageResource(imageResource)
            }

            foreground = resources.getDrawable(foregroundResource, context.theme)

            if (rawScaleType != -1) {
                scaleType = ImageView.ScaleType.values()[rawScaleType]
            }

            styledAttributes.recycle()
        }

    }

    fun updateUI() {
        translationZ = if (selection == SELECTED) dimen(R.dimen.card_raised_elevation).toFloat() else dimen(R.dimen.card_resting_elevation).toFloat()
        transparentOverlay.isVisible = (selection == NOT_SELECTED)
    }

    enum class Selection {
        NEUTRAL,
        SELECTED,
        NOT_SELECTED
    }

}
