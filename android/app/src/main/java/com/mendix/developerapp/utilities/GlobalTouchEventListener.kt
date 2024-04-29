package com.mendix.developerapp.utilities

import android.view.MotionEvent

interface GlobalTouchEventListener {
    fun dispatchTouchEvent(ev: MotionEvent?): Boolean
}
