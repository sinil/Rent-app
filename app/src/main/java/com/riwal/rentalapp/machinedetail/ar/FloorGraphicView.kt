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
import android.widget.FrameLayout
import com.riwal.rentalapp.common.extensions.android.dp
import com.riwal.rentalapp.common.extensions.android.graphics.lineTo
import com.riwal.rentalapp.common.extensions.android.graphics.moveTo

class FloorGraphicView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private lateinit var floorPath: Path
    private val lineWidth = 4f

    private val paint = Paint().apply {
        strokeWidth = dp(lineWidth)
        color = WHITE
        style = STROKE
        isAntiAlias = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        updateFloorPath()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(floorPath, paint)
    }

    private fun updateFloorPath() {

        val width = width.toFloat()
        val height = height.toFloat()

        val lineMargin = lineWidth / 2

        val topLeft = PointF(width / 4, 0f)
        val bottomLeft = PointF(lineMargin, height - lineMargin)
        val bottomRight = PointF(width - lineMargin, height - lineMargin)
        val topRight = PointF(width - width / 4, 0f)

        floorPath = Path().apply {
            moveTo(topLeft)
            lineTo(bottomLeft)
            lineTo(bottomRight)
            lineTo(topRight)
            close()
        }

    }

}
