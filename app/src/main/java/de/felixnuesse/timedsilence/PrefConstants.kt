package de.felixnuesse.timedsilence;


/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 10.04.19 - 18:07
 *
 * Edited by: Felix Nüsse felix.nuesse(at)t-online.de
 *
 *
 * This program is released under the GPLv3 license
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 *
 *
 *
 */


class PrefConstants {
    companion object {

        const val DEFAULT_DELAY = 60
        const val PREFS_NAME = "prefsname"

        const val PREFS_LAST_KEY_EXEC = "last_ExecTime"


        const val PREF_INTERVAL_CHECK = "PREF_INTERVAL_CHECK"
        const val PREF_INTERVAL_CHECK_DEFAULT = 15


        const val PREF_BOOT_RESTART = "PREF_BOOT_RESTART"
        const val PREF_BOOT_RESTART_DEFAULT = false


        const val PREF_IGNORE_CHECK_WHEN_HEADSET= "PREF_IGNORE_CHECK_WHEN_HEADSET"
        const val PREF_IGNORE_CHECK_WHEN_HEADSET_DEFAULT = true


        const val PREF_DARKMODE= "PREF_DARKMODE"
        const val PREF_DARKMODE_DEFAULT = 0
        const val PREF_DARKMODE_LIGHT = 0
        const val PREF_DARKMODE_DARK = 1
        const val PREF_DARKMODE_AUTO = 2


        const val PREF_PAUSE_NOTIFICATION= "PREF_PAUSE_NOTIFICATION"
        const val PREF_PAUSE_NOTIFICATION_DEFAULT = true



        const val VOLUME_LOW_WARNING_THRESHOLD = 15

        const val PREF_VOLUME_ALARM = "PREF_VOLUME_ALARM"
        const val PREF_VOLUME_ALARM_DEFAULT = 80


        const val PREF_VOLUME_RINGER = "PREF_VOLUME_RINGER"
        const val PREF_VOLUME_RINGER_DEFAULT = 80


        const val PREF_VOLUME_NOTIFICATION = "PREF_VOLUME_NOTIFICATION"
        const val PREF_VOLUME_NOTIFICATION_DEFAULT = 80


        const val PREF_VOLUME_MUSIC = "PREF_VOLUME_MUSIC"
        const val PREF_VOLUME_MUSIC_DEFAULT = 80


    }
}
