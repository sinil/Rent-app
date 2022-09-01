package com.riwal.rentalapp.common.ui.easyrecyclerview

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.riwal.rentalapp.common.extensions.android.*
import com.riwal.rentalapp.common.extensions.widgets.recyclerview.dp
import com.riwal.rentalapp.common.ui.easyrecyclerview.MaterialRowViewHolder.ImageStyle.*
import com.riwal.rentalapp.common.ui.easyrecyclerview.MaterialRowViewHolder.MaterialStyle.*
import kotlinx.android.synthetic.main.row_material_list_item.view.*


open class MaterialRowViewHolder(itemView: View) : EasyRecyclerView.ViewHolder(itemView) {


    /*---------------------------------------- Properties ----------------------------------------*/


    val imageView
        get() = itemView.iconImageView!!

    val titleTextView
        get() = itemView.titleTextView!!

    val subtitleTextView
        get() = itemView.subtitleTextView!!

    val accessoryTextView
        get() = itemView.accessoryTextView!!

    val accessoryImageView
        get() = itemView.accessoryImageView!!

    var title: String?
        get() = titleTextView.text.toString()
        set(title) {
            titleTextView.text = title
        }

    var subtitle: String?
        get() = subtitleTextView.text.toString()
        set(subtitle) {
            subtitleTextView.text = subtitle
            subtitleTextView.isVisible = !subtitle.isNullOrEmpty()
        }

    var accessoryText: String?
        get() = accessoryTextView.text.toString()
        set(accessoryText) {
            accessoryTextView.text = accessoryText
            accessoryTextView.isVisible = true
        }

    var style: MaterialStyle = SINGLE_LINE
        set(value) {
            field = value
            updateUI()
        }

    private var imageStyle = NONE
        set(value) {
            field = value
            updateUI()
        }

    private val titleTextViewStartMargin
        get() = when {
            style == THREE_LINES && imageStyle == RECTANGLE_LARGE -> dp(20)
            imageStyle == SQUARE_SMALL -> dp(32)
            else -> dp(16)
        }

    private val imageViewTopMargin
        get() = when {
            style == SINGLE_LINE && imageStyle in listOf(CIRCLE_MEDIUM, SQUARE_MEDIUM, SQUARE_LARGE, RECTANGLE_LARGE) -> dp(8)
            style == TWO_LINES && imageStyle == RECTANGLE_LARGE -> dp(8)
            else -> dp(16)
        }

    private val imageViewWidth
        get() = if (imageStyle == RECTANGLE_LARGE) dp(100) else imageViewHeight

    private val imageViewHeight
        get() = when (imageStyle) {
            NONE -> dp(0)
            SQUARE_SMALL -> dp(24)
            CIRCLE_MEDIUM, SQUARE_MEDIUM -> dp(40)
            SQUARE_LARGE, RECTANGLE_LARGE -> dp(56)
        }

    private val rowHeight
        get() = when (style) {
            SINGLE_LINE -> when (imageStyle) {
                NONE -> dp(48)
                SQUARE_SMALL, CIRCLE_MEDIUM, SQUARE_MEDIUM -> dp(56)
                SQUARE_LARGE, RECTANGLE_LARGE -> dp(72)
            }
            TWO_LINES -> when (imageStyle) {
                NONE -> dp(64)
                else -> dp(72)
            }
            THREE_LINES -> dp(88)
        }

    private val marginStart
        get() = if (imageStyle == RECTANGLE_LARGE) dp(0) else dp(16)


    /*------------------------------------------ Methods -----------------------------------------*/


    fun setImage(@DrawableRes iconRes: Int, imageStyle: ImageStyle) {
        this.imageStyle = imageStyle
        imageView.setImageResource(iconRes)
        imageView.isVisible = true
    }

    fun setTitle(@StringRes titleRes: Int) {
        titleTextView.setText(titleRes)
    }

    fun setSubtitle(@StringRes subtitleRes: Int) {
        subtitleTextView.setText(subtitleRes)
        subtitleTextView.isVisible = true
    }

    fun setAccessoryIcon(@DrawableRes iconRes: Int) {
        accessoryImageView.setImageResource(iconRes)
        accessoryImageView.isVisible = true
    }


    /*-------------------------------------- Private methods -------------------------------------*/


    private fun updateUI() {

        itemView.layoutHeight = rowHeight

        imageView.marginLeft = marginStart
        imageView.marginTop = imageViewTopMargin
        imageView.layoutHeight = imageViewHeight
        imageView.layoutWidth = imageViewWidth
        imageView.isVisible = imageStyle != NONE

        titleTextView.marginLeft = titleTextViewStartMargin

        subtitleTextView.setLines(if (style == THREE_LINES) 2 else 1)
    }


    /*------------------------------------------- Enums ------------------------------------------*/


    enum class MaterialStyle {
        SINGLE_LINE,
        TWO_LINES,
        THREE_LINES
    }

    enum class ImageStyle {
        NONE,
        SQUARE_SMALL,
        CIRCLE_MEDIUM,
        SQUARE_MEDIUM,
        SQUARE_LARGE,
        RECTANGLE_LARGE,
    }

}
