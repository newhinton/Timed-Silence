package de.felixnuesse.timedintenttrigger.database.xml

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.util.Log
import android.widget.Toast
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.handler.PreferencesManager
import de.felixnuesse.timedsilence.model.database.AppDataStructure
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.util.DateUtil
import java.io.*
import java.util.*


/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 29.12.19 - 17:49
 *
 * Edited by: Felix Nüsse felix.nuesse(at)t-online.de
 *
 * TimedIntentTrigger
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

class Exporter(private var mActivity: Activity) {

    companion object {
        private const val TAG = "Exporter"
        private const val OPEN_DIRECTORY_REQUEST = 443

    }

    private val mDateString = DateUtil.getDate(Date().time, "yyyy.MM.dd")

    fun export() {
        Log.e(TAG, "Export!")

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/json"
        intent.putExtra(Intent.EXTRA_TITLE, "${mActivity.getString(R.string.app_name)}_${mDateString}.json")

        mActivity.startActivityForResult(intent, OPEN_DIRECTORY_REQUEST, null)
    }

    fun onRequestPermissionsResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == OPEN_DIRECTORY_REQUEST) {
            if (data != null) {
                data.data?.let { treeUri ->

                    // treeUri is the Uri of the file

                    // if life long access is required the takePersistableUriPermission() is used
                    try {
                        val outputStream = mActivity.contentResolver.openOutputStream(treeUri)
                        val content = create()
                        Log.e(TAG, content)
                        outputStream?.write(content.toByteArray())
                        outputStream?.close()
                        Toast.makeText(mActivity, "Write file successfully", Toast.LENGTH_SHORT).show()
                    } catch (e: IOException) {
                        Toast.makeText(mActivity, "Fail to write file", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }

    private fun create(): String {
        val dbHandler = DatabaseHandler(mActivity)
        val data = AppDataStructure("")

        dbHandler.getAllSchedules().forEach { data.addSchedule(it) }
        dbHandler.getAllCalendarEntries().forEach { data.addCalendar(it) }
        dbHandler.getKeywords().forEach { data.addKeyword(it) }
        dbHandler.getAllWifiEntries().forEach { data.addWifi(it) }

        var preferences = PreferencesManager(mActivity.applicationContext)
        data.setPreferences(preferences.getPreferenceHolder())

        return data.asJSON().toString(4)
    }
}