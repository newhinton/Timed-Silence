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


class Constants {
    companion object {
        const val APP_NAME = "Timed Silence"
        const val BROADCAST_INTENT_ACTION = "action"
        const val BROADCAST_INTENT_ACTION_UPDATE_VOLUME = "volumeUpdate"
        const val BROADCAST_INTENT_ACTION_DELAY = "delayVolumeUpdate"
        const val BROADCAST_INTENT_ACTION_DELAY_EXTRA = "delayVolumeUpdate_EXTRA"
        const val BROADCAST_INTENT_ACTION_DELAY_RESTART_NOW = "restartDelayNow"

        const val TIME_SETTING_SILENT = 1
        const val TIME_SETTING_VIBRATE = 2
        const val TIME_SETTING_LOUD = 3


        const val WIFI_TYPE_CONNECTED = 1
        const val WIFI_TYPE_SEARCHING = 2

        const val RECURRING_INTENT_ID = 123789


        const val SERVICE_INTENT_DELAY_ACTION = "SERVICE_INTENT_DELAY_ACTION"
        const val SERVICE_INTENT_DELAY_ACTION_TOGGLE = "SERVICE_INTENT_DELAY_ACTION_TOGGLE"
        const val SERVICE_INTENT_DELAY_AMOUNT = "SERVICE_INTENT_DELAY_AMOUNT"
        const val SERVICE_INTENT_TICK_ACTION = "SERVICE_INTENT_TICK_ACTION"
        const val SERVICE_INTENT_STOP_ACTION = "SERVICE_INTENT_STOP_ACTION"


        const val WIDGET_SERVICE_UPDATE_STATE = "WIDGET_SERVICE_UPDATE_STATE"

        const val SEC = 1000
        const val MIN = 60*SEC
        const val HOUR = 60*MIN

        var TIME_PAUSE_SERVICE_LENGTH_ARRAY: IntArray = intArrayOf(5*MIN, 15*MIN, 30*MIN, HOUR, 3*HOUR, 8*HOUR, 0)
    }
}
