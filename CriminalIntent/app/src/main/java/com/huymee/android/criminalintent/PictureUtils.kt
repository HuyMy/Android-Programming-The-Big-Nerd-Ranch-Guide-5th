package com.huymee.android.criminalintent

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlin.math.roundToInt

fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true

    BitmapFactory.decodeFile(path, options)

    val srcWidth = options.outWidth.toFloat()
    val srcHeight = options.outHeight.toFloat()

    val sampleSize = if (srcWidth <= destWidth && srcHeight <= destHeight) {
        1
    } else {
        val heightScale = srcHeight / destHeight
        val widthScale = srcWidth / destWidth

        minOf(widthScale, heightScale).roundToInt()
    }

    return BitmapFactory.decodeFile(path, BitmapFactory.Options().apply {
        inSampleSize = sampleSize
    })
}