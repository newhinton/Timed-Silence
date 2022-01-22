package de.felixnuesse.timedsilence

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.MODE_APPEND
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import de.felixnuesse.timedintenttrigger.database.xml.Exporter
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Copyright (C) 2020  Felix Nüsse
 * Created on 11.02.20 - 16:28
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

class Utils{

    companion object{

        fun getDate(milliSeconds: Long, format: String): String {
            val formatter = SimpleDateFormat(format, Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            return formatter.format(calendar.time)
        }

        fun getDate(milliSeconds: Long): String {
           return getDate(milliSeconds, "dd.MM.yyyy HH:mm:ss")
        }

        fun getDate(milliSeconds: String): String {
            return getDate(milliSeconds.toLong())
        }

        fun appendLogfile(context: Context, state: String, content: String){
            if (!isWriteStoragePermissionGranted(context)) {
                Log.e(APP_NAME, "No external write permission for logging.")
                return
            }
            val now = Date()
            val timestamp = now.time
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            val dateStr = sdf.format(timestamp)

            var text = "$dateStr - [$state]: $content\n"

            val filename = "${APP_NAME}.log"

            //val path = File(Environment.getExternalStorageDirectory().absolutePath + "/$APP_NAME")
            val path = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath)
            path.mkdirs()
            val file = File(path, filename)
            val stream = FileOutputStream(file, true)
            val myOutWriter = OutputStreamWriter(stream)

            try {
                myOutWriter.append(text)
            } finally {
                myOutWriter.close()
                stream.close()
            }
        }

        private fun isWriteStoragePermissionGranted(a: Context): Boolean {
            if (a.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("perm", "Permission is granted")
                return true
            } else {
                Log.v("perm", "Permission is revoked")
                return false
            }
        }

    }

}