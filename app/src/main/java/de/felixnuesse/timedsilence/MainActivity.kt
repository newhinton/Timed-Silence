package de.felixnuesse.timedsilence

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime
import android.content.ComponentName
import android.media.AudioManager
import android.os.Build
import android.support.v4.content.ContextCompat.getSystemService
import android.app.NotificationManager
import android.widget.Button


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        volume()



        // get reference to button
        val btn_click_me = findViewById(R.id.button) as Button
        // set on-click listener
        btn_click_me.setOnClickListener {
           setSilent()
        }

        // get reference to button
        val btn1_click_me = findViewById(R.id.button2) as Button
        // set on-click listener
        btn1_click_me.setOnClickListener {
            setLoud()
        }

        // get reference to button
        val btn2_click_me = findViewById(R.id.button3) as Button
        // set on-click listener
        btn2_click_me.setOnClickListener {
            setVibrate()
        }


        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()


        }

        val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val broadcastIntent = Intent(this, AlarmBroadcastReceiver::class.java)

        // The Pending Intent to pass in AlarmManager
        val pIntent = PendingIntent.getBroadcast(this,0,broadcastIntent,0)


        val current = LocalDateTime.now()
        Log.e(Constants.APP_NAME, "Current Date and Time is: $current")



        // Set an alarm to trigger 5 second after this code is called

       /* alarmMgr.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 5000,
            1000 * 5,
            pIntent
        )*/


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> callThirdparty()
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun callThirdparty(): Boolean {
        val intentDeviceTest = Intent("android.intent.action.MAIN")
        intentDeviceTest.component = ComponentName("felixnuesse.de.uniVV_webbrowser", "LogoActivity")

        //startActivity(intentDeviceTest)
        startActivity( getPackageManager().getLaunchIntentForPackage("felixnuesse.de.uniVV_webbrowser"))
        return true
    }

    fun volume() {

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted) {

            val intent = Intent(
                android.provider.Settings
                    .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
            )

            startActivity(intent)
        }

    }


    fun setSilent(){

        val manager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        setStreamToPercent(manager, AudioManager.STREAM_MUSIC, 0)
        setStreamToPercent(manager, AudioManager.STREAM_ALARM, 0)
        setStreamToPercent(manager, AudioManager.STREAM_NOTIFICATION, 0)
        setStreamToPercent(manager, AudioManager.STREAM_RING, 0)
        manager.setRingerMode(AudioManager.RINGER_MODE_SILENT)

    }

    fun setLoud(){

        val manager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        manager.setStreamVolume(AudioManager.STREAM_MUSIC, manager.getStreamMaxVolume( AudioManager.STREAM_MUSIC), 0)
        manager.setStreamVolume(AudioManager.STREAM_ALARM, manager.getStreamMaxVolume( AudioManager.STREAM_ALARM), 0)
        manager.setStreamVolume(AudioManager.STREAM_RING, manager.getStreamMaxVolume( AudioManager.STREAM_RING), 0)
        manager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, manager.getStreamMaxVolume( AudioManager.STREAM_NOTIFICATION), 0)
        manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL)

    }

    fun setVibrate(){

        val manager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL)
        setStreamToPercent(manager, AudioManager.STREAM_MUSIC, 0)
        setStreamToPercent(manager, AudioManager.STREAM_ALARM, 0)
        setStreamToPercent(manager, AudioManager.STREAM_NOTIFICATION, 0)
        setStreamToPercent(manager, AudioManager.STREAM_RING, 0)

    }

    fun setStreamToPercent(manager: AudioManager, stream: Int , percentage: Int){

        val maxVol = manager.getStreamMaxVolume(stream)
        val onePercent=maxVol/100
        val vol=onePercent*percentage
        manager.setStreamVolume(stream, vol, 0);
    }

}
