package de.felixnuesse.timedsilence.extensions

import android.util.Log


fun Any.e(message: String) {
    Log.e(TAG(), message)
}
fun Any.d(message: String) {
    Log.d(TAG(), message)
}
fun Any.i(message: String) {
    Log.i(TAG(), message)
}