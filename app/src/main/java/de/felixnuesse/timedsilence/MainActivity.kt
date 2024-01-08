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

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.text.format.DateFormat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import de.felixnuesse.timedsilence.Constants.Companion.MAIN_ACTIVITY_LOAD_CALENDAR_FORCE
import de.felixnuesse.timedsilence.IntroActivity.Companion.INTRO_PREFERENCES
import de.felixnuesse.timedsilence.databinding.ActivityMainBinding
import de.felixnuesse.timedsilence.extensions.TAG
import de.felixnuesse.timedsilence.fragments.BluetoothFragment
import de.felixnuesse.timedsilence.fragments.CalendarFragment
import de.felixnuesse.timedsilence.fragments.CheckupFragment
import de.felixnuesse.timedsilence.fragments.KeywordFragment
import de.felixnuesse.timedsilence.fragments.ScheduleFragment
import de.felixnuesse.timedsilence.fragments.graph.GraphFragment
import de.felixnuesse.timedsilence.handler.*
import de.felixnuesse.timedsilence.handler.trigger.Trigger
import de.felixnuesse.timedsilence.handler.volume.VolumeHandler
import de.felixnuesse.timedsilence.services.`interface`.TimerInterface
import de.felixnuesse.timedsilence.volumestate.StateGenerator

import java.util.*


class MainActivity : AppCompatActivity(), TimerInterface {

