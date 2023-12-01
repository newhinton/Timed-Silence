package de.felixnuesse.timedsilence.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import de.felixnuesse.timedintenttrigger.database.xml.Exporter
import de.felixnuesse.timedintenttrigger.database.xml.Importer

import de.felixnuesse.timedsilence.activities.settings.SelectorFragment
import de.felixnuesse.timedsilence.handler.PreferencesManager


class SettingsActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SettingsActivity"
    }

    private var mSelectorFragment = SelectorFragment(this)
    private var mCurrentFragment: Fragment = mSelectorFragment
    private var mOnBackCallback: OnBackPressedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openFragment(mCurrentFragment)

        mOnBackCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                openFragment(mSelectorFragment)
            }
        }

        onBackPressedDispatcher.addCallback(this, mOnBackCallback as OnBackPressedCallback)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Exporter.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Importer.onActivityResult(this, requestCode, resultCode, data)
    }

    fun openFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, fragment)
            .commit()
        mCurrentFragment = fragment

        mOnBackCallback?.isEnabled = fragment !is SelectorFragment
    }



}
