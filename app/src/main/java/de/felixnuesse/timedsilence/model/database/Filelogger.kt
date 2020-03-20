package de.felixnuesse.timedsilence.model.database

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import de.felixnuesse.timedintenttrigger.database.xml.Importer
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.Utils
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.*
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * Copyright (C) 2020  Felix Nüsse
 * Created on 12.02.20 - 14:29
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
class Filelogger {

    /*
 Important! set;

 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

 Implement this in the calling activity

 override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
     Exporter.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
 }

  */

    companion object{

        private const val PERMISSION_WRITE_EXTERNAL = 443

        fun log(a: Activity, content: String) {
            try {
                Log.e(Constants.APP_NAME, "Filelogger: ")
                storeFile(a, content)
            } catch (e: ParserConfigurationException) {
                e.printStackTrace()
            } catch (e: TransformerException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        /**
         * This also handles the importer request!
         */
        fun onRequestPermissionsResult(activity: Activity, requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                when (requestCode) {
                    Importer.PERM_REQUEST_CODE -> Importer.importFile(activity)
                //    PERMISSION_WRITE_EXTERNAL -> export(activity)
                }
            }
        }

        private fun isWriteStoragePermissionGranted(a: Activity): Boolean {
            if (a.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("perm", "Permission is granted")
                return true
            } else {
                Log.v("perm", "Permission is revoked")
                // <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" /> needs to be defined, otherwise this will always be denied

                ActivityCompat.requestPermissions(
                    a,
                    arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_WRITE_EXTERNAL
                )
                return false
            }
        }

        private fun storeFile(a: Activity, content: String) {

            if (!isWriteStoragePermissionGranted(a)) {
                return
            }

            val currentDateandTime = Utils.getDate(Date().time)

            val filename = "${Constants.APP_NAME}_$currentDateandTime.xml"

            val path = File(Environment.getExternalStorageDirectory().absolutePath + "/${Constants.APP_NAME}")
            path.mkdirs()
            val file = File(path, filename)
            val stream = FileOutputStream(file)
            val myOutWriter = OutputStreamWriter(stream)

            try {
                myOutWriter.append(content)
                val text = a.getString(R.string.export_file_success) + file.absolutePath
                Toast.makeText(a, text, Toast.LENGTH_LONG).show()
            } finally {
                myOutWriter.close()
                stream.close()
            }

        }

    }
}