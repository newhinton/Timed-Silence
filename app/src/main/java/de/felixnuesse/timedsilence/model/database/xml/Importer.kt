package de.felixnuesse.timedintenttrigger.database.xml

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.handler.PreferencesManager
import de.felixnuesse.timedsilence.model.database.AppDataStructure
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import java.io.*

/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 29.12.19 - 18:42
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
class Importer(private var mActivity: Activity) {


    companion object{
        private const val TAG = "Importer"
        private const val OPEN_FILE_REQUEST = 4443
    }


    fun import() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "application/json"
        mActivity.startActivityForResult(intent, OPEN_FILE_REQUEST)

    }
    fun onRequestPermissionsResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == OPEN_FILE_REQUEST) {
            if (data != null) {
                writeResultsToDB(data)
            }
        }
    }

    private fun writeResultsToDB(resultData: Intent) {

        val db = DatabaseHandler(mActivity.applicationContext)
        val result = readFileFromDisk(resultData)
        var data = AppDataStructure.fromJSON(result)

        db.clean()

        data.schedules.forEach { db.createScheduleEntry(it) }
        data.calendars.forEach { db.createCalendarEntry(it) }
        data.wifi.forEach { db.createWifiEntry(it) }
        data.keywords.forEach { db.createKeyword(it) }

        PreferencesManager(mActivity.applicationContext).applyPreferenceHolder(data.getPreferences())

        val text = mActivity.getString(R.string.import_file_success)
        Toast.makeText(mActivity, text, Toast.LENGTH_LONG).show()

    }


    @Throws(IOException::class)
    private fun readFileFromDisk(data: Intent): String {
        var uri: Uri? = data.data
        val inputStream = mActivity.contentResolver.openInputStream(uri!!)
        val inputString = inputStream?.bufferedReader().use { it?.readText() }
        inputStream?.close()
        return inputString ?: ""
    }

}