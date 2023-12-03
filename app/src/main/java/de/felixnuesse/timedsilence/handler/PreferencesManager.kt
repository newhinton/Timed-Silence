package de.felixnuesse.timedsilence.handler

import android.content.Context
import androidx.preference.PreferenceManager
import de.felixnuesse.timedsilence.PrefConstants
import de.felixnuesse.timedsilence.R

class PreferencesManager(private var mContext: Context) {

    private var mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
    private var mPreferencesEditor = mPreferences!!.edit()

    // This "Cache" is required for easy export&import
    private var mPreferencesHolder = PreferencesHolder(mContext.resources.getInteger(R.integer.pref_volume_default))

    fun getPreferenceHolder(): PreferencesHolder {
        checkIfHeadsetIsConnected()
        shouldShowNotification()
        shouldRestartOnBoot()

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
        mPreferencesHolder.headsetConnectionCheck = mPreferences.getBoolean(PrefConstants.PREF_IGNORE_CHECK_WHEN_HEADSET, mPreferencesHolder.headsetConnectionCheck)
        return mPreferencesHolder.headsetConnectionCheck
    }

    fun shouldRestartOnBoot(): Boolean {
        mPreferencesHolder.shouldRestartOnBoot = mPreferences.getBoolean(PrefConstants.PREF_BOOT_RESTART, mPreferencesHolder.shouldRestartOnBoot)
        return mPreferencesHolder.shouldRestartOnBoot
    }

    fun setRestartOnBoot(restart: Boolean) {
        mPreferencesEditor.putBoolean(PrefConstants.PREF_BOOT_RESTART, restart)
        mPreferencesEditor.apply()
    }

    fun shouldShowNotification(): Boolean {
        mPreferencesHolder.showNotifications = mPreferences.getBoolean(PrefConstants.PREF_PAUSE_NOTIFICATION, mPreferencesHolder.showNotifications)
        return mPreferencesHolder.showNotifications
    }


    ///////////////////////
    // --- Calendar --- ///
    ///////////////////////

    fun ignoreAllday(): Boolean {
        return mPreferences.getBoolean(mContext.getString(R.string.pref_calendar_ignore_allday), mPreferencesHolder.ignoreAllDay)
    }

    fun ignoreTentative(): Boolean {
        return mPreferences.getBoolean(mContext.getString(R.string.pref_calendar_ignore_tentative), mPreferencesHolder.ignoreTentative)
    }

    fun ignoreCancelled(): Boolean {
        return mPreferences.getBoolean(mContext.getString(R.string.pref_calendar_ignore_cancelled), mPreferencesHolder.ignoreCancelled)
    }

    fun ignoreFree(): Boolean {
        return mPreferences.getBoolean(mContext.getString(R.string.pref_calendar_ignore_free), mPreferencesHolder.ignoreFree)
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
        return mPreferences.getInt(mContext.getString(resourceId), mContext.resources.getInteger(R.integer.pref_volume_default))
    }

    fun getDefaultUnsetVolume(): Int {
        var defaultValue = mContext.resources.getInteger(R.integer.pref_volume_unset_default)
        var value = mPreferences.getString(mContext.getString(R.string.pref_volume_unset_value), defaultValue.toString())
        mPreferencesHolder.defaultUnsetVolume = value?.toInt() ?: defaultValue
        return mPreferencesHolder.defaultUnsetVolume
    }

    fun applyPreferenceHolder(preferences: PreferencesHolder) {
        setRestartOnBoot(preferences.shouldRestartOnBoot)
        mPreferencesEditor.putBoolean(PrefConstants.PREF_IGNORE_CHECK_WHEN_HEADSET, mPreferencesHolder.headsetConnectionCheck)
        mPreferencesEditor.putBoolean(PrefConstants.PREF_PAUSE_NOTIFICATION, mPreferencesHolder.showNotifications)

        mPreferencesEditor.putInt(mContext.getString(R.string.pref_volume_media), preferences.mediaVolume)
        mPreferencesEditor.putInt(mContext.getString(R.string.pref_volume_notification), preferences.notificationVolume)
        mPreferencesEditor.putInt(mContext.getString(R.string.pref_volume_ringer), preferences.ringerVolume)
        mPreferencesEditor.putInt(mContext.getString(R.string.pref_volume_alarm), preferences.alarmVolume)

        mPreferencesEditor.putString(mContext.getString(R.string.pref_volume_unset_value), preferences.defaultUnsetVolume.toString())

        mPreferencesEditor.putBoolean(mContext.getString(R.string.pref_calendar_ignore_allday), preferences.ignoreAllDay)
        mPreferencesEditor.putBoolean(mContext.getString(R.string.pref_calendar_ignore_tentative), preferences.ignoreTentative)
        mPreferencesEditor.putBoolean(mContext.getString(R.string.pref_calendar_ignore_cancelled), preferences.ignoreCancelled)
        mPreferencesEditor.putBoolean(mContext.getString(R.string.pref_calendar_ignore_free), preferences.ignoreFree)

    }
}