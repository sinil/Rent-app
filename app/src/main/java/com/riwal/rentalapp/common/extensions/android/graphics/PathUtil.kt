package com.riwal.rentalapp.common.extensions.android.graphics

import android.graphics.Path
import android.graphics.PointF

fun Path.moveTo(point: PointF) = moveTo(point.x, point.y)
fun Path.lineTo(point: PointF) = lineTo(point.x, point.y)