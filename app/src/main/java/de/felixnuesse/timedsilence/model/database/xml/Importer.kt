package de.felixnuesse.timedintenttrigger.database.xml

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME
import de.felixnuesse.timedsilence.model.data.CalendarObject
import de.felixnuesse.timedsilence.model.data.ScheduleObject
import de.felixnuesse.timedsilence.model.data.WifiObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import org.xml.sax.InputSource
import java.io.*
import java.util.ArrayList
import javax.xml.parsers.DocumentBuilderFactory

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
class Importer {

    /*

    Implement this in the calling activity

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Importer.onActivityResult(this, requestCode, resultCode, data)
    }

     */

    companion object{
        private const val READ_SUCCESS_CODE = 42
        const val PERM_REQUEST_CODE = 41

        fun importFile(activity: Activity) {
            if(!getFileAccessPermission(activity)){
                return
            }

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/xml"
            activity.startActivityForResult(intent, READ_SUCCESS_CODE)

        }

        fun onActivityResult(a: Activity, requestCode: Int, resultCode: Int, resultData: Intent?){
            when (requestCode) {
                READ_SUCCESS_CODE -> writeResultsToDB(a, requestCode, resultCode, resultData)
            }
        }

        private fun writeResultsToDB(a: Activity, requestCode: Int, resultCode: Int, resultData: Intent?) {

            val db = DatabaseHandler(a.applicationContext)

            val result = readFileFromDisk(a, requestCode, resultCode, resultData)
            val schedulesList = getScheduleObjects(result)
            val calendarList = getCalendarObjects(result)
            val wifiList = getWifiObjects(result)


            Log.e(APP_NAME, ": $result")



            for(scheduleObject in schedulesList){
                Log.e(APP_NAME, "Create Schedule: ${scheduleObject.name}")
                db.createScheduleEntry(scheduleObject)
            }

            for(calendarObject in calendarList){
                Log.e(APP_NAME, "Create Calendar: ${calendarObject.name}")
                db.createCalendarEntry(calendarObject)
            }

            for(wifiObject in wifiList){
                Log.e(APP_NAME, "Create Wifi: ${wifiObject.ssid}")
                db.createWifiEntry(wifiObject)
            }

        }


        @Throws(IOException::class)
        private fun readFileFromDisk(a: Context, requestCode: Int, resultCode: Int, resultData: Intent?): String {

            if (requestCode == READ_SUCCESS_CODE && resultCode == Activity.RESULT_OK) {
                if (resultData != null) {
                    var uri: Uri? = resultData.data
                    val inputStream = a.contentResolver.openInputStream(uri!!)
                    val inputString = inputStream?.bufferedReader().use { it?.readText() }
                    return inputString ?: ""
                }
            }
            return ""
        }

        private fun getFileAccessPermission(activity: Activity): Boolean {
            return if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.e(APP_NAME, "Get permissions -- already granted!")
                true
            } else {
                Log.e(APP_NAME, "Get permissions!")
                ActivityCompat.requestPermissions(activity, arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE), PERM_REQUEST_CODE)
                false
            }
        }

        private fun getCalendarObjects(content: String): ArrayList<CalendarObject> {
            val result = ArrayList<CalendarObject>()

            val documentBuilderFactory = DocumentBuilderFactory.newInstance()
            val documentBuilder = documentBuilderFactory.newDocumentBuilder()
            val inputStream = InputSource(StringReader(content))
            val document = documentBuilder.parse(inputStream)

            val nList = document.getElementsByTagName("calendar")

            for (i in 0 until nList.length) {

                val children =  nList.item(i).childNodes

                val transferEObject = CalendarObject(-1,-1, Constants.TIME_SETTING_UNSET)

                for (j in 0 until children.length) {
                    val type = children.item(j)
                    when (type.nodeName) {
                        "ext_id" -> transferEObject.ext_id=type.textContent.toLong()
                        "color" -> transferEObject.color=type.textContent.toInt()
                        "volume" -> transferEObject.volume=type.textContent.toInt()
                    }

                }

                val eObject = CalendarObject(transferEObject.id,transferEObject.ext_id, transferEObject.volume)
                eObject.color=transferEObject.color
                result.add(eObject)
            }

            return result
        }

        private fun getWifiObjects(content: String): ArrayList<WifiObject> {
            val result = ArrayList<WifiObject>()

            val documentBuilderFactory = DocumentBuilderFactory.newInstance()
            val documentBuilder = documentBuilderFactory.newDocumentBuilder()
            val inputStream = InputSource(StringReader(content))
            val document = documentBuilder.parse(inputStream)

            val nList = document.getElementsByTagName("wifi")

            for (i in 0 until nList.length) {

                val children =  nList.item(i).childNodes

                val transferEObject = WifiObject(-1,"",0, Constants.TIME_SETTING_UNSET)

                for (j in 0 until children.length) {
                    val type = children.item(j)
                    when (type.nodeName) {
                        "ssid" -> transferEObject.ssid=type.textContent
                        "type" -> transferEObject.type=type.textContent.toInt()
                        "volume" -> transferEObject.volume=type.textContent.toInt()
                    }

                }

                val eObject = WifiObject(transferEObject.id,transferEObject.ssid,transferEObject.type,transferEObject.volume)
                result.add(eObject)
            }

            return result
        }

        private fun getScheduleObjects(content: String): ArrayList<ScheduleObject> {
            val result = ArrayList<ScheduleObject>()

            val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val inputStream = InputSource(StringReader(content))
            val document = documentBuilder.parse(inputStream)

            val nodeList = document.getElementsByTagName("schedule")


            for (i in 0 until nodeList.length) {

                val children =  nodeList.item(i).childNodes
                val transferIObject = ScheduleObject("",0,0, Constants.TIME_SETTING_UNSET, -1)
                var id = 0L

                for (j in 0 until children.length) {
                    val type = children.item(j)


                    when (type.nodeName) {
                        "name" -> transferIObject.name=type.textContent
                        "time_start" -> transferIObject.time_start=type.textContent.toLong()
                        "time_end" -> transferIObject.time_end=type.textContent.toLong()
                        "time_setting" -> transferIObject.time_setting=type.textContent.toInt()
                        "mon" -> transferIObject.mon=type.textContent.toBoolean()
                        "tue" -> transferIObject.tue=type.textContent.toBoolean()
                        "wed" -> transferIObject.wed=type.textContent.toBoolean()
                        "thu" -> transferIObject.thu=type.textContent.toBoolean()
                        "fri" -> transferIObject.fri=type.textContent.toBoolean()
                        "sat" -> transferIObject.sat=type.textContent.toBoolean()
                        "sun" -> transferIObject.sun=type.textContent.toBoolean()
                    }
                }

                val iObject = ScheduleObject(transferIObject.name,
                    transferIObject.time_start,
                    transferIObject.time_end,
                    transferIObject.time_setting,
                    transferIObject.id,
                    transferIObject.mon,
                    transferIObject.tue,
                    transferIObject.wed,
                    transferIObject.thu,
                    transferIObject.fri,
                    transferIObject.sat,
                    transferIObject.sun
                    )

                result.add(iObject)

            }

            return result
        }
    }
}