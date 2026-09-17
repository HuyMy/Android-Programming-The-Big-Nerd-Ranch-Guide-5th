package com.huymy.example.draganddraw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import java.util.Collections.emptyList

private const val TAG = "BoxDrawingView"
private const val KEY_SUPER_STATE = "superState"
private const val KEY_BOXES = "boxes"

class BoxDrawingView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var currentBox: Box? = null
    private var boxes = mutableListOf<Box>()
    private val boxPaint = Paint().apply {
        color = resources.getColor(R.color.box_paint_color, null)
    }
    private val backgroundPaint = Paint().apply {
        color = resources.getColor(R.color.back_ground_paint_color, null)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        return Bundle().apply {
            putParcelable(KEY_SUPER_STATE, superState)
            putParcelableArrayList(KEY_BOXES, ArrayList(boxes))
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            boxes = state.getParcelableArrayList(KEY_BOXES, Box::class.java) ?: emptyList()
            Log.i(TAG, "Loaded ${boxes.size} boxes")
            super.onRestoreInstanceState(state.getParcelable(KEY_SUPER_STATE))
        } else {
            super.onRestoreInstanceState(state)
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val current = PointF(event.x, event.y)
        var action = ""
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                action = "ACTION_DOWN"
                //Reset drawing state
                currentBox = Box(current).also {
                    boxes.add(it)
                }

            }
            MotionEvent.ACTION_MOVE -> {
                action = "ACTION_MOVE"
                updateCurrentBox(current)
            }
            MotionEvent.ACTION_UP -> {
                action = "ACTION_UP"
                updateCurrentBox(current)
                currentBox = null
            }
            MotionEvent.ACTION_CANCEL -> {
                action = "ACTION_CANCEL"
                currentBox = null
            }
        }
        Log.i(TAG, "$action at x=${current.x}, y=${current.y}")

        return true
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPaint(backgroundPaint)
        boxes.forEach { box ->
            canvas.drawRect(box.toRect(), boxPaint)
        }
    }

    private fun updateCurrentBox(current: PointF) {
        currentBox?.let {
            it.end = current
            invalidate()
        }
    }
}