    private var mDontCheckGraph = true
    private var button_check: String = ""
    private var lastTabPosition = 0
    private lateinit var mPager: ViewPager
    private lateinit var mTrigger: Trigger
    private lateinit var mVolumeHandler: VolumeHandler

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = applicationContext.getSharedPreferences(INTRO_PREFERENCES, Context.MODE_PRIVATE)
        if (!sharedPref.getBoolean(getString(R.string.pref_key_intro_v1_0_0), false)) {
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        mTrigger = Trigger(this)
        mVolumeHandler = VolumeHandler(this)

        button_check = getString(R.string.timecheck_stopped)

        //This hidden button is needed because the buttonsound of the main button is supressed because the device is still muted. A click is performed on this button, and when the onClick handler is set,
        //it plays a sound after the volume has changed to loud. Therefore it seems to be the main button who makes the sound
        binding.buttonButtonsoundFix.isSoundEffectsEnabled = true
        binding.buttonButtonsoundFix.setOnClickListener {
            Log.e(TAG(), "MainAcitivity: HiddenButton: PerformClick to make sound")
        }

        binding.fab.setOnClickListener {
            //Log.e(TAG(), "Main: fab: Clicked")
            setHandlerState()
        }

        val tabs = binding.tabLayout
        mPager = binding.viewPager

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                lastTabPosition = tab.position
                mPager.currentItem = lastTabPosition
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        mPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                val tab = tabs.getTabAt(position)
                tab?.select()
            }
        })


        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        mPager.offscreenPageLimit = 4
        mPager.adapter = pagerAdapter

        for(i in 0..4) {
            tabs.getTabAt(i)?.text = ""
        }

        buttonState()
        handleCalendarFragmentIntentExtra()
        if (mDontCheckGraph) {
            mVolumeHandler.setVolumeStateAndApply(StateGenerator(this).stateAt(System.currentTimeMillis()))
        }

    }


    /**
     * This opens the CalendarFragment if the intent has the proper extras.
     */
    private fun handleCalendarFragmentIntentExtra() {
        val intentFragment = intent?.extras?.getInt(Constants.MAIN_ACTIVITY_LOAD_CALENDAR)
        if (intentFragment == MAIN_ACTIVITY_LOAD_CALENDAR_FORCE) {
            mPager.currentItem = 2
            mDontCheckGraph = true
        }
    }

    override fun onResume() {
        super.onResume()

        handleCalendarFragmentIntentExtra()
        buttonState()

        //This handler is needed. Otherwise the state is not beeing restored
        Handler().postDelayed({
            if (binding.viewPager.adapter != null) {
                binding.viewPager.adapter = ScreenSlidePagerAdapter(supportFragmentManager)
                mPager.currentItem = lastTabPosition
            }
        }, 0)
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        if (menu is MenuBuilder) {
            var builder = menu
            builder.setOptionalIconsVisible(true)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        when (item.itemId) {
            R.id.action_about -> openAbout()
            R.id.action_goto_dnd -> openDnDSettings()
            R.id.action_settings -> openSettings()
            R.id.action_set_manual_loud -> {
                val makeSound = !mVolumeHandler.isButtonClickAudible()
                mVolumeHandler.setLoud()
                if (makeSound) {
                    binding.buttonButtonsoundFix.performClick()
                }
                Toast.makeText(this, getString(R.string.loud), Toast.LENGTH_LONG).show()
            }

            R.id.action_set_manual_vibrate -> {
                mVolumeHandler.setVibrate()
                Toast.makeText(
                    this, getString(
                        R.string.vibrate
                    ), Toast.LENGTH_LONG
                ).show()
            }

            R.id.action_set_manual_silent -> {
                mVolumeHandler.setSilent()
                Toast.makeText(
                    this, getString(
                        R.string.silent
                    ), Toast.LENGTH_LONG
                ).show()
            }
        }
        mVolumeHandler.applyVolume()
        return true
    }

    override fun timerStarted(context: Context, timeAsLong: Long, timeAsString: String) {}

    override fun timerReduced(context: Context, timeAsLong: Long, timeAsString: String) {
        buttonState()
    }

    override fun timerFinished(context: Context) {
        buttonState()
    }

    private fun openSettings(): Boolean {
        val intent = Intent(this, SettingsActivity::class.java).apply {}
        startActivity(intent)
        return true
    }

    private fun openAbout(): Boolean {
        val intent = Intent(this, AboutActivity::class.java).apply {}
        startActivity(intent)
        return true
    }

    private fun openDnDSettings(): Boolean {
        startActivity(Intent("android.settings.ZEN_MODE_SETTINGS"))
        return true
    }

    private fun updateTimeCheckDisplay() {
        binding.nextCheckDisplay.text = mTrigger.getNextAlarmTimestamp()

        val sharedPref = this.getSharedPreferences("test", Context.MODE_PRIVATE) ?: return
        val lasttime = sharedPref.getLong("last_ExecTime", 0)

        val current = System.currentTimeMillis()
        val date = Date(current)


        //val df = DateFormat.
        val date1 = Date(current)
        val date2 = Date(lasttime)

        var difference = date1.time - date2.time

        val days = (difference / (1000 * 60 * 60 * 24));
        val hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60))
        val min = (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours) / (1000 * 60)

        var highScore = DateFormat.getTimeFormat(this)
        if (hours > 24) {
            highScore = DateFormat.getDateFormat(this)
        }

        binding.nextCheckDisplay.text = highScore.format(date)
    }


    private fun buttonState() {
        Log.e(TAG(), "Main: ButtonStartCheck: State: $button_check")

        //Todo remove dummy textview
        if (mTrigger.checkIfNextAlarmExists()) {
            setFabStarted(binding.fab, TextView(this))
            binding.runningStatus.visibility = View.INVISIBLE
        } else {
            setFabStopped(binding.fab, TextView(this))
            binding.runningStatus.visibility = View.VISIBLE
        }
        updateTimeCheckDisplay()

        // Generally Hide this StatusElement.
        binding.lastCheckStatus.visibility = View.GONE
    }

    private fun setHandlerState() {

        Log.e(TAG(), "Main: setHandlerState: State: $button_check")

        if (button_check == getString(R.string.timecheck_start)) {
            mTrigger.createAlarmIntime()
            PreferencesManager(this).setRestartOnBoot(true)
            mVolumeHandler.setVolumeStateAndApply(StateGenerator(this).stateAt(System.currentTimeMillis()))

        } else if (button_check == getString(R.string.timecheck_paused)) {
            mTrigger.createAlarmIntime()
            PreferencesManager(this).setRestartOnBoot(true)
            mVolumeHandler.setVolumeStateAndApply(StateGenerator(this).stateAt(System.currentTimeMillis()))
        } else {
            mTrigger.removeTimecheck()
            PreferencesManager(this).setRestartOnBoot(false)
        }
        buttonState()
    }


    private fun setFabStarted(fab: FloatingActionButton, text: TextView) {
        text.text = getString(R.string.timecheck_running)
        fab.backgroundTintList = ColorStateList.valueOf(getColor(R.color.colorFab_running))
        // fab.setImageResource(R.drawable.ic_play_arrow_white_24dp)

        val drawable = AppCompatResources.getDrawable(this, R.drawable.icon_pause)
        drawable?.mutate()?.setColorFilter(
            resources.getColor(R.color.colorStateButtonIcon, text.context.theme),
            PorterDuff.Mode.SRC_IN
        )

        fab.setImageDrawable(drawable)

        button_check = getString(R.string.timecheck_stop)

    }

    private fun setFabStopped(fab: FloatingActionButton, text: TextView) {
        text.text = getString(R.string.timecheck_stopped)
        fab.backgroundTintList = ColorStateList.valueOf(getColor(R.color.colorFab_stopped))


        val drawable = AppCompatResources.getDrawable(this, R.drawable.icon_play_arrow)
        drawable?.mutate()?.setColorFilter(
            resources.getColor(R.color.colorStateButtonIcon, text.context.theme),
            PorterDuff.Mode.SRC_IN
        )

        fab.setImageDrawable(drawable)
        mTrigger.removeTimecheck()
        button_check = getString(R.string.timecheck_start)
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     * FragmentStatePagerAdapter
     */
    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount(): Int = 6

        override fun getItem(position: Int): Fragment {

            when (position) {
                0 -> return GraphFragment()
                1 -> return ScheduleFragment()
                2 -> return CalendarFragment()
                3 -> return KeywordFragment()
                //4 -> return WifiConnectedFragment()
                4 -> return BluetoothFragment()
                5 -> return CheckupFragment()
                else -> return ScheduleFragment()
            }
        }
    }

}
