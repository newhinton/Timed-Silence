package de.felixnuesse.timedsilence.handler.permissions

import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.R

class DoNotDisturb {

    companion object {

        fun hasAccess(context: Context, request: Boolean = false): Boolean {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val access = notificationManager.isNotificationPolicyAccessGranted
            if (!access && request) {
                Log.d(Constants.APP_NAME, "DoNotDisturb: Ask for DND-Access")
                requestAccess(context)
                return access

            }
            return access
        }

        fun requestAccess(context: Context) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(R.string.GrantDNDPermissionAccess)
                .setPositiveButton(R.string.GrantDND) { _, _ ->
                    directRequest(context)
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

        fun directRequest(context: Context) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            context.startActivity(intent)
        }


    }
}