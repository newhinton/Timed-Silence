package de.felixnuesse.timedsilence.handler

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import de.felixnuesse.timedsilence.MainActivity
import de.felixnuesse.timedsilence.R

class PreferencesManager(private var mContext: Context) {

    private var mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext.applicationContext)
    private var mPreferencesEditor = mPreferences!!.edit()

    // This "Cache" is required for easy export&import
    private var mPreferencesHolder = PreferencesHolder(mContext.resources.getInteger(R.integer.pref_volume_default))

    fun getPreferenceHolder(): PreferencesHolder {
        checkIfHeadsetIsConnected()
        shouldShowNotification()
        shouldRestartOnBoot()
        runWhenIdle()

        getRingerVolume()
        getAlarmVolume()
        getMediaVolume()
        getNotificationVolume()
        getDefaultUnsetVolume()

        ignoreAllday()
        ignoreFree()
        ignoreTentative()
        ignoreCancelled()

        return mPreferencesHolder
    }

    fun checkIfHeadsetIsConnected(): Boolean {
        mPreferencesHolder.headsetConnectionCheck = mPreferences.getBoolean(getKey(R.string.pref_general_dont_check_with_connected_headset), mPreferencesHolder.headsetConnectionCheck)
        return mPreferencesHolder.headsetConnectionCheck
    }

    fun shouldRestartOnBoot(): Boolean {
        mPreferencesHolder.shouldRestartOnBoot = mPreferences.getBoolean(getKey(R.string.pref_general_boot_restart), mPreferencesHolder.shouldRestartOnBoot)
        return mPreferencesHolder.shouldRestartOnBoot
    }

    fun setRestartOnBoot(restart: Boolean) {
        mPreferencesEditor.putBoolean(getKey(R.string.pref_general_boot_restart), restart)
        mPreferencesEditor.apply()
    }

    fun shouldShowNotification(): Boolean {
        mPreferencesHolder.showNotifications = mPreferences.getBoolean(getKey(R.string.pref_general_show_notifications_when_paused), mPreferencesHolder.showNotifications)
        return mPreferencesHolder.showNotifications
    }

    fun runWhenIdle(): Boolean {
        mPreferencesHolder.runWhenIdle = mPreferences.getBoolean(getKey(R.string.pref_general_run_when_idle), mPreferencesHolder.runWhenIdle)
        return mPreferencesHolder.runWhenIdle
    }

    fun setRunWhenIdle(shouldRun: Boolean) {
        mPreferencesEditor.putBoolean(getKey(R.string.pref_general_run_when_idle), shouldRun)
        mPreferencesEditor.apply()
    }


    ///////////////////////
    // --- Calendar --- ///
    ///////////////////////

    fun ignoreAllday(): Boolean {
        return mPreferences.getBoolean(getKey(R.string.pref_calendar_ignore_allday), mPreferencesHolder.ignoreAllDay)
    }

    fun ignoreTentative(): Boolean {
        return mPreferences.getBoolean(getKey(R.string.pref_calendar_ignore_tentative), mPreferencesHolder.ignoreTentative)
    }

    fun ignoreCancelled(): Boolean {
        return mPreferences.getBoolean(getKey(R.string.pref_calendar_ignore_cancelled), mPreferencesHolder.cancelled)
    }

    fun ignoreFree(): Boolean {
        return mPreferences.getBoolean(getKey(R.string.pref_calendar_ignore_free), mPreferencesHolder.ignoreFree)
    }


    ///////////////////////
    // ---- Volume ---- ///
    ///////////////////////

    fun getAlarmVolume(): Int {
        mPreferencesHolder.alarmVolume = getVolume(R.string.pref_volume_alarm)
        return mPreferencesHolder.alarmVolume
    }
    fun getRingerVolume(): Int {
        mPreferencesHolder.ringerVolume = getVolume(R.string.pref_volume_ringer)
        return mPreferencesHolder.ringerVolume
    }
    fun getNotificationVolume(): Int {
        mPreferencesHolder.notificationVolume = getVolume(R.string.pref_volume_notification)
        return mPreferencesHolder.notificationVolume
    }
    fun getMediaVolume(): Int {
        mPreferencesHolder.mediaVolume = getVolume(R.string.pref_volume_media)
        return mPreferencesHolder.mediaVolume
    }

    private fun getVolume(resourceId: Int): Int {
        return mPreferences.getInt(getKey(resourceId), mContext.resources.getInteger(R.integer.pref_volume_default))
    }

    fun getDefaultUnsetVolume(): Int {
        var defaultValue = mContext.resources.getInteger(R.integer.pref_volume_unset_default)
        var value = mPreferences.getString(getKey(R.string.pref_volume_unset_value), defaultValue.toString())
        mPreferencesHolder.defaultUnsetVolume = value?.toInt() ?: defaultValue
        return mPreferencesHolder.defaultUnsetVolume
    }

    fun applyPreferenceHolder(preferences: PreferencesHolder) {
        setRestartOnBoot(preferences.shouldRestartOnBoot)
        setRunWhenIdle(preferences.runWhenIdle)
        mPreferencesEditor.putBoolean(getKey(R.string.pref_general_dont_check_with_connected_headset), mPreferencesHolder.headsetConnectionCheck)
        mPreferencesEditor.putBoolean(getKey(R.string.pref_general_show_notifications_when_paused), mPreferencesHolder.showNotifications)

        mPreferencesEditor.putInt(getKey(R.string.pref_volume_media), preferences.mediaVolume)
        mPreferencesEditor.putInt(getKey(R.string.pref_volume_notification), preferences.notificationVolume)
        mPreferencesEditor.putInt(getKey(R.string.pref_volume_ringer), preferences.ringerVolume)
        mPreferencesEditor.putInt(getKey(R.string.pref_volume_alarm), preferences.alarmVolume)

        mPreferencesEditor.putString(getKey(R.string.pref_volume_unset_value), preferences.defaultUnsetVolume.toString())

        mPreferencesEditor.putBoolean(getKey(R.string.pref_calendar_ignore_allday), preferences.ignoreAllDay)
        mPreferencesEditor.putBoolean(getKey(R.string.pref_calendar_ignore_tentative), preferences.ignoreTentative)
        mPreferencesEditor.putBoolean(getKey(R.string.pref_calendar_ignore_cancelled), preferences.cancelled)
        mPreferencesEditor.putBoolean(getKey(R.string.pref_calendar_ignore_free), preferences.ignoreFree)
        mPreferencesEditor.apply()
    }

    private fun getKey(resId: Int): String {
        return mContext.getString(resId)
    }
}