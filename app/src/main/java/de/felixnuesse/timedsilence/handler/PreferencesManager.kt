package de.felixnuesse.timedsilence.handler

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import de.felixnuesse.timedsilence.PrefConstants
import de.felixnuesse.timedsilence.R

class PreferencesManager(private var mContext: Context) {

    @Deprecated("use mPreferences and migrate!")
    private var mOldPreferences = mContext.getSharedPreferences(PrefConstants.PREFS_NAME, 0)
    private var mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
    private var mPrefEditor = mOldPreferences!!.edit()




    fun getTriggerType(): Int {
        return mOldPreferences.getInt(PrefConstants.PREF_TRIGGERTYPE, PrefConstants.PREF_TRIGGERTYPE_DEFAULT)
    }

    fun getTriggerInterval(): Int {
        return mOldPreferences.getInt(PrefConstants.PREF_INTERVAL_CHECK, PrefConstants.PREF_INTERVAL_CHECK_DEFAULT)
    }
    fun setTriggerInterval(interval: Int) {
        mPrefEditor.putInt(PrefConstants.PREF_INTERVAL_CHECK, interval)
        mPrefEditor.apply()
    }

    fun headsetCheck(): Boolean {
        return mOldPreferences.getBoolean(PrefConstants.PREF_IGNORE_CHECK_WHEN_HEADSET, PrefConstants.PREF_IGNORE_CHECK_WHEN_HEADSET_DEFAULT)
    }

    fun shouldRestartOnBoot(): Boolean {
        return mOldPreferences.getBoolean(PrefConstants.PREF_BOOT_RESTART, PrefConstants.PREF_BOOT_RESTART_DEFAULT)
    }

    fun setRestartOnBoot(restart: Boolean) {
        mPrefEditor.putBoolean(PrefConstants.PREF_BOOT_RESTART, restart)
        mPrefEditor.apply()
    }
    fun shouldShowNotification(): Boolean {
        return mOldPreferences.getBoolean(PrefConstants.PREF_PAUSE_NOTIFICATION, PrefConstants.PREF_PAUSE_NOTIFICATION_DEFAULT)
    }



    ///////////////////////
    // --- Calendar --- ///
    ///////////////////////

    fun ignoreAllday(): Boolean {
        return mPreferences.getBoolean(mContext.getString(R.string.pref_calendar_ignore_allday), true)
    }

    fun ignoreTentative(): Boolean {
        return mPreferences.getBoolean(mContext.getString(R.string.pref_calendar_ignore_tentative), false)
    }

    fun ignoreCancelled(): Boolean {
        return mPreferences.getBoolean(mContext.getString(R.string.pref_calendar_ignore_cancelled), false)
    }

    fun ignoreFree(): Boolean {
        return mPreferences.getBoolean(mContext.getString(R.string.pref_calendar_ignore_free), false)
    }


    ///////////////////////
    // ---- Volume ---- ///
    ///////////////////////

    fun getVolume(resourceId: Int): Int {
        return mPreferences.getInt(mContext.getString(resourceId), mContext.resources.getInteger(R.integer.pref_volume_default))
    }

    fun getDefaultVolume(): Int {
        var defaultValue = mContext.resources.getInteger(R.integer.pref_volume_unset_default)
        var value = mPreferences.getString(mContext.getString(R.string.pref_volume_unset_value), defaultValue.toString())
        return value?.toInt() ?: defaultValue
    }
}