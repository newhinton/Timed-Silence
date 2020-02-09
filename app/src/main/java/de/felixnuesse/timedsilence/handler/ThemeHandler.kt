package de.felixnuesse.timedsilence.handler

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate
import de.felixnuesse.timedsilence.PrefConstants

/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 09.02.20 - 12:39
 *
 * Edited by: Felix Nüsse felix.nuesse(at)t-online.de
 *
 * timed-silence
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
class ThemeHandler {
    companion object{

        fun setTheme(context: Activity, window: Window, supportActionBar: ActionBar?){

            val dark = SharedPreferencesHandler.getPref(context, PrefConstants.PREF_DARKMODE, PrefConstants.PREF_DARKMODE_DEFAULT)

            if(!dark){
                supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ffffff")))
            }

            setTheme(window, dark)

        }

        fun setTheme(context: Activity, window: Window){
            setTheme(window, SharedPreferencesHandler.getPref(context, PrefConstants.PREF_DARKMODE, PrefConstants.PREF_DARKMODE_DEFAULT))
        }

        fun setTheme(window: Window, dark: Boolean){
            if(dark){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                var flags = window.decorView.systemUiVisibility
                flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.decorView.systemUiVisibility=flags
            }
        }
    }
}
