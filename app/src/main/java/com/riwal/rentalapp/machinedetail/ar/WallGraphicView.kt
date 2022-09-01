package com.riwal.rentalapp.machinedetail.ar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.WHITE
import android.graphics.Paint
import android.graphics.Paint.Style.STROKE
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import com.riwal.rentalapp.common.extensions.android.dp
import com.riwal.rentalapp.common.extensions.android.graphics.lineTo
import com.riwal.rentalapp.common.extensions.android.graphics.moveTo

class WallGraphicView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {


    /*--------------------------------------- Properties -----------------------------------------*/


    private lateinit var wallAndFloorPath: Path
    private val lineWidth = 4f

    private val paint = Paint().apply {
        strokeWidth = dp(lineWidth)
        color = WHITE
        style = STROKE
        isAntiAlias = true
    }


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        updateWallAndFloorPath()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(wallAndFloorPath, paint)
    }


    /*------------------------------------ Private methods ---------------------------------------*/


    private fun updateWallAndFloorPath() {

        val width = width.toFloat()
        val height = height.toFloat()

        val lineMargin = lineWidth / 2

        val horizontalWallMargin = width / 8
        val bottomWallMargin = height / 8

        val wallTopLeft = PointF(horizontalWallMargin, lineMargin)
        val wallBottomLeft = PointF(horizontalWallMargin, height - bottomWallMargin)
        val wallBottomRight = PointF(width - horizontalWallMargin, height - bottomWallMargin)
        val wallTopRight = PointF(width - horizontalWallMargin, lineMargin)

        val floorBottomLeft = PointF(0f, height)
        val floorBottomRight = PointF(width, height)

        wallAndFloorPath = Path().apply {
            moveTo(wallTopLeft)
            lineTo(wallBottomLeft)
            lineTo(wallBottomRight)
            lineTo(wallTopRight)
            close()

            moveTo(wallBottomLeft)
            lineTo(floorBottomLeft)

            moveTo(wallBottomRight)
            lineTo(floorBottomRight)
        }

    }

}
