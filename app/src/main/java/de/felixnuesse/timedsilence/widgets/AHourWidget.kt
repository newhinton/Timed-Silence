package de.felixnuesse.timedsilence.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.services.PauseTileService
import de.felixnuesse.timedsilence.services.PauseTimerService
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent


/**
 * Implementation of App Widget functionality.
 */
class AHourWidget : AppWidgetProvider() {


    private val SYNC_CLICKED = "automaticWidgetSyncButtonClick"

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them

        for (appWidgetId in appWidgetIds) {

            Log.e(Constants.APP_NAME, "AHourWidget: Updated Widgets!")
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
            val watchWidget = ComponentName(context, AHourWidget::class.java)

            Log.e(Constants.APP_NAME, "AHourWidget: A widget was clicked!")

            PauseTimerService.startAutoTimer(context)

            appWidgetManager.updateAppWidget(watchWidget, remoteViews)

        }
    }

    protected fun getPendingSelfIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, AHourWidget::class.java)
        intent.action = action;
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {

        val views = RemoteViews(context.packageName, R.layout.ahour_widget)


        views.setOnClickPendingIntent(R.id.appwidget_text, getPendingSelfIntent(context, SYNC_CLICKED));

        if (PauseTimerService.isTimerRunning()) {
            views.setTextViewText(
                R.id.textViewHours,
                PauseTimerService.getTimestampInProperLength(PauseTimerService.mTimerTimeLeft)
            )
            views.setTextViewText(R.id.textViewHours_label, "")
        } else {
            views.setTextViewText(R.id.textViewHours, "NULL ")
            views.setTextViewText(R.id.textViewHours_label, "HOUR")
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

