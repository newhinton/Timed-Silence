package de.felixnuesse.timedsilence.handler

import de.felixnuesse.timedsilence.PrefConstants
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class PreferencesHolder(@Transient val mDefaultVolumeValue: Int = 80) {

    var alarmVolume = mDefaultVolumeValue
    var mediaVolume = mDefaultVolumeValue
    var ringerVolume = mDefaultVolumeValue
    var notificationVolume = mDefaultVolumeValue

    var defaultUnsetVolume = 0

    var ignoreAllDay = true
    var ignoreCancelled = false
    var ignoreFree = false
    var ignoreTentative = false


    var headsetConnectionCheck = PrefConstants.PREF_IGNORE_CHECK_WHEN_HEADSET_DEFAULT
    var shouldRestartOnBoot = PrefConstants.PREF_BOOT_RESTART_DEFAULT
    var showNotifications = PrefConstants.PREF_PAUSE_NOTIFICATION_DEFAULT
}
