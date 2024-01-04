package de.felixnuesse.timedsilence.extensions

import android.util.Log


fun Any.logE(message: String) {
    Log.e(TAG(), message)
}
fun Any.logD(message: String) {
    Log.d(TAG(), message)
}
fun Any.logI(message: String) {
    Log.i(TAG(), message)
}