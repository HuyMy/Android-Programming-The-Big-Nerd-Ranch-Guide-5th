package com.huymy.example.draganddraw

import android.graphics.PointF

data class Box(val start: PointF) {

    var end: PointF = start

    val left: Float
        get() = start.x.coerceAtMost(end.x)

    val right: Float
        get() = start.x.coerceAtLeast(end.x)

    val top: Float
        get() = start.y.coerceAtMost(end.y)

    val bottom: Float
        get() = start.y.coerceAtLeast(end.y)
}