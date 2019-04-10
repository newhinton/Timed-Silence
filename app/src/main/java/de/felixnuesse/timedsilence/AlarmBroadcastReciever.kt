package de.felixnuesse.timedsilence


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.time.LocalDateTime

class AlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {


        Log.e(Constants.APP_NAME, "test!")
        val current = LocalDateTime.now()
        Log.e(Constants.APP_NAME, "Current Date and Time is: $current")

        if (intent?.getStringExtra(Constants.BROADCAST_INTENT_ACTION).equals(Constants.BROADCAST_INTENT_ACTION_UPDATE_VOLUME)){
            switchVolumeMode(context)
        }


    }

    fun switchVolumeMode(context: Context?){

        val hour= LocalDateTime.now().hour
        val min= LocalDateTime.now().minute


        Log.e(Constants.APP_NAME, "Hour $hour")
        Log.e(Constants.APP_NAME, "Min  $min")

        if(hour in 0..8 || hour in 22..24){
            val copy = context
            if (copy != null) {
                // copy is guaranteed to be to non-nullable whatever you do
                VolumeHandler.setSilent(copy)
                Log.e(Constants.APP_NAME, "set silent!")
            }
        }

        if(hour in 8..16){
            val copy = context
            if (copy != null) {
                VolumeHandler.setVibrate(copy)
                Log.e(Constants.APP_NAME, "set vibrate!")
            }
        }

        if(hour in 16..22){
            val copy = context
            if (copy != null) {
                VolumeHandler.setLoud(copy)
                Log.e(Constants.APP_NAME, "set loud!")
            }
        }


    }

    fun Any?.notNull(f: ()-> Unit){
        if (this != null){
            f()
        }
    }
}