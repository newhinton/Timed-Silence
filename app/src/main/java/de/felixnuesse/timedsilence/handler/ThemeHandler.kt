package de.felixnuesse.timedsilence.handler

import android.app.Activity
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import de.felixnuesse.timedsilence.PrefConstants
import de.felixnuesse.timedsilence.R


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

        fun setSupportActionBarTheme(context: Activity, supportActionBar: ActionBar?){
            supportActionBar?.setBackgroundDrawable(ColorDrawable(context.getColor(R.color.colorBackground)))
        }

        fun setTabLayoutTheme(context: Activity, tabLayout: TabLayout?){
            // todo: color not set properly
            tabLayout?.setBackgroundColor(context.getColor(R.color.colorBackground))
        }

        fun setTheme(context: Activity, window: Window){
            setTheme(
                context, window, SharedPreferencesHandler.getPref(
                    context,
                    PrefConstants.PREF_DARKMODE,
                    PrefConstants.PREF_DARKMODE_DEFAULT
                )
            )
        }

        fun setTheme(context: Activity, window: Window, mode: Int){
            when(mode) {
                PrefConstants.PREF_DARKMODE_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                PrefConstants.PREF_DARKMODE_AUTO -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            setLightBar(window, context)


        }

        private fun setLightBar(window: Window, context: Activity){

            when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    var flags = window.decorView.systemUiVisibility
                    flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    window.decorView.systemUiVisibility=flags
                }
                else -> return
            }
        }
    }
}
