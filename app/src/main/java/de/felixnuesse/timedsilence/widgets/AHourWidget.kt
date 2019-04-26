package de.felixnuesse.timedsilence.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.services.PauseTileService
import de.felixnuesse.timedsilence.services.PauseTimerService
import de.felixnuesse.timedsilence.services.`interface`.TimerInterface
import android.content.ComponentName
import android.content.Intent


/**
 * Implementation of App Widget functionality.
 */
class AHourWidget : AppWidgetProvider(), TimerInterface {


    var mContext : Context?=null

    override fun timerStarted(timeAsLong: Long) {
        val views = RemoteViews(mContext?.packageName, R.layout.ahour_widget)
        views.setTextViewText(R.id.textViewHours_label, "")
        views.setTextViewText(R.id.textViewHours, PauseTileService.getTimestampInProperLength(timeAsLong))
        updateWidget()

    }

    override fun timerReduced(timeAsLong: Long) {

        val views = RemoteViews(mContext?.packageName, R.layout.ahour_widget)
        views.setTextViewText(R.id.textViewTimer, PauseTileService.getTimestampInProperLength(timeAsLong))


        updateWidget()

    }

    override fun timerFinished() {

        val views = RemoteViews(mContext?.packageName, R.layout.ahour_widget)
        views.setTextViewText(R.id.textViewHours_label, "Hours")
        views.setTextViewText(R.id.textViewHours, "One")
        updateWidget()
    }


    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        mContext=context


        for (appWidgetId in appWidgetIds) {
            updateAppWidget(
                context,
                appWidgetManager,
                appWidgetId
            )
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        PauseTimerService.registerListener(this)
        mContext=context
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        internal fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {

            val views = RemoteViews(context.packageName, R.layout.ahour_widget)
            views.setTextViewText(R.id.textViewHours, "ONE")
            views.setTextViewText(R.id.textViewHours_label, "HOUR")

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    fun updateWidget(){
        // Send a broadcast so that the Operating system updates the widget
        // 1
        val man = AppWidgetManager.getInstance(mContext)
        // 2
        val ids = man.getAppWidgetIds(
            ComponentName(mContext, AHourWidget::class.java))
        // 3
        val updateIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
        // 4
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        // 5
        mContext?.sendBroadcast(updateIntent)
    }
}

