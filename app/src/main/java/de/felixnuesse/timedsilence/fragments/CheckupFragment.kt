package de.felixnuesse.timedsilence.fragments

import android.app.NotificationManager
import android.app.NotificationManager.Policy.PRIORITY_CATEGORY_ALARMS
import android.app.NotificationManager.Policy.PRIORITY_CATEGORY_CALLS
import android.app.NotificationManager.Policy.PRIORITY_CATEGORY_CONVERSATIONS
import android.app.NotificationManager.Policy.PRIORITY_CATEGORY_MESSAGES
import android.app.NotificationManager.Policy.PRIORITY_CATEGORY_REPEAT_CALLERS
import android.app.NotificationManager.Policy.SUPPRESSED_EFFECT_AMBIENT
import android.app.NotificationManager.Policy.SUPPRESSED_EFFECT_BADGE
import android.app.NotificationManager.Policy.SUPPRESSED_EFFECT_FULL_SCREEN_INTENT
import android.app.NotificationManager.Policy.SUPPRESSED_EFFECT_LIGHTS
import android.app.NotificationManager.Policy.SUPPRESSED_EFFECT_NOTIFICATION_LIST
import android.app.NotificationManager.Policy.SUPPRESSED_EFFECT_PEEK
import android.app.NotificationManager.Policy.SUPPRESSED_EFFECT_SCREEN_OFF
import android.app.NotificationManager.Policy.SUPPRESSED_EFFECT_SCREEN_ON
import android.app.NotificationManager.Policy.SUPPRESSED_EFFECT_STATUS_BAR
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.felixnuesse.timedsilence.databinding.FragmentCheckupBinding
import de.felixnuesse.timedsilence.extensions.e
import de.felixnuesse.timedsilence.model.contacts.Contact
import de.felixnuesse.timedsilence.model.contacts.ContactUtil
import de.felixnuesse.timedsilence.util.PermissionManager


class CheckupFragment : Fragment() {

    companion object {
        private const val TAG = "CheckupFragment"
    }

    private var _binding: FragmentCheckupBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateData()

        binding.gotoDND.setOnClickListener {
            startActivity(Intent("android.settings.ZEN_MODE_SETTINGS"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        updateData()
    }

    private fun updateData() {
        val context = binding.root.context

        var l = arrayListOf<Contact>()


        var permissionManager = PermissionManager(context)
        if(permissionManager.grantedContacts()) {
            binding.CheckupContentContainer.visibility = View.VISIBLE
            binding.CheckupPermissionCheckContainer.visibility = View.GONE
            l = ContactUtil(context).getContactList()
        } else{
            binding.CheckupContentContainer.visibility = View.GONE
            binding.buttonRequestContactPermissions.setOnClickListener{
                permissionManager.requestContactsAccess()
            }
        }

        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        val policy = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            mNotificationManager?.consolidatedNotificationPolicy
        } else {
            mNotificationManager?.notificationPolicy
        }


        /*
        The following is the current (api 33) list of available categories:

        fun allowAlarms(): Boolean {
            return priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_ALARMS != 0
        }
        fun allowMedia(): Boolean {
            return priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_MEDIA != 0
        }
        fun allowSystem(): Boolean {
            return priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_SYSTEM != 0
        }
        fun allowRepeatCallers(): Boolean {
            return priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_REPEAT_CALLERS != 0
        }
        fun allowCalls(): Boolean {
            return priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_CALLS != 0
        }
        fun allowConversations(): Boolean {
            return priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_CONVERSATIONS != 0
        }
        fun allowMessages(): Boolean {
            return priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_MESSAGES != 0
        }
        fun allowEvents(): Boolean {
            return priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_EVENTS != 0
        }
        fun allowReminders(): Boolean {
            return priorityCategories and NotificationManager.Policy.PRIORITY_CATEGORY_REMINDERS != 0
        }
        fun allowCallsFrom(): Int {
            return priorityCallSenders
        }
        fun allowMessagesFrom(): Int {
            return priorityMessageSenders
        }
        fun allowConversationsFrom(): Int {
            return priorityConversationSenders
        }
         */

        var prioCategories = policy?.priorityCategories ?: 0
        var suppressedVisuals = policy?.suppressedVisualEffects ?: 0


        val repeatCaller = prioCategories and PRIORITY_CATEGORY_REPEAT_CALLERS
        val priorityCaller = prioCategories and PRIORITY_CATEGORY_CALLS

        // contacts, or conversations
        var repeatMessenger = prioCategories and PRIORITY_CATEGORY_MESSAGES

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            repeatMessenger += prioCategories and PRIORITY_CATEGORY_CONVERSATIONS
        }

        val alarms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            prioCategories and PRIORITY_CATEGORY_ALARMS
        } else {
            1
        }


        binding.checkboxHavePriorityContacts.isChecked = l.size > 0
        binding.checkboxPriorityContactsCanBypass.isChecked = priorityCaller != 0
        binding.checkboxRepeatCallerCanBypass.isChecked = repeatCaller != 0
        binding.checkboxRepeatMessengerCanBypass.isChecked = repeatMessenger != 0
        binding.checkboxRepeatMessengerCanBypass.isChecked = repeatMessenger != 0
        binding.checkboxAlarmsCanBypass.isChecked = alarms != 0
        binding.checkboxNotificationsVisible.isChecked = !areAllVisualEffectsSuppressed(suppressedVisuals)

    }

    /**
     * Extracted from the Notification Manager itself. It is useful for me here.
     * Version: API 34
     */
    fun areAllVisualEffectsSuppressed(effects: Int): Boolean {
        var allEffects = getAllVisualEffects()
        for (i in allEffects.indices) {
            val effect: Int = allEffects[i]
            if (effects and effect == 0) {
                return false
            }
        }
        return true
    }

    /**
     * Extracted from the Notification Manager itself. It is useful for me here.
     * Version: API 34
     */
    fun getAllVisualEffects(): IntArray {
        return intArrayOf(
            SUPPRESSED_EFFECT_SCREEN_OFF,
            SUPPRESSED_EFFECT_SCREEN_ON,
            SUPPRESSED_EFFECT_FULL_SCREEN_INTENT,
            SUPPRESSED_EFFECT_LIGHTS,
            SUPPRESSED_EFFECT_PEEK,
            SUPPRESSED_EFFECT_STATUS_BAR,
            SUPPRESSED_EFFECT_BADGE,
            SUPPRESSED_EFFECT_AMBIENT,
            SUPPRESSED_EFFECT_NOTIFICATION_LIST
        )
    }
}
