package de.felixnuesse.timedsilence.model.data

import java.util.*

class CachedArrayList<T> : ArrayList<T>() {
    var cacheInitialized = false

    fun set(newlist: ArrayList<T>){
        clear()
        addAll(newlist)
        cacheInitialized = true
    }
}