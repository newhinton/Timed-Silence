package de.felixnuesse.timedsilence.receiver


import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.extensions.TAG
import de.felixnuesse.timedsilence.handler.PreferencesManager
import de.felixnuesse.timedsilence.handler.volume.VolumeHandler
import de.felixnuesse.timedsilence.handler.volume.VolumeState
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.volumestate.StateGenerator


class NotificationListener : NotificationListenerService() {

    private val TAG: String = this.javaClass.simpleName

    override fun onNotificationPosted(notification: StatusBarNotification) {
        handleNotification(false, notification)
    }

    override fun onNotificationRemoved(notification: StatusBarNotification) {
        handleNotification(true, notification)
    }

    fun handleNotification(isRemove: Boolean, notification: StatusBarNotification) {
        val proceed = PreferencesManager(this).shouldSearchInNotifications()
        if(!proceed) {
            return
        }

        if(isRemove) {
            Log.e(TAG(), "NotificationListener: Removed notification, check!")
            VolumeHandler(this, "NotificationListener").setVolumeStateAndApply(StateGenerator(this).stateAt(System.currentTimeMillis()))
            return
        }

        Log.e(TAG(), "NotificationListener: Posted notification, check!")

        val toSearch =  notification.notification.extras.toString()+notification.notification.toString()+notification.toString()

        val db = DatabaseHandler(this)
        db.getKeywords().forEach {
            if (toSearch.lowercase().contains(it.keyword.lowercase())) {
                val state = VolumeState(it.volume)
                state.setReason(Constants.REASON_MANUALLY_SET, "Keyword ${it.keyword} was found in notification")
                VolumeHandler(this, "NotificationListener").setVolumeStateAndApply(state)
                return
            }
        }
    }

}
