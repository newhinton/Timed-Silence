package de.felixnuesse.timedsilence

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log
import java.time.LocalDateTime

class AlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {


        Log.e(Constants.APP_NAME, "test!")
        val current = LocalDateTime.now()
        Log.e(Constants.APP_NAME, "Current Date and Time is: $current")


        val hour= LocalDateTime.now().hour
        val min= LocalDateTime.now().minute


        Log.e(Constants.APP_NAME, "Hour $hour")
        Log.e(Constants.APP_NAME, "Min  $min")

        if(hour in 0..7 || hour in 22..24){
            val copy = context
            if (copy != null) {
                // copy is guaranteed to be to non-nullable whatever you do
                VolumeHandler.setSilent(copy)
            }
        }

        if(hour in 7..16){
            val copy = context
            if (copy != null) {
                VolumeHandler.setVibrate(copy)
            }
        }

        if(hour in 16..22){
            val copy = context
            if (copy != null) {
                VolumeHandler.setLoud(copy)
            }
        }


        val copy = context
        if (copy != null) {

            val name = "test"
            val descriptionText = "desc"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel("0010", name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = copy.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)


            var builder = NotificationCompat.Builder(copy,"0010")
                .setSmallIcon(R.drawable.navigation_empty_icon)
                .setContentTitle("test")
                .setContentText("test1")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)


        }




    }

    fun Any?.notNull(f: ()-> Unit){
        if (this != null){
            f()
        }
    }
}