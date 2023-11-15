package de.felixnuesse.timedsilence.handler.trigger

import android.content.Context
import android.util.Log
import de.felixnuesse.timedsilence.PrefConstants
import de.felixnuesse.timedsilence.handler.SharedPreferencesHandler

class Trigger(val mContext: Context) {

    private var mTrigger: TriggerInterface

    init {
        mTrigger = when (getTriggertype()) {
            PrefConstants.PREF_TRIGGERTYPE_REPEATING -> {
                RepeatingAlarmHandler(mContext)
            }
            PrefConstants.PREF_TRIGGERTYPE_TARGETED -> {
                TargetedAlarmHandler(mContext)
            }
            else ->{
                TargetedAlarmHandler(mContext)
            }
        }
    }

    fun getTriggertype(): Int {
        val t = SharedPreferencesHandler.getPreferences(mContext)
        val defaultVal = PrefConstants.PREF_TRIGGERTYPE_DEFAULT
        return t?.getInt(PrefConstants.PREF_TRIGGERTYPE, defaultVal) ?: defaultVal
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