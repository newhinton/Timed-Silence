package de.felixnuesse.timedsilence.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.extensions.TAG
import de.felixnuesse.timedsilence.handler.trigger.Trigger



/**
 * Implementation of App Widget functionality.
 */
class StartStopWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            Log.e(TAG(), "PlayResumeWidget:Update Widget!")
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_start_stop)
        updateIcon(remoteViews, context)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (PLAYPAUSECLICKED == intent.action) {
            Log.e(TAG(), "PlayResumeWidget: A widget was clicked!")

            val appWidgetManager = AppWidgetManager.getInstance(context)

            var remoteViews = RemoteViews(context.packageName, R.layout.widget_start_stop)


            val watchWidget = ComponentName(context, StartStopWidget::class.java)

            val t = Trigger(context)
            if(t.checkIfNextAlarmExists()){
                t.removeTimecheck()
            } else {
                t.createAlarmIntime()
            }
            updateIcon(remoteViews, context)

            appWidgetManager.updateAppWidget(watchWidget, remoteViews)

        }
    }


    companion object {

        val PLAYPAUSECLICKED = "playpauseClick"

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {

            val views = RemoteViews(context.packageName, R.layout.widget_start_stop)

            views.setOnClickPendingIntent(R.id.playpausewidget_running, getPendingSelfIntent(context, PLAYPAUSECLICKED))
            views.setOnClickPendingIntent(R.id.playpausewidget_paused, getPendingSelfIntent(context, PLAYPAUSECLICKED))
            views.setOnClickPendingIntent(R.id.playpausewidget_stopped, getPendingSelfIntent(context, PLAYPAUSECLICKED))

            updateIcon(views, context)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        protected fun getPendingSelfIntent(context: Context, action: String): PendingIntent {
            val intent = Intent(context, StartStopWidget::class.java)
            intent.action = action
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        protected fun updateIcon(remoteViews: RemoteViews, context: Context){
            remoteViews.setViewVisibility( R.id.playpausewidget_running, View.GONE)
            remoteViews.setViewVisibility( R.id.playpausewidget_stopped, View.GONE)
            remoteViews.setViewVisibility( R.id.playpausewidget_paused, View.GONE)

            if(Trigger(context).checkIfNextAlarmExists()){
                remoteViews.setViewVisibility( R.id.playpausewidget_running, View.VISIBLE)
            } else {
                remoteViews.setViewVisibility( R.id.playpausewidget_stopped, View.VISIBLE)
            }
        }
    }

}

