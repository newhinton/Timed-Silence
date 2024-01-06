package de.felixnuesse.timedsilence.util

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.R


class PermissionManager(private var mContext: Context) {

    companion object {
        fun getNotificationSettingsIntent(context: Context): Intent {
            return Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
    }

    fun hasAllRequiredPermissions(): Boolean {
        if(!grantedDoNotDisturb()) {
            return false
        }
        if(!grantedAlarms()) {
            return false
        }
        return true
    }

    fun hasAllPermissions(): Boolean {
        if(!grantedDoNotDisturb()) {
            return false
        }
        if(!grantedNotifications()) {
            return false
        }
        if(!grantedAlarms()) {
            return false
        }
        if(!grantedBatteryOptimizationExemption()) {
            return false
        }

        if(!grantedContacts()) {
            return false
        }

        return hasAllRequiredPermissions()
    }

    fun grantedDoNotDisturb(): Boolean {
        val notificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.isNotificationPolicyAccessGranted
    }

    fun grantedAlarms(): Boolean {
        val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    fun grantedBatteryOptimizationExemption(): Boolean {
        val powerManger = mContext.getSystemService(POWER_SERVICE) as PowerManager
        return powerManger.isIgnoringBatteryOptimizations(mContext.packageName)
    }

    fun grantedNotifications(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
           ActivityCompat.checkSelfPermission(mContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun grantedCalendar(): Boolean {
        return ContextCompat.checkSelfPermission(
            mContext,
            Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun grantedContacts(): Boolean {
        return ContextCompat.checkSelfPermission(
            mContext,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestDoNotDisturb() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        mContext.startActivity(intent)
    }

    fun requestAlarms() {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        intent.data = Uri.parse("package:" + mContext.packageName)
        mContext.startActivity(intent)
    }

    fun requestBatteryOptimizationException() {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.data = Uri.parse("package:" + mContext.packageName)
        mContext.startActivity(intent)
    }

    fun requestCalendarAccess() {
        val builder = AlertDialog.Builder(mContext)
        builder.setMessage(R.string.GrantCalendarPermissionAccessDescription)
            .setPositiveButton(R.string.GrantCalendarPermissionAccess) { _, _ ->
                val permissionsList = Array(1) { Manifest.permission.READ_CALENDAR }
                ActivityCompat.requestPermissions(
                    mContext as Activity,
                    permissionsList,
                    Constants.CALENDAR_PERMISSION_REQUEST_ID
                )
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                ActivityCompat.finishAffinity(mContext as Activity)
            }
        builder.create().show()
    }


    fun requestContactsAccess() {
        val builder = AlertDialog.Builder(mContext)
        builder.setMessage(R.string.GrantContactsPermissionAccessDescription)
            .setPositiveButton(R.string.GrantCalendarPermissionAccess) { _, _ ->
                val permissionsList = Array(1) { Manifest.permission.READ_CONTACTS }
                ActivityCompat.requestPermissions(
                    mContext as Activity,
                    permissionsList,
                    Constants.CONTACT_PERMISSION_REQUEST_ID
                )
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                ActivityCompat.finishAffinity(mContext as Activity)
            }
        builder.create().show()
    }
}