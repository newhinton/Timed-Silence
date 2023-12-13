package de.felixnuesse.timedsilence

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import de.felixnuesse.timedintenttrigger.database.xml.Exporter
import de.felixnuesse.timedintenttrigger.database.xml.Importer

import de.felixnuesse.timedsilence.fragments.settings.SelectorFragment


class SettingsActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SettingsActivity"
    }

    private var mSelectorFragment = SelectorFragment(this)
    private var mCurrentFragment: Fragment = mSelectorFragment
    private var mOnBackCallback: OnBackPressedCallback? = null


    private var mExporter = Exporter(this)
    private var mImporter = Importer(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openFragment(mCurrentFragment)

        mOnBackCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(mCurrentFragment is SelectorFragment) {
                    finish()
                }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mExporter.onRequestPermissionsResult(requestCode, resultCode, data)
        mImporter.onRequestPermissionsResult(requestCode, resultCode, data)
    }

    fun openFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, fragment)
            .commit()
        mCurrentFragment = fragment

        mOnBackCallback?.isEnabled = fragment !is SelectorFragment
    }

    fun export() {
        mExporter.export()
    }

    fun import() {
        mImporter.import()
    }

}
