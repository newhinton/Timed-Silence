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
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.View
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME
import de.felixnuesse.timedsilence.activities.SettingsMainActivity
import de.felixnuesse.timedsilence.handler.AlarmHandler
import de.felixnuesse.timedsilence.handler.SharedPreferencesHandler
import de.felixnuesse.timedsilence.handler.VolumeHandler
import de.felixnuesse.timedsilence.services.PauseTileService
import de.felixnuesse.timedsilence.services.PauseTimerService
import de.felixnuesse.timedsilence.services.`interface`.TimerInterface
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity(), TimerInterface {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //This hidden button is needed because the buttonsound of the main button is supressed because the device is still muted. A click is performed on this button, and when the onClick handler is set,
        //it plays a sound after the volume has changed to loud. Therefore it seems to be the main button who makes the sound
        button_buttonsound_fix.isSoundEffectsEnabled=true
        button_buttonsound_fix.setOnClickListener {
            Log.e(APP_NAME,"MainAcitivity: HiddenButton: PerformClick to make sound")
        }

        frameLayout.setOnClickListener { view ->
            Log.e(Constants.APP_NAME, "Main: FabTester: Clicked")
            buttonState()
        }
        button_check.setOnClickListener { view ->
            Log.e(Constants.APP_NAME, "Main: ButtonStartCheck: Clicked")
            setHandlerState()
        }


        val seekBarSupportText= findViewById<TextView>(R.id.textview_waittime_content)
        val seekBar = findViewById<SeekBar>(R.id.seekBar_waittime)

        val interval= SharedPreferencesHandler.getPref(this, PrefConstants.PREF_INTERVAL_CHECK, PrefConstants.PREF_INTERVAL_CHECK_DEFAULT)

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


        val tabs = findViewById<TabLayout>(R.id.tabLayout)
        val mPager = findViewById<ViewPager>(R.id.viewPager)

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                mPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        mPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                val tab = tabs.getTabAt(position)
                tab?.select()
            }
        })


        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        mPager.adapter = pagerAdapter


        SharedPreferencesHandler.getPreferences(this)?.registerOnSharedPreferenceChangeListener(getSharedPreferencesListener())


        VolumeHandler.getVolumePermission(this)

        PauseTimerService.registerListener(this)


        buttonState()
    }

    override fun onResume() {
        super.onResume()
        buttonState()
        SharedPreferencesHandler.getPreferences(this)?.registerOnSharedPreferenceChangeListener(getSharedPreferencesListener())
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
            R.id.action_settings -> openSettings()
            R.id.action_set_manual_loud -> {
                val makeSound=!VolumeHandler.isButtonClickAudible(this)

                VolumeHandler.setLoud(applicationContext)
                if(makeSound){
                    button_buttonsound_fix.performClick()
                }
            }
            R.id.action_set_manual_vibrate -> VolumeHandler.setVibrate(applicationContext)
            R.id.action_set_manual_silent -> VolumeHandler.setSilent(applicationContext)
        }
        return true;
    }

    override fun timerReduced(timeAsLong: Long) {
        textViewPausedTimestamp.visibility= View.VISIBLE
        label_Paused_until.visibility= View.VISIBLE
        textViewPausedTimestamp.text=PauseTileService.getTimestampInProperLength(timeAsLong);
        buttonState()
    }

    override fun timerFinished() {
        textViewPausedTimestamp.visibility= View.INVISIBLE
        label_Paused_until.visibility= View.INVISIBLE
        buttonState()
    }


    fun openSettings(): Boolean {
        val intent = Intent(this, SettingsMainActivity::class.java).apply {}
        startActivity(intent)
        return true
    }



    fun setInterval(interval: Int){

        SharedPreferencesHandler.setPref(this, PrefConstants.PREF_INTERVAL_CHECK, interval)
        AlarmHandler.removeRepeatingTimecheck(this)
        AlarmHandler.createRepeatingTimecheck(this)

    }


    fun updateTimeCheckDisplay(){
        val nextCheckDisplayTextView= findViewById<TextView>(R.id.nextCheckDisplay)
        //nextCheckDisplayTextView.text=AlarmHandler.getNextAlarmTimestamp(this)

        val sharedPref = this?.getSharedPreferences("test", Context.MODE_PRIVATE) ?: return
        val highScore = sharedPref.getString("last_ExecTime", "notset")

        nextCheckDisplayTextView.text=highScore
    }



    private fun buttonState() {
        val fabTextView = findViewById<TextView>(R.id.fab_textview)

        Log.e(Constants.APP_NAME, "Main: ButtonStartCheck: State: "+button_check.text)

        if(AlarmHandler.checkIfNextAlarmExists(this)){
            setFabStarted(fab, fabTextView)
        }else if(PauseTimerService.isTimerRunning()){
            setFabPaused(fab, fabTextView)
        }else{
            setFabStopped(fab, fabTextView)
        }
        updateTimeCheckDisplay()
    }

    private fun setHandlerState() {

        Log.e(Constants.APP_NAME, "Main: setHandlerState: State: "+button_check.text)

        if(button_check.text == getString(R.string.timecheck_start)){

            AlarmHandler.createRepeatingTimecheck(this)
            SharedPreferencesHandler.setPref(this, PrefConstants.PREF_BOOT_RESTART,true)


        }else if(button_check.text == getString(R.string.timecheck_paused)){

            AlarmHandler.createRepeatingTimecheck(this)
            SharedPreferencesHandler.setPref(this, PrefConstants.PREF_BOOT_RESTART,true)
            PauseTimerService.cancelTimer()

        }else{

            AlarmHandler.removeRepeatingTimecheck(this)
            SharedPreferencesHandler.setPref(this, PrefConstants.PREF_BOOT_RESTART,false)

        }
        buttonState()
    }


    fun setFabStarted(fab: FloatingActionButton, text: TextView){
        text.text = getString(R.string.timecheck_running)
        fab.backgroundTintList = ColorStateList.valueOf(getColor(R.color.colorFab_started))
        button_check.text=getString(R.string.timecheck_stop)

    }

    fun setFabStopped(fab: FloatingActionButton, text: TextView){
        text.text = getString(R.string.timecheck_stopped)
        fab.backgroundTintList = ColorStateList.valueOf(getColor(R.color.colorFab_stopped))
        AlarmHandler.removeRepeatingTimecheck(this)
        button_check.text=getString(R.string.timecheck_start)
    }

    fun setFabPaused(fab: FloatingActionButton, text: TextView){
        text.text = getString(R.string.timecheck_paused)
        fab.backgroundTintList = ColorStateList.valueOf(getColor(R.color.colorFab_paused))
        button_check.text=getString(R.string.timecheck_paused)
    }

    @Deprecated("replace by callback")
    fun getSharedPreferencesListener(): SharedPreferences.OnSharedPreferenceChangeListener {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            Log.e(Constants.APP_NAME, "Main: SharedPrefs: prefs: "+prefs)
            Log.e(Constants.APP_NAME, "Main: SharedPrefs: key:   "+key)


            if(key==PrefConstants.PREFS_LAST_KEY_EXEC){
                updateTimeCheckDisplay()
            }
        }
        return listener
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
