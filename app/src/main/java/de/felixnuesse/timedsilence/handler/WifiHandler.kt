package de.felixnuesse.timedsilence.handler

import android.Manifest
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import de.felixnuesse.timedsilence.Constants


/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 12.04.19 - 17:22
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


class WifiHandler {

    companion object {
        fun getCurrentSsid(context: Context): String? {
            var ssid: String? = null
            val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connManager.activeNetworkInfo
            if (networkInfo.isConnected) {
                Log.e(Constants.APP_NAME, "Wifimanager: networkInfo is connected")

                val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as android.net.wifi.WifiManager
                val connectionInfo = wifiManager.connectionInfo
                if (connectionInfo != null && connectionInfo.ssid != "") {
                    ssid = connectionInfo.ssid

                    Log.e(Constants.APP_NAME, "Wifimanager: non blank, non null ssid")
                }
            }

            Log.e(Constants.APP_NAME, "Wifimanager: Queried current ssid: $ssid")
            return ssid
        }

        fun requestPermissions(activity: Activity){
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(activity,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        9080);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
            }

        }
    }
}