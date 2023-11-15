package de.felixnuesse.timedsilence.handler

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date


class LogHandler() {

    companion object {
        fun writeVolumeManager(context: Context, content: String) {
            append(context, content, "volumes.txt")
        }
        fun writeTargeted(context: Context, content: String) {
            append(context, content, "log.txt")
        }
        fun append(context: Context, content: String, filename: String) {
            try {
                Log.e("TAG", context.getExternalFilesDir(null).toString())
                val log = File(context.getExternalFilesDir(null), filename)
                val writer = FileWriter(log, true)
                writer.append(content+"\n")
                writer.flush()
                writer.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun writeAppLog(context: Context, content: String) {
            val now = Date()
            val timestamp = now.time
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            val dateStr = sdf.format(timestamp)

            val text = "$dateStr;;$content"
            append(context, text, "log.txt")
        }
    }
}