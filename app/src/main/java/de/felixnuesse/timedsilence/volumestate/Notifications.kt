package de.felixnuesse.timedsilence.volumestate


import android.app.NotificationManager
import android.content.Context
import android.service.notification.StatusBarNotification
import android.util.Log
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.extensions.TAG
import de.felixnuesse.timedsilence.handler.PreferencesManager
import de.felixnuesse.timedsilence.handler.volume.VolumeState
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_UNSET
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.isFirstLouder
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.receiver.NotificationListener


open class Notifications(private var mContext: Context): DeterministicCalculationInterface() {

    private val mPrefs = PreferencesManager(mContext)
    private val mDB = DatabaseHandler(mContext)

    override fun stateAt(timeInMs: Long): ArrayList<VolumeState> {
        return ArrayList()
    }

    override fun states(): ArrayList<VolumeState> {
        val list = ArrayList<VolumeState>()

        if(mPrefs.shouldSearchInNotifications()) {
            var currentVolumeState = VolumeState(TIME_SETTING_UNSET)
            NotificationListener.getService()?.getAllActiveNotifications()?.forEach {
                val checkedState = handleNotification(it)?: VolumeState(TIME_SETTING_UNSET)
                if (isFirstLouder(checkedState, currentVolumeState)) {
                    currentVolumeState = checkedState
                }
            }
            if(currentVolumeState.state != TIME_SETTING_UNSET){
                list.add(currentVolumeState)
            }
        }

        Log.e(TAG(), "Size: ${list.size}")
        return list
    }

    private fun handleNotification(notification: StatusBarNotification): VolumeState? {
        Log.e(TAG(), "Size: ${notification.notification}")
        val toSearch =  notification.notification.extras.toString()+notification.notification.toString()+notification.toString()

        mDB.getKeywords().forEach {
            if (toSearch.lowercase().contains(it.keyword.lowercase())) {
                val state = VolumeState(it.volume)
                state.startTime = System.currentTimeMillis() - 60*1000
                state.endTime = state.startTime + 120*1000 // add a minute
                state.setReason(Constants.REASON_NOTIFICATION_VISIBLE, "Keyword: ${it.keyword}")
                return state
            }
        }
        return null
    }

    override fun isEnabled(): Boolean {
        return mPrefs.shouldSearchInNotifications()
    }
}