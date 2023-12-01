package de.felixnuesse.timedsilence.handler.trigger

import android.content.Context
import de.felixnuesse.timedsilence.PrefConstants
import de.felixnuesse.timedsilence.handler.PreferencesManager

class Trigger(mContext: Context) {

    private var mTrigger: TriggerInterface = when (PreferencesManager(mContext).getTriggerType()) {
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