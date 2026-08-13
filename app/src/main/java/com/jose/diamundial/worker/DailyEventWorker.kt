package com.jose.diamundial.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jose.diamundial.R
import com.jose.diamundial.data.EventParser
import com.jose.diamundial.domain.Event
import com.jose.diamundial.ui.NotificationHelper
import com.jose.diamundial.ui.SettingsManager
import com.jose.diamundial.ui.DebugLogger
import java.text.SimpleDateFormat
import java.util.*

class DailyEventWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            DebugLogger.init(applicationContext)
            DebugLogger.worker("doWork", "=== Worker START ===")
            val settingsManager = SettingsManager(applicationContext)
            if (!settingsManager.areNotificationsEnabled()) {
                DebugLogger.worker("doWork", "Notifications disabled, skipping")
                return Result.success()
            }

            val savedLang = settingsManager.getLanguage()
            DebugLogger.worker("doWork", "savedLang='$savedLang'")
            val locale = if (savedLang.isEmpty()) Locale.getDefault() else Locale.forLanguageTag(savedLang)
            DebugLogger.worker("doWork", "Using locale: ${locale.language}")
            val config = android.content.res.Configuration(applicationContext.resources.configuration)
            config.setLocale(locale)
            val localizedContext = applicationContext.createConfigurationContext(config)

            val parser = EventParser(localizedContext)
            val events = parser.parseEvents()
            DebugLogger.worker("doWork", "Loaded ${events.size} events")
            val today = Calendar.getInstance().time
            
            val sdf = SimpleDateFormat("MM-dd", Locale.getDefault())
            val todayDateStr = sdf.format(today)
            DebugLogger.worker("doWork", "Today: $todayDateStr")
            
            val eventForToday = events.find { it.date == todayDateStr }
            
            val notificationHelper = NotificationHelper(localizedContext)
            notificationHelper.createNotificationChannel()
            
            if (eventForToday != null) {
                DebugLogger.worker("doWork", "Found event '${eventForToday.title}' for $todayDateStr")
                notificationHelper.showNotification(
                    eventForToday.title,
                    eventForToday.description
                )
            } else {
                DebugLogger.worker("doWork", "No event for $todayDateStr, showing default")
                notificationHelper.showNotification(
                    localizedContext.getString(R.string.today_world_day),
                    localizedContext.getString(R.string.no_world_day_today)
                )
            }
            
            DebugLogger.worker("doWork", "=== Worker DONE ===")
            Result.success()
        } catch (e: Exception) {
            DebugLogger.error("Worker failed", e)
            Result.failure()
        }
    }
}
