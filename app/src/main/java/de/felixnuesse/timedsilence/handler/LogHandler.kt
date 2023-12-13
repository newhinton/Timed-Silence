package de.felixnuesse.timedsilence.handler

import android.content.Context
import android.os.Environment
import android.util.Log
import de.felixnuesse.timedsilence.util.DateUtil
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class LogHandler() {

    companion object {

        fun writeLog(context: Context, who: String, why: String, what: String) {
            val timestamp = DateUtil.getDateFormatted("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            val time = "${System.currentTimeMillis()}; $timestamp"
            val content = "$time [$who] - $why: $what"

            Log.e("LogHandler", content)
            append(context, content, "log.txt")
        }


        fun writeDebugFiles(context: Context, who: String, content: String) {
            val timestamp = DateUtil.getDateFormatted("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            val time = "${System.currentTimeMillis()}; $timestamp"

            Log.e("DebugLogHandler", content)
            append(context, content, "$time-$who.txt")
        }

        private fun append(context: Context, content: String, filename: String) {
            try {
                val log = File(context.getExternalFilesDir(null), filename)
                val writer = FileWriter(log, true)
                writer.append(content+"\n")
                writer.flush()
                writer.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}