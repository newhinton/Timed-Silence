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
        const val APP_NAME = "foo"
        const val BROADCAST_INTENT_ACTION = "action"
        const val BROADCAST_INTENT_ACTION_UPDATE_VOLUME = "volumeUpdate"
        const val BROADCAST_INTENT_ACTION_DELAY = "delayVolumeUpdate"
        const val BROADCAST_INTENT_ACTION_DELAY_EXTRA = "delayVolumeUpdate_EXTRA"
        const val BROADCAST_INTENT_ACTION_DELAY_RESTART_NOW = "restartDelayNow"


        const val DEFAULT_DELAY = 60
    }
}
