package de.felixnuesse.timedsilence.model.database

import de.felixnuesse.timedsilence.handler.PreferencesHolder
import de.felixnuesse.timedsilence.model.data.CalendarObject
import de.felixnuesse.timedsilence.model.data.KeywordObject
import de.felixnuesse.timedsilence.model.data.ScheduleObject
import de.felixnuesse.timedsilence.model.data.WifiObject


import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject

@Serializable
data class AppDataStructure(var date: String) {

    var schedules = arrayListOf<ScheduleObject>()
    var calendars = arrayListOf<CalendarObject>()
    var keywords = arrayListOf<KeywordObject>()
    var wifi = arrayListOf<WifiObject>()


    private var preferences: PreferencesHolder? = null

    fun addSchedule(schedule: ScheduleObject) {
        schedules.add(schedule)
    }

    fun addCalendar(calendar: CalendarObject) {
        calendars.add(calendar)
    }

    fun addKeyword(keyword: KeywordObject) {
        keywords.add(keyword)
    }

    fun addWifi(wifi: WifiObject) {
        this.wifi.add(wifi)
    }

    fun setPreferences(prefs: PreferencesHolder) {
        preferences = prefs
    }

    fun getPreferences(): PreferencesHolder {
        return preferences?: PreferencesHolder()
    }

    fun asJSON(): JSONObject {
        return JSONObject(Json.encodeToString(this))
    }

    companion object {
        fun fromJSON(data: String): AppDataStructure {
            val json = Json { ignoreUnknownKeys = true }
            return json.decodeFromString(data)
        }
    }
}