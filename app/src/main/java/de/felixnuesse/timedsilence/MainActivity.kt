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

import android.content.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import de.felixnuesse.timedsilence.fragments.WifiConnectedFragment
import de.felixnuesse.timedsilence.fragments.WifiSearchingFragment
import de.felixnuesse.timedsilence.fragments.CalendarEventFragment
import de.felixnuesse.timedsilence.fragments.TimeFragment
import android.content.res.ColorStateList
import android.media.AudioManager
import android.support.design.widget.FloatingActionButton
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME
import de.felixnuesse.timedsilence.handler.AlarmHandler
import de.felixnuesse.timedsilence.handler.SharedPreferencesHandler
import de.felixnuesse.timedsilence.handler.VolumeHandler
import de.felixnuesse.timedsilence.receiver.NoisyBroadcastReciever


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        VolumeHandler.getVolumePermission(this)




        //This hidden button is needed because the buttonsound of the main button is supressed because the device is still muted. A click is performed on this button, and when the onClick handler is set,
        //it plays a sound after the volume has changed to loud. Therefore it seems to be the main button who makes the sound
        (findViewById(R.id.button_buttonsound_fix) as Button).isSoundEffectsEnabled=true
        (findViewById(R.id.button_buttonsound_fix) as Button).setOnClickListener {
            Log.e(APP_NAME,"MainAcitivity: HiddenButton: PerformClick to make sound")
        }

        (findViewById(R.id.button_set_loud) as Button).isSoundEffectsEnabled=false
        (findViewById(R.id.button_set_loud) as Button).setOnClickListener {
            VolumeHandler.setLoud(this)
            val b = (findViewById(R.id.button_buttonsound_fix) as Button)
            b.performClick()
        }

        (findViewById(R.id.button_set_vibrate) as Button).isSoundEffectsEnabled=false
        (findViewById(R.id.button_set_vibrate) as Button).setOnClickListener {
            VolumeHandler.setVibrate(this)
        }

        (findViewById(R.id.button_set_silent) as Button).isSoundEffectsEnabled=false
        (findViewById(R.id.button_set_silent) as Button).setOnClickListener {
            VolumeHandler.setSilent(this)
        }


        (findViewById(R.id.button_delay_one) as Button).setOnClickListener {
            AlarmHandler.removeRepeatingTimecheck(this)
            //AlarmHandler.createAlarmIntime(this, 1 * 60 * 60 * 1000)
            AlarmHandler.createAlarmIntime(this, 5000)
        }

        (findViewById(R.id.button_delay_three) as Button).setOnClickListener {
            AlarmHandler.removeRepeatingTimecheck(this)
            //AlarmHandler.createAlarmIntime(this, 3 * 60 * 60 * 1000)
        }

        (findViewById(R.id.button_delay_eight) as Button).setOnClickListener {
            AlarmHandler.removeRepeatingTimecheck(this)
            //AlarmHandler.createAlarmIntime(this, 8 * 60 * 60 * 1000)
        }

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

        val fabTextView = findViewById<TextView>(R.id.fab_textview)
        fab.setOnClickListener { view ->
            //get current state
            checkStateOfAlarm()

            if(fabTextView.text == getString(R.string.timecheck_start)){
                AlarmHandler.createRepeatingTimecheck(this)
                setFabStarted(fab, fabTextView)
                SharedPreferencesHandler.setPref(this, Constants.PREF_BOOT_RESTART,true)
            }else{
                AlarmHandler.removeRepeatingTimecheck(this)
                setFabStopped(fab, fabTextView)
                SharedPreferencesHandler.setPref(this, Constants.PREF_BOOT_RESTART,false)
            }



        }


        val tabs = findViewById<TabLayout>(R.id.tabLayout)
        val mPager = findViewById<ViewPager>(R.id.viewPager)

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                mPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        mPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {
                val tab = tabs.getTabAt(position)
                tab?.select()
            }

        })


        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        mPager.adapter = pagerAdapter


        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            Log.e(Constants.APP_NAME, "Main: SharedPrefs: prefs: "+prefs)
            Log.e(Constants.APP_NAME, "Main: SharedPrefs: key:   "+key)


            if(key=="last_ExecTime"){
                updateTimeCheckDisplay()
            }
        }


        val sharedPref = this?.getSharedPreferences("test", Context.MODE_PRIVATE)
        sharedPref.registerOnSharedPreferenceChangeListener(listener)


    }

    fun setFabStarted(fab: FloatingActionButton, text: TextView){
        text.text = getString(R.string.timecheck_stop)
        fab.backgroundTintList = ColorStateList.valueOf(getColor(R.color.colorFab_started))

    }

    fun setFabStopped(fab: FloatingActionButton, text: TextView){
        text.text = getString(R.string.timecheck_start)
        fab.backgroundTintList = ColorStateList.valueOf(getColor(R.color.colorFab_stopped))
        AlarmHandler.removeRepeatingTimecheck(this)
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

        when (item.itemId) {
            R.id.action_settings -> callThirdparty()
            R.id.action_set_manual_loud -> VolumeHandler.setLoud(applicationContext)
            R.id.action_set_manual_vibrate -> VolumeHandler.setVibrate(applicationContext)
            R.id.action_set_manual_silent -> VolumeHandler.setSilent(applicationContext)
        }
        return true;
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
        val fabTextView = findViewById<TextView>(R.id.fab_textview)

        if(AlarmHandler.checkIfNextAlarmExists(this)){
            setFabStarted(fab, fabTextView)
        }else{
            setFabStopped(fab, fabTextView)
        }

        updateTimeCheckDisplay()
    }

    fun updateTimeCheckDisplay(){
        val nextCheckDisplayTextView= findViewById<TextView>(R.id.nextCheckDisplay)
        //nextCheckDisplayTextView.text=AlarmHandler.getNextAlarmTimestamp(this)

        val sharedPref = this?.getSharedPreferences("test", Context.MODE_PRIVATE) ?: return
        val highScore = sharedPref.getString("last_ExecTime", "notset")

        nextCheckDisplayTextView.text=highScore
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = 4

        override fun getItem(position: Int): Fragment {

            when (position) {
                0 -> return TimeFragment()
                1 -> return WifiConnectedFragment()
                2 -> return WifiSearchingFragment()
                3 -> return CalendarEventFragment()
                else -> return TimeFragment()
            }
        }
    }


}
