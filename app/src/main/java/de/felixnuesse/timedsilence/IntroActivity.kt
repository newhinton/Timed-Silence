package de.felixnuesse.timedsilence

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import de.felixnuesse.timedsilence.util.PermissionManager

class IntroActivity : AppIntro() {


    companion object {
        const val INTRO_PREFERENCES = "IntroPreferences"
    }

    private var permissionManager = PermissionManager(this)
    private var isDoNotDisturbSlide = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Make sure you don't call setContentView!

        isWizardMode = true
        isColorTransitionsEnabled = true

        // dont allow the intro to be bypassed
        isSystemBackButtonLocked = true


        // Call addSlide passing your Fragments.
        // You can use AppIntroFragment to use a pre-built fragment
        addSlide(
            AppIntroFragment.createInstance(
                title = getString(R.string.intro_slide_welcome_title),
                description = getString(R.string.intro_slide_welcome_description),
                imageDrawable = R.drawable.undraw_reading_time,
                backgroundColorRes = R.color.intro_color1
            ))

        addSlide(
            AppIntroFragment.createInstance(
                title = getString(R.string.intro_slide_calendar_title),
                description = getString(R.string.intro_slide_calendar_description),
                imageDrawable = R.drawable.undraw_schedule,
                backgroundColorRes = R.color.intro_color2
            ))
        // Regarding Slide Numbers: They index with 0. But since we want to show them AFTER the slide,
        // We have to offset them by one, and assume 1-indexing.
        askForPermissions(
            permissions = arrayOf(Manifest.permission.READ_CALENDAR),
            slideNumber = 2,
            required = false)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            addSlide(
                AppIntroFragment.createInstance(
                    title = getString(R.string.intro_slide_notification_title),
                    description = getString(R.string.intro_slide_notification_description),
                    imageDrawable = R.drawable.undraw_notify,
                    backgroundColorRes = R.color.intro_color1
                ))
            askForPermissions(
                permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                slideNumber = 3,
                required = false)
        }

        addSlide(
            AppIntroFragment.createInstance(
                title = getString(R.string.intro_slide_donotdisturb_title),
                description = getString(R.string.intro_slide_donotdisturb_description),
                imageDrawable = R.drawable.undraw_camping,
                backgroundColorRes = R.color.intro_color2
            ))


        addSlide(
            AppIntroFragment.createInstance(
                title = getString(R.string.intro_slide_done_title),
                description = getString(R.string.intro_slide_done_description),
                imageDrawable = R.drawable.undraw_completed,
                backgroundColorRes = R.color.intro_color1
            ))
    }


    override fun onPageSelected(position: Int) {
        //check if we *were* on the dnd slide on the last slide, and ask for dnd. Then update slide.
        if(isDoNotDisturbSlide) {
            if(!permissionManager.grantedDoNotDisturb()) {
                permissionManager.requestDoNotDisturb()
            }
        }

        val pageID = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { 3 } else { 2 }
        isDoNotDisturbSlide = position == pageID
    }


    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // Decide what to do when the user clicks on "Skip"
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Decide what to do when the user clicks on "Done"
        endIntro()
    }


    private fun endIntro() {
        val sharedPref = applicationContext.getSharedPreferences(INTRO_PREFERENCES, Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putBoolean(getString(R.string.pref_key_intro_v1_0_0), true)
            apply()
        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}