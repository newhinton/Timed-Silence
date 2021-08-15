package de.felixnuesse.timedsilence.handler.trigger

import android.app.PendingIntent
import android.content.Context

class Trigger(mContext: Context) {

    private var mTrigger: TriggerInterface

    init {
        mTrigger = TargetedAlarmHandler(mContext)
    }

    fun createTimecheck(){
        mTrigger.createTimecheck()
    }

    fun removeTimecheck(){
        mTrigger.removeTimecheck()
    }

    fun checkIfNextAlarmExists(): Boolean {
        return mTrigger.checkIfNextAlarmExists()
    }

    fun getNextAlarmTimestamp(): String {
        return mTrigger.getNextAlarmTimestamp()
    }
}