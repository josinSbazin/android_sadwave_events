package com.sadwave.events.view

import android.view.View
import android.widget.TextView


inline var TextView.textOrGone: CharSequence?
    inline get() = text
    inline set(value) {
        text = value
        isVisibleOrGone = !value.isNullOrEmpty()
    }

var View.isVisibleOrGone: Boolean
    inline get() = visibility == View.VISIBLE
    inline set(value) {
        visibility = if (value) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }