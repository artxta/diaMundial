package com.jose.diamundial.data

import android.content.Context
import android.util.Log
import com.jose.diamundial.R
import com.jose.diamundial.domain.Event
import com.jose.diamundial.ui.DebugLogger
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class EventParser(private val context: Context) {

    companion object {
        private const val EDITED_DIR = "edited_events"
        private const val EDITED_FILE = "events_edited.json"

        fun readFromFile(path: String): List<Event>? {
            return try {
                val file = File(path)
                if (!file.exists()) return null
                val jsonString = file.readText()
                val gson = Gson()
                val listType = object : TypeToken<List<Event>>() {}.type
                val events: List<Event> = gson.fromJson(jsonString, listType)
                if (events.isNotEmpty()) events else null
            } catch (_: Exception) {
                null
            }
        }
    }

    fun parseEvents(): List<Event> {
        val configLocale = context.resources.configuration.locales[0]
        DebugLogger.events("parseEvents", "Config locale: ${configLocale.language}")
        val edited = parseEditedEvents()
        if (edited != null) {
            DebugLogger.events("parseEvents", "Using EDITED events (${edited.size} events)")
            return edited
        }
        val imported = parseImportedEvents()
        if (imported != null) {
            DebugLogger.events("parseEvents", "Using IMPORTED events (${imported.size} events)")
            return imported
        }
        return parseDefaultEvents()
    }

    fun saveEvents(events: List<Event>): Boolean {
        return try {
            val dir = context.filesDir.resolve(EDITED_DIR)
            dir.mkdirs()
            val file = dir.resolve(EDITED_FILE)
            val gson = Gson()
            file.writeText(gson.toJson(events))
            true
        } catch (_: Exception) {
            false
        }
    }

    fun restoreEvent(date: String): Boolean {
        val edited = parseEditedEvents() ?: return false
        val filtered = edited.filter { it.date != date }
        return if (filtered.size < edited.size) {
            if (filtered.isEmpty()) {
                val dir = context.filesDir.resolve(EDITED_DIR)
                val file = dir.resolve(EDITED_FILE)
                file.delete()
            } else {
                saveEvents(filtered)
            }
            true
        } else {
            false
        }
    }

    fun getDefaultEvent(date: String): Event? {
        return parseDefaultEvents().find { it.date == date }
    }

    fun isEventModified(date: String): Boolean {
        val edited = parseEditedEvents() ?: return false
        val editedEvent = edited.find { it.date == date } ?: return false
        val defaultEvent = parseDefaultEvents().find { it.date == date } ?: return true
        return editedEvent != defaultEvent
    }

    fun getUserCreatedOrModifiedDates(): Set<String> {
        val edited = parseEditedEvents() ?: return emptySet()
        val defaultEvents = parseDefaultEvents()
        val defaultDates = defaultEvents.map { it.date }.toSet()
        return edited.filter { event ->
            event.date !in defaultDates || isEventModified(event.date)
        }.map { it.date }.toSet()
    }

    fun getEventsFilePath(): String {
        val editedFile = context.filesDir.resolve(EDITED_DIR).resolve(EDITED_FILE)
        if (editedFile.exists()) return editedFile.absolutePath
        val importedFile = context.filesDir.resolve("imported_events").resolve("events_imported.json")
        if (importedFile.exists()) return importedFile.absolutePath
        return ""
    }

    private fun parseEditedEvents(): List<Event>? {
        return try {
            val dir = context.filesDir.resolve(EDITED_DIR)
            val file = dir.resolve(EDITED_FILE)
            if (!file.exists()) return null
            readFromFile(file.absolutePath)
        } catch (_: Exception) {
            null
        }
    }

    private fun parseImportedEvents(): List<Event>? {
        return try {
            val dir = context.filesDir.resolve("imported_events")
            val file = dir.resolve("events_imported.json")
            if (!file.exists()) return null
            readFromFile(file.absolutePath)
        } catch (_: Exception) {
            null
        }
    }

    private fun parseDefaultEvents(): List<Event> {
        return try {
            val prefs = context.getSharedPreferences("dia_mundial_prefs", Context.MODE_PRIVATE)
            val savedLang = prefs.getString("app_language", "") ?: ""
            val lang = if (savedLang.isEmpty()) {
                context.resources.configuration.locales[0].language
            } else {
                savedLang
            }

            DebugLogger.events("parseDefaultEvents", "savedLang='$savedLang', resolvedLang='$lang', configLocale=${context.resources.configuration.locales[0].language}")

            val assetPath = "$lang/events.json"
            DebugLogger.events("parseDefaultEvents", "Trying asset: $assetPath")
            val inputStream = try {
                context.assets.open(assetPath)
            } catch (e: Exception) {
                DebugLogger.events("parseDefaultEvents", "Asset $assetPath not found, falling back to es/events.json")
                context.assets.open("es/events.json")
            }

            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val gson = Gson()
            val listType = object : TypeToken<List<Event>>() {}.type
            val events: List<Event> = gson.fromJson(jsonString, listType) ?: emptyList()
            DebugLogger.events("parseDefaultEvents", "Loaded ${events.size} events, first: '${events.firstOrNull()?.title ?: "N/A"}'")
            events
        } catch (e: Exception) {
            DebugLogger.error("Error loading default events", e)
            emptyList()
        }
    }
}
