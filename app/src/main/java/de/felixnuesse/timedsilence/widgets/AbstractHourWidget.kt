package de.felixnuesse.timedsilence.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.services.PauseTimerService
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent


/**
 * Implementation of App Widget functionality.
 */
abstract class AbstractHourWidget : AppWidgetProvider() {


    private val SYNC_CLICKED = "automaticWidgetSyncButtonClick"

    abstract val mWidgetTime: Long
    abstract val mWidgetName: String
    abstract val mWidgetClass: Class<*>


    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them

        for (appWidgetId in appWidgetIds) {

            Log.e(Constants.APP_NAME, "AbstractHourWidget: Updated Widgets!")
            updateAppWidget(
                context,
                appWidgetManager,
                appWidgetId
            )
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (SYNC_CLICKED == intent.action) {

            val appWidgetManager = AppWidgetManager.getInstance(context)

            val remoteViews = RemoteViews(context.packageName, R.layout.ahour_widget)
            val watchWidget = ComponentName(context, mWidgetClass)

            Log.e(Constants.APP_NAME, "AbstractHourWidget($mWidgetName): A widget was clicked!")

            PauseTimerService.toggleTimer(context, mWidgetTime)

            appWidgetManager.updateAppWidget(watchWidget, remoteViews)

        }
    }

    protected fun getPendingSelfIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, mWidgetClass)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {

        val views = RemoteViews(context.packageName, R.layout.ahour_widget)


        views.setOnClickPendingIntent(R.id.appwidget_text, getPendingSelfIntent(context, SYNC_CLICKED));

        val timetodisplay: String

        if (PauseTimerService.isTimerRunning() && PauseTimerService.mTimerTimeInitial == mWidgetTime) {
            timetodisplay = PauseTimerService.getTimestampInProperLength(PauseTimerService.mTimerTimeLeft)

        } else {
            timetodisplay = PauseTimerService.getTimestampInProperLength(mWidgetTime)
        }

        views.setTextViewText(R.id.widget_tv_label, "Pause for $timetodisplay")

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

