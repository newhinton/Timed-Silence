package de.felixnuesse.timedintenttrigger.database.xml

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import androidx.core.app.ActivityCompat
import android.util.Log
import android.widget.Toast
import de.felixnuesse.timedsilence.PrefConstants.Companion.PREF_BOOT_RESTART
import de.felixnuesse.timedsilence.PrefConstants.Companion.PREF_IGNORE_CHECK_WHEN_HEADSET
import de.felixnuesse.timedsilence.PrefConstants.Companion.PREF_INTERVAL_CHECK
import de.felixnuesse.timedsilence.PrefConstants.Companion.PREF_PAUSE_NOTIFICATION
import de.felixnuesse.timedsilence.PrefConstants.Companion.PREF_TRIGGERTYPE
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.util.DateUtil
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
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
import android.content.SharedPreferences

import android.content.Context.MODE_PRIVATE
import de.felixnuesse.timedsilence.PrefConstants.Companion.PREFS_NAME


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

class Exporter {

    /*
    Important! set;

    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

    Implement this in the calling activity

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Exporter.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }

     */

    companion object{

        private const val TAG = "Exporter"
        private const val PERMISSION_WRITE_EXTERNAL = 443

        fun export(a: Activity) {
            try {
                Log.e(TAG, "Export!")
                val `val` = create(a)
                storeFile(a, `val`)
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
                    PERMISSION_WRITE_EXTERNAL -> export(activity)
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

            val currentDateandTime = DateUtil.getDate(Date().time, "yyyyMMdd-HHmmss")
            val filename = "${a.getString(R.string.app_name)}_${currentDateandTime}.xml"

            val path = File(Environment.getExternalStorageDirectory().absolutePath + "/${a.getString(R.string.app_name)}")
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

        private fun create(a: Activity): String {

            val documentBuilderFactory = DocumentBuilderFactory.newInstance()
            val documentBuilder = documentBuilderFactory.newDocumentBuilder()
            val document = documentBuilder.newDocument()

            val dbHandler = DatabaseHandler(a)

            val rootElement = document.createElement("TimedSilence")
            document.appendChild(rootElement)

            //schedules does not work, the parser then thinks it is also an schedule
            val scheduleElement = document.createElement("_schedules")
            rootElement.appendChild(scheduleElement)

            for (scheduleObject in dbHandler.getAllSchedules()) {
                val em = document.createElement("schedule")
                em.appendChild(createChild(document, "name", scheduleObject.name))
                em.appendChild(createChild(document, "time_start", scheduleObject.time_start.toString()))
                em.appendChild(createChild(document, "time_end", scheduleObject.time_end.toString()))
                em.appendChild(createChild(document, "time_setting", scheduleObject.time_setting.toString()))
                em.appendChild(createChild(document, "mon", scheduleObject.mon.toString()))
                em.appendChild(createChild(document, "tue", scheduleObject.tue.toString()))
                em.appendChild(createChild(document, "wed", scheduleObject.wed.toString()))
                em.appendChild(createChild(document, "thu", scheduleObject.thu.toString()))
                em.appendChild(createChild(document, "fri", scheduleObject.fri.toString()))
                em.appendChild(createChild(document, "sat", scheduleObject.sat.toString()))
                em.appendChild(createChild(document, "sun", scheduleObject.sun.toString()))

                scheduleElement.appendChild(em)
            }

            //calendars does not work, the parser then thinks it is also an calendar
            val calendarElement = document.createElement("_calendars")
            rootElement.appendChild(calendarElement)

            for (calendarEntry in dbHandler.getAllCalendarEntries()) {
                val em = document.createElement("calendar")
                em.appendChild(createChild(document, "name", calendarEntry.name))
                em.appendChild(createChild(document, "ext_id", calendarEntry.ext_id.toString()))
                em.appendChild(createChild(document, "volume", calendarEntry.volume.toString()))
                em.appendChild(createChild(document, "color", calendarEntry.color.toString()))
                calendarElement.appendChild(em)
            }

            //wifielements does not work, the parser then thinks it is also an _wifielement
            val wifiElement = document.createElement("_wifielements")
            rootElement.appendChild(wifiElement)

            for (wifiObject in dbHandler.getAllWifiEntries()) {
                val em = document.createElement("wifi")
                em.appendChild(createChild(document, "ssid", wifiObject.ssid))
                em.appendChild(createChild(document, "type", wifiObject.type.toString()))
                em.appendChild(createChild(document, "volume", wifiObject.volume.toString()))
                wifiElement.appendChild(em)
            }

            //keywords
            val keywordsElement = document.createElement("_keywords")
            rootElement.appendChild(keywordsElement)

            for (keywordObject in dbHandler.getKeywords()) {
                val em = document.createElement("keyword")
                em.appendChild(createChild(document, "key", keywordObject.keyword))
                em.appendChild(createChild(document, "calendarid", keywordObject.calendarid.toString()))
                em.appendChild(createChild(document, "volume", keywordObject.volume.toString()))
                keywordsElement.appendChild(em)
            }

            val settings = document.createElement("SETTINGS")
            rootElement.appendChild(settings)
            addPreferences(document, settings, a.applicationContext)


            val sw = StringWriter()
            val tf = TransformerFactory.newInstance()
            val transformer = tf.newTransformer()
            transformer.transform(DOMSource(document), StreamResult(sw))

            return sw.toString()
        }

        private fun createChild(document: Document, name: String, content: String): Element {
            val child = document.createElement(name)
            child.textContent = content
            return child
        }

        private fun addPreferences(doc: Document, element: Element, context: Context){
            var preferencesBoolean = arrayListOf(PREF_BOOT_RESTART, PREF_IGNORE_CHECK_WHEN_HEADSET, PREF_PAUSE_NOTIFICATION)
            var preferencesInt = arrayListOf(
                PREF_INTERVAL_CHECK,
                context.getString(R.string.pref_volume_alarm),
                context.getString(R.string.pref_volume_ringer),
                context.getString(R.string.pref_volume_notification),
                context.getString(R.string.pref_volume_music),
                PREF_TRIGGERTYPE
            )

            val sharedPrefs: SharedPreferences = context.applicationContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            for (settings in preferencesBoolean) {
                if(sharedPrefs.contains(settings)){
                    val em = doc.createElement("PREFERENCE")
                    em.setAttribute("NAME", settings)
                    em.setAttribute("VALUE", sharedPrefs.getBoolean(settings, false).toString())
                    em.setAttribute("TYPE", "BOOLEAN")
                    element.appendChild(em)
                }
            }
            for (settings in preferencesInt) {
                if(sharedPrefs.contains(settings)){
                    val em = doc.createElement("PREFERENCE")
                    em.setAttribute("NAME", settings)
                    em.setAttribute("VALUE", sharedPrefs.getInt(settings, -1).toString())
                    em.setAttribute("TYPE", "INTEGER")
                    element.appendChild(em)
                }
            }
        }
    }



}