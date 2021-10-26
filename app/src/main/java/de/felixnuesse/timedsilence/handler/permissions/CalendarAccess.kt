package de.felixnuesse.timedsilence.handler.permissions

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.R

class CalendarAccess {

    companion object {
        fun hasCalendarReadPermission(context: Context, request: Boolean = false): Boolean {
            val permissions = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CALENDAR
            ) == PackageManager.PERMISSION_GRANTED
            if (!permissions && request) {
                Log.d(Constants.APP_NAME, "DoNotDisturb: Ask for DND-Access")
                requestCalendarReadPermission(context)

            }
            return permissions

        }

        private fun requestCalendarReadPermission(context: Context) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(R.string.GrantCalendarPermissionAccessDescription)
                .setPositiveButton(R.string.GrantCalendarPermissionAccess) { _, _ ->
                    val permissionsList = Array(1) { Manifest.permission.READ_CALENDAR }
                    ActivityCompat.requestPermissions(
                        context as Activity,
                        permissionsList,
                        Constants.CALENDAR_PERMISSION_REQUEST_ID
                    )
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                    Log.e(
                        Constants.APP_NAME,
                        "DoNotDisturb: Did not get 'Do not Disturb'-Access, quitting..."
                    )
                    ActivityCompat.finishAffinity(context as Activity)
                }
            builder.create().show()

        }
    }
}