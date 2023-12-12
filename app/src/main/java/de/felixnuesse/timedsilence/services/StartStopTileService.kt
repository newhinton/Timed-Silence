package de.felixnuesse.timedsilence.services


import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.handler.trigger.Trigger

/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 21.04.19 - 12:37
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

class StartStopTileService: TileService() {

    companion object {
        private const val TAG = "StartStopTileService"
        var icon = R.drawable.icon_av_timer
    }

    override fun onClick() {
        super.onClick()
        Log.e(TAG,"StartStopTileService: onClick")

        val t = Trigger(this)

        if(t.checkIfNextAlarmExists()){
            t.removeTimecheck()
        } else {
            t.createAlarmIntime()
            //VolumeCalculator(this).calculateAllAndApply()
        }
        updateTile()
    }

    override fun onTileRemoved() {
        super.onTileRemoved()

        // Do something when the user removes the Tile
    }

    override fun onTileAdded() {
        super.onTileAdded()
        updateTile()
    }

    override fun onStartListening() {
        super.onStartListening()
        Log.e(TAG,"StartStopTileService: onStartListening")
        updateTile()

        // Called when the Tile becomes visible
    }

    override fun onStopListening() {
        super.onStopListening()
        Log.e(TAG,"StartStopTileService: onStopListening")

        // Called when the tile is no longer visible
    }

    fun updateTile(){
        val tile = qsTile

        if(Trigger(applicationContext).checkIfNextAlarmExists()){
            tile.state = Tile.STATE_ACTIVE
            tile.label = applicationContext.getString(R.string.timecheck_running)
        } else {
            tile.state = Tile.STATE_INACTIVE
            tile.label = applicationContext.getString(R.string.timecheck_stopped)
        }

        tile.icon = Icon.createWithResource(this, icon)
        tile.updateTile()

    }
}