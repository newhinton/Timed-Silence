package de.felixnuesse.timedsilence.services

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME
import de.felixnuesse.timedsilence.R
import android.content.Intent
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.services.`interface`.TimerInterface


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

class PauseTileService: TileService(), TimerInterface {


    companion object {
        const val state_unused= "1"
    }

    override fun timerReduced(timeAsLong: Long) {
        val tile = qsTile
        tile.label = "t: "+timeAsLong.toString()
        tile.updateTile()
    }

    var icon = R.drawable.ic_av_timer_black_24dp

    override fun onClick() {
        super.onClick()
        Log.e(APP_NAME,"PauseTileService: onClick")
        val tile = qsTile
        tile.icon = Icon.createWithResource(this, icon)
        tile.label = "hmmm"
        tile.contentDescription = "help?"
        tile.state = Tile.STATE_ACTIVE

        tile.updateTile()



        val i =Intent(this, PauseTimerService::class.java)
        i.putExtra(Constants.SERVICE_INTENT_DELAY_ACTION,Constants.SERVICE_INTENT_DELAY_ACTION)
        Log.e(APP_NAME,"PauseTileService: service started")
        startService(i)
        // Called when the user click the tile
    }

    override fun onTileRemoved() {
        super.onTileRemoved()

        // Do something when the user removes the Tile
    }

    override fun onTileAdded() {
        super.onTileAdded()

        val tile = qsTile
        tile.icon = Icon.createWithResource(this, icon)
        tile.label = getString(R.string.qs_tile_label)
        tile.contentDescription = PauseTileService.state_unused
        tile.state = Tile.STATE_ACTIVE

        tile.updateTile()

        // Do something when the user add the Tile
    }

    override fun onStartListening() {
        super.onStartListening()
        Log.e(APP_NAME,"PauseTileService: onStartListening")
        PauseTimerService.registerListener(this)
        // Called when the Tile becomes visible
    }

    override fun onStopListening() {
        super.onStopListening()
        Log.e(APP_NAME,"PauseTileService: onStopListening")

        // Called when the tile is no longer visible
    }
}