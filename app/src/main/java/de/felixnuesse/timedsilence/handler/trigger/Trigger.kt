package de.felixnuesse.timedsilence.handler.trigger

import android.content.Context
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

    fun getTriggertype(): String {
        val t = SharedPreferencesHandler.getPreferences(mContext)
        val type: String? = t?.getString(PrefConstants.PREF_TRIGGERTYPE, PrefConstants.PREF_TRIGGERTYPE_DEFAULT)
        return type?: PrefConstants.PREF_TRIGGERTYPE_DEFAULT
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