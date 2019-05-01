package de.felixnuesse.timedsilence.services

import android.content.Context
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME
import de.felixnuesse.timedsilence.R
import android.content.Intent
import android.widget.Toast
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.handler.AlarmHandler
import de.felixnuesse.timedsilence.services.`interface`.TimerInterface
import java.text.SimpleDateFormat
import java.util.*


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

        var icon = R.drawable.ic_av_timer_black_24dp
    }

    override fun timerStarted(context: Context, timeAsLong: Long, timeAsString: String) {
    }

    override fun timerReduced(context: Context, timeAsLong: Long, timeAsString: String) {
        updateTile(PauseTimerService.getTimestampInProperLength(timeAsLong), Tile.STATE_ACTIVE)
    }

    override fun timerFinished(context: Context) {
        updateTile(getString(R.string.qs_tile_label), Tile.STATE_INACTIVE)
    }

    override fun onClick() {
        super.onClick()
        Log.e(APP_NAME,"PauseTileService: onClick")

        updateTile(Tile.STATE_ACTIVE)

        PauseTimerService.startAutoTimer(this)
    }

    override fun onTileRemoved() {
        super.onTileRemoved()

        // Do something when the user removes the Tile
    }

    override fun onTileAdded() {
        super.onTileAdded()
        updateTile(getString(R.string.qs_tile_label), Tile.STATE_ACTIVE)
    }

    override fun onStartListening() {
        super.onStartListening()
        Log.e(APP_NAME,"PauseTileService: onStartListening")
        PauseTimerService.registerListener(this)

        if(PauseTimerService.isTimerRunning()){
            updateTile(getString(R.string.qs_tile_label), Tile.STATE_ACTIVE)
        }else{
            updateTile(getString(R.string.qs_tile_label), Tile.STATE_INACTIVE)
        }

        updateTile(getString(R.string.qs_tile_label))

        // Called when the Tile becomes visible
    }

    override fun onStopListening() {
        super.onStopListening()
        Log.e(APP_NAME,"PauseTileService: onStopListening")

        // Called when the tile is no longer visible
    }


    fun updateTile(state: Int){
        updateTile(qsTile.label.toString(), state)
    }

    fun updateTile(label: String){
        updateTile(label, qsTile.state)
    }

    fun updateTile(label: String, state: Int){
        val tile = qsTile
        tile.icon = Icon.createWithResource(this, icon)
        tile.label = label
        tile.contentDescription = PauseTileService.state_unused
        tile.state = state

        tile.updateTile()
    }

}