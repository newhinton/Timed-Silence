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
import android.widget.SeekBar
import android.widget.TextView
import de.felixnuesse.timedsilence.fragments.WifiConnectedFragment
import de.felixnuesse.timedsilence.fragments.CalendarEventFragment
import de.felixnuesse.timedsilence.fragments.TimeFragment
import android.content.res.ColorStateList
import android.support.design.widget.FloatingActionButton
import android.text.format.DateFormat
import android.util.Log
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME
import de.felixnuesse.timedsilence.activities.SettingsMainActivity
import de.felixnuesse.timedsilence.fragments.graph.GraphFragment
import de.felixnuesse.timedsilence.handler.AlarmHandler
import de.felixnuesse.timedsilence.handler.CalendarHandler
import de.felixnuesse.timedsilence.handler.SharedPreferencesHandler
import de.felixnuesse.timedsilence.handler.VolumeHandler
import de.felixnuesse.timedsilence.receiver.AlarmBroadcastReceiver
import de.felixnuesse.timedsilence.services.PauseTimerService
import de.felixnuesse.timedsilence.services.WidgetService
import de.felixnuesse.timedsilence.services.`interface`.TimerInterface
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


class MainActivity : AppCompatActivity(), TimerInterface {


    private var button_check : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)



        VolumeHandler.getVolumePermission(this)
        CalendarHandler.getCalendarReadPermission(this)

        button_check = getString(R.string.timecheck_stopped)

        //This hidden button is needed because the buttonsound of the main button is supressed because the device is still muted. A click is performed on this button, and when the onClick handler is set,
        //it plays a sound after the volume has changed to loud. Therefore it seems to be the main button who makes the sound
        button_buttonsound_fix.isSoundEffectsEnabled=true
        button_buttonsound_fix.setOnClickListener {
            Log.e(APP_NAME,"MainAcitivity: HiddenButton: PerformClick to make sound")
        }

        fab.setOnClickListener { view ->
            //Log.e(APP_NAME, "Main: fab: Clicked")
            setHandlerState()
        }

        frameLayout.setOnClickListener { view ->
            //Log.e(APP_NAME, "Main: FabTester: Clicked")
            buttonState()
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


        val tabs = tabLayout
        val mPager = viewPager

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

        PauseTimerService.registerListener(this)

        // use this to start and trigger a service
        val i = Intent(this, WidgetService::class.java)
        // potentially add data to the intent
        i.putExtra("KEY1", "Value to be used by the service");
        startService(i)
        buttonState()


        AlarmBroadcastReceiver().switchVolumeMode(this)
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

        val voLHandler = VolumeHandler()

        when (item.itemId) {
            R.id.action_settings -> openSettings()
            R.id.action_set_manual_loud -> {

                val makeSound=!voLHandler.isButtonClickAudible(this)

                voLHandler.setLoud()

                if(makeSound){
                    button_buttonsound_fix.performClick()
                }
            }
            R.id.action_set_manual_vibrate -> voLHandler.setVibrate()
            R.id.action_set_manual_silent -> voLHandler.setSilent()
        }
        voLHandler.applyVolume(applicationContext)
        return true;
    }

    override fun timerStarted(context: Context, timeAsLong: Long, timeAsString: String) {}

    override fun timerReduced(context: Context, timeAsLong: Long, timeAsString: String) {
        //textViewPausedTimestamp.visibility= View.VISIBLE
        //label_Paused_until.visibility= View.VISIBLE
        //textViewPausedTimestamp.text=PauseTimerService.getTimestampInProperLength(timeAsLong);
        buttonState()
    }

    override fun timerFinished(context: Context) {
        //textViewPausedTimestamp.visibility= View.INVISIBLE
        //label_Paused_until.visibility= View.INVISIBLE
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
        val lasttime = sharedPref.getLong("last_ExecTime", 0)

        val current =System.currentTimeMillis()
        val date = Date(current)


        //val df = DateFormat.
        val date1 = Date(current)
        val date2 = Date(lasttime)

        var difference = date1.time - date2.time

        val days = (difference / (1000 * 60 * 60 * 24));
        val hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60))
        val min = (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours) / (1000 * 60)

        var highScore =  DateFormat.getTimeFormat(this)
        if(hours>24){
            highScore =  DateFormat.getDateFormat(this)
        }

        nextCheckDisplayTextView.text=highScore.format(date)
    }



    private fun buttonState() {
        val fabTextView = findViewById<TextView>(R.id.fab_textview)

        Log.e(Constants.APP_NAME, "Main: ButtonStartCheck: State: "+button_check)

        if(AlarmHandler.checkIfNextAlarmExists(this)){
            setFabStarted(fab, fabTextView)
        }else if(PauseTimerService.isTimerRunning()){
            setFabPaused(fab, fabTextView)
        }else{
            setFabStopped(fab, fabTextView)
        }
        updateTimeCheckDisplay()
        WidgetService.updateStateWidget(this)
    }

    private fun setHandlerState() {

        Log.e(Constants.APP_NAME, "Main: setHandlerState: State: "+button_check)

        if(button_check == getString(R.string.timecheck_start)){
            AlarmHandler.createRepeatingTimecheck(this)
            SharedPreferencesHandler.setPref(this, PrefConstants.PREF_BOOT_RESTART,true)
            AlarmBroadcastReceiver().switchVolumeMode(this)
        }else if(button_check == getString(R.string.timecheck_paused)){
            AlarmHandler.createRepeatingTimecheck(this)
            SharedPreferencesHandler.setPref(this, PrefConstants.PREF_BOOT_RESTART,true)
            PauseTimerService.cancelTimer(this)
            AlarmBroadcastReceiver().switchVolumeMode(this)
        }else{
            AlarmHandler.removeRepeatingTimecheck(this)
            SharedPreferencesHandler.setPref(this, PrefConstants.PREF_BOOT_RESTART,false)
        }
        buttonState()
    }


    fun setFabStarted(fab: FloatingActionButton, text: TextView){
        text.text = getString(R.string.timecheck_running)
        fab.setImageResource(R.drawable.ic_play_arrow_white_24dp)
        fab.backgroundTintList = ColorStateList.valueOf(getColor(R.color.colorFab_started))
        button_check=getString(R.string.timecheck_stop)

    }

    fun setFabStopped(fab: FloatingActionButton, text: TextView){
        text.text = getString(R.string.timecheck_stopped)
        fab.setImageResource(R.drawable.ic_pause_white_24dp)
        fab.backgroundTintList = ColorStateList.valueOf(getColor(R.color.colorFab_stopped))
        AlarmHandler.removeRepeatingTimecheck(this)
        button_check=getString(R.string.timecheck_start)
    }

    fun setFabPaused(fab: FloatingActionButton, text: TextView){
        text.text = getString(R.string.timecheck_paused)
        fab.setImageResource(R.drawable.ic_fast_forward_white_24dp)
        fab.backgroundTintList = ColorStateList.valueOf(getColor(R.color.colorFab_paused))
        button_check=getString(R.string.timecheck_paused)
    }

    @Deprecated("replace by callback")
    fun getSharedPreferencesListener(): SharedPreferences.OnSharedPreferenceChangeListener {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            //Log.e(Constants.APP_NAME, "Main: SharedPrefs: prefs: "+prefs)
            //Log.e(Constants.APP_NAME, "Main: SharedPrefs: key:   "+key)


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
                0 -> return GraphFragment()
                1 -> return TimeFragment()
                2 -> return WifiConnectedFragment()
                3 -> return CalendarEventFragment()
                else -> return TimeFragment()
            }
        }
    }

}
