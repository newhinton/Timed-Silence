package de.felixnuesse.timedsilence


/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 10.04.19 - 18:07
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

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import android.content.ComponentName
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        VolumeHandler.getVolumePermission(this)



        (findViewById(R.id.button_set_loud) as Button).setOnClickListener {
            VolumeHandler.setLoud(this)
            checkStateOfAlarm()
        }

        (findViewById(R.id.button_set_vibrate) as Button).setOnClickListener {
            VolumeHandler.setVibrate(this)
            checkStateOfAlarm()
        }

        (findViewById(R.id.button_set_silent) as Button).setOnClickListener {
            VolumeHandler.setSilent(this)
            checkStateOfAlarm()
        }

        (findViewById(R.id.button_start_checking) as Button).setOnClickListener {
            //AlarmHandler.createAlarmIntime(this, 100);
            AlarmHandler.createRepeatingTimecheck(this)
            checkStateOfAlarm()
        }

        (findViewById(R.id.button_stop_checking) as Button).setOnClickListener {
            AlarmHandler.removeRepeatingTimecheck(this)
            checkStateOfAlarm()
        }


        (findViewById(R.id.button_delay_one) as Button).setOnClickListener {
            AlarmHandler.removeRepeatingTimecheck(this)
            checkStateOfAlarm()
            //AlarmHandler.createAlarmIntime(this, 1 * 60 * 60 * 1000)
            AlarmHandler.createAlarmIntime(this, 5000)
        }

        (findViewById(R.id.button_delay_three) as Button).setOnClickListener {
            AlarmHandler.removeRepeatingTimecheck(this)
            //AlarmHandler.createAlarmIntime(this, 3 * 60 * 60 * 1000)
            checkStateOfAlarm()
        }

        (findViewById(R.id.button_delay_eight) as Button).setOnClickListener {
            AlarmHandler.removeRepeatingTimecheck(this)
            //AlarmHandler.createAlarmIntime(this, 8 * 60 * 60 * 1000)
            checkStateOfAlarm()
        }


        WifiManager.requestPermissions(this)
        WifiManager.getCurrentSsid(this)
        checkStateOfAlarm()


        val seekBarSupportText= findViewById<TextView>(R.id.textview_waittime_content)
        val seekBar = findViewById<SeekBar>(R.id.seekBar_waittime)

        val interval= SharedPreferencesHandler.getPref(this, Constants.PREF_INTERVAL_CHECK, Constants.PREF_INTERVAL_CHECK_DEFAULT)

        seekBarSupportText.text=interval.toString()
        seekBar.progress=interval

        //minimum is zero, so we need to offset by one
        seekBar.max=179
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Write code to perform some action when progress is changed.
                seekBarSupportText.text= (seekBar.progress+1).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is started.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
             //Toast.makeText(this@MainActivity, "Progress is " + seekBar.progress+1 + "%", Toast.LENGTH_SHORT).show()
                seekBarSupportText.text= (seekBar.progress+1).toString()
                setInterval(seekBar.progress+1)

            }
        })


        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()


        }


    }

    override fun onResume() {
        super.onResume()
        checkStateOfAlarm()
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



    fun setInterval(interval: Int){

        SharedPreferencesHandler.setPref(this, Constants.PREF_INTERVAL_CHECK, interval)
        AlarmHandler.removeRepeatingTimecheck(this)
        AlarmHandler.createRepeatingTimecheck(this)

    }

    fun checkStateOfAlarm(){
        val status= findViewById<ImageView>(R.id.statusCircle) as ImageView
        status.setImageDrawable(getDrawable(R.drawable.circle_red))
        if(AlarmHandler.checkIfNextAlarmExists(this)){
            status.setImageDrawable(getDrawable(R.drawable.circle_green))
        }
    }
}
