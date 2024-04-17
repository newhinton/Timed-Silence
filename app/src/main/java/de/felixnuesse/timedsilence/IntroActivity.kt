package de.felixnuesse.timedsilence

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import de.felixnuesse.disky.ui.appintro.IdentifiableAppIntroFragment
import de.felixnuesse.disky.ui.appintro.SlideLeaveInterface
import de.felixnuesse.timedsilence.util.PermissionManager
import de.felixnuesse.timedsilence.util.PrepareDefaultsUtil

class IntroActivity : AppIntro(), SlideLeaveInterface {


    companion object {
        const val INTRO_PREFERENCES = "IntroPreferences"

        private const val SLIDE_ID_WELCOME = "SLIDE_ID_WELCOME"
        private const val SLIDE_ID_CALENDAR = "SLIDE_ID_CALENDAR"
        private const val SLIDE_ID_NOTIFICATIONS = "SLIDE_ID_NOTIFICATIONS"
        private const val SLIDE_ID_DND = "SLIDE_ID_DND"
    }

    private var permissionManager = PermissionManager(this)


    override fun onResume() {
        enableEdgeToEdge()
        super.onResume()
        setImmersiveMode()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Make sure you don't call setContentView!

        setImmersiveMode()
        showStatusBar(true)
        isWizardMode = true
        isColorTransitionsEnabled = true

        // dont allow the intro to be bypassed
        isSystemBackButtonLocked = true


        // Call addSlide passing your Fragments.
        // You can use AppIntroFragment to use a pre-built fragment
        addSlide(
            IdentifiableAppIntroFragment.createInstance(
                title = getString(R.string.intro_slide_welcome_title),
                description = getString(R.string.intro_slide_welcome_description),
                imageDrawable = R.drawable.undraw_reading_time,
                backgroundColorRes = R.color.intro_color1,
                id = SLIDE_ID_WELCOME,
                callback = this
            ))

        addSlide(
            IdentifiableAppIntroFragment.createInstance(
                title = getString(R.string.intro_slide_calendar_title),
                description = getString(R.string.intro_slide_calendar_description),
                imageDrawable = R.drawable.undraw_schedule,
                backgroundColorRes = R.color.intro_color2,
                id = SLIDE_ID_CALENDAR,
                callback = this
            ))
        // Regarding Slide Numbers: They index with 0. But since we want to show them AFTER the slide,
        // We have to offset them by one, and assume 1-indexing.
        askForPermissions(
            permissions = arrayOf(Manifest.permission.READ_CALENDAR),
            slideNumber = 2,
            required = false)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            addSlide(
                IdentifiableAppIntroFragment.createInstance(
                    title = getString(R.string.intro_slide_notification_title),
                    description = getString(R.string.intro_slide_notification_description),
                    imageDrawable = R.drawable.undraw_notify,
                    backgroundColorRes = R.color.intro_color1,
                    id = SLIDE_ID_NOTIFICATIONS,
                    callback = this
                ))
            askForPermissions(
                permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                slideNumber = 3,
                required = false)
        }

        addSlide(
            IdentifiableAppIntroFragment.createInstance(
                title = getString(R.string.intro_slide_donotdisturb_title),
                description = getString(R.string.intro_slide_donotdisturb_description),
                imageDrawable = R.drawable.undraw_camping,
                backgroundColorRes = R.color.intro_color2,
                id = SLIDE_ID_DND,
                callback = this
            ))

        addSlide(
            AppIntroFragment.createInstance(
                title = getString(R.string.intro_slide_done_title),
                description = getString(R.string.intro_slide_done_description),
                imageDrawable = R.drawable.undraw_completed,
                backgroundColorRes = R.color.intro_color1
            ))
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
        PrepareDefaultsUtil.addDefaults(this)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun allowSlideLeave(id: String): Boolean {
        return when(id) {
            SLIDE_ID_DND -> permissionManager.grantedDoNotDisturb()
            else -> true
        }
    }

    override fun onSlideLeavePrevented(id: String) {
        when(id) {
            SLIDE_ID_DND -> permissionManager.requestDoNotDisturb()
            else -> {}
        }
    }

}