package com.communal_solutions.www.communalsolutions.HelperFiles

import android.util.Log

fun eLog(tag: String, msg: String) {
    Log.e(tag, msg)
    Thread.sleep(1)
}

fun dLog(tag: String, msg: String) {
    Log.d(tag, msg)
    Thread.sleep(1)
}

