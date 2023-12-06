package de.felixnuesse.timedsilence.handler

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class PreferencesHolder(@Transient val mDefaultVolumeValue: Int = 80) {

    @EncodeDefault var alarmVolume = mDefaultVolumeValue
    @EncodeDefault var mediaVolume = mDefaultVolumeValue
    @EncodeDefault var ringerVolume = mDefaultVolumeValue
    @EncodeDefault var notificationVolume = mDefaultVolumeValue

    @EncodeDefault var defaultUnsetVolume = 0

    @EncodeDefault var ignoreAllDay = true
    @EncodeDefault var cancelled = false
    @EncodeDefault var ignoreFree = false
    @EncodeDefault var ignoreTentative = false


    @EncodeDefault var headsetConnectionCheck = true
    @Transient var shouldRestartOnBoot = false
    @EncodeDefault var showNotifications = true
    @EncodeDefault var runWhenIdle = true

    @EncodeDefault var changeRingerVolume = false
}
