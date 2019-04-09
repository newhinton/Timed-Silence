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
import android.widget.Button


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        VolumeHandler.getVolumePermission(this)



        // get reference to button
        val btn_click_me = findViewById(R.id.button) as Button
        // set on-click listener
        btn_click_me.setOnClickListener {
            VolumeHandler.setSilent(this)
        }

        // get reference to button
        val btn1_click_me = findViewById(R.id.button2) as Button
        // set on-click listener
        btn1_click_me.setOnClickListener {
            VolumeHandler.setLoud(this)
        }

        // get reference to button
        val btn2_click_me = findViewById(R.id.button3) as Button
        // set on-click listener
        btn2_click_me.setOnClickListener {
            VolumeHandler.setVibrate(this)
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

        alarmMgr.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 100,
            1000 * 60 * 15,
            pIntent
        )


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
}
