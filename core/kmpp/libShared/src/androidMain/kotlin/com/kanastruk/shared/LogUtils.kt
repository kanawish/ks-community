package com.kanastruk.shared

import android.util.Log

const val TAG = "libShared"

    actual fun logE(msg: String) {
        Log.e(TAG, msg)
    }

    actual fun logW(msg: String) {
        Log.w(TAG, msg)
    }

    actual fun logD(msg: String) {
        Log.d(TAG, msg)
    }