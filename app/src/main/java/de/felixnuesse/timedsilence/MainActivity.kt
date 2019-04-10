package de.felixnuesse.timedsilence

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import android.content.ComponentName
import android.widget.Button
import kotlinx.android.synthetic.main.content_main.view.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        VolumeHandler.getVolumePermission(this)



        (findViewById(R.id.button_set_loud) as Button).setOnClickListener {
            VolumeHandler.setLoud(this)
        }

        (findViewById(R.id.button_set_vibrate) as Button).setOnClickListener {
            VolumeHandler.setVibrate(this)
        }

        (findViewById(R.id.button_set_silent) as Button).setOnClickListener {
            VolumeHandler.setSilent(this)
        }

        (findViewById(R.id.button_start_checking) as Button).setOnClickListener {
            AlarmHandler.createRepeatingTimecheck(this, 1)
        }

        (findViewById(R.id.button_stop_checking) as Button).setOnClickListener {
            AlarmHandler.removeRepeatingTimecheck(this)
        }


        (findViewById(R.id.button_delay_one) as Button).setOnClickListener {
            AlarmHandler.removeRepeatingTimecheck(this)
            AlarmHandler.reenableInGivenTime(this)
        }

        (findViewById(R.id.button_delay_three) as Button).setOnClickListener {
            AlarmHandler.removeRepeatingTimecheck(this)
        }

        (findViewById(R.id.button_delay_eight) as Button).setOnClickListener {
            AlarmHandler.removeRepeatingTimecheck(this)
        }



        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()


        }


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
