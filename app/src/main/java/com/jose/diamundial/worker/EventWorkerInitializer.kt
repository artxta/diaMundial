package com.jose.diamundial.worker

import android.content.Context
import android.content.SharedPreferences
import androidx.work.*
import com.jose.diamundial.ui.DebugLogger
import java.util.concurrent.TimeUnit

class EventWorkerInitializer {
    companion object {
        private const val WORK_NAME = "daily_event_worker"
        private const val PREFS_NAME = "dia_mundial_prefs"
        private const val KEY_HOUR = "notification_hour"
        private const val KEY_MINUTE = "notification_minute"
        private const val DEFAULT_HOUR = 9
        private const val DEFAULT_MINUTE = 0

        fun getPrefs(context: Context): SharedPreferences {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }

        fun getNotificationTime(context: Context): Pair<Int, Int> {
            val prefs = getPrefs(context)
            val hour = prefs.getInt(KEY_HOUR, DEFAULT_HOUR)
            val minute = prefs.getInt(KEY_MINUTE, DEFAULT_MINUTE)
            return Pair(hour, minute)
        }

        fun setNotificationTime(context: Context, hour: Int, minute: Int) {
            DebugLogger.worker("setNotificationTime", "Setting time: ${hour}:${minute.toString().padStart(2, '0')}")
            val prefs = getPrefs(context)
            prefs.edit()
                .putInt(KEY_HOUR, hour)
                .putInt(KEY_MINUTE, minute)
                .apply()
            scheduleDailyWork(context)
        }

        fun scheduleDailyWork(context: Context) {
            DebugLogger.worker("scheduleDailyWork", "Scheduling daily work")
            val workManager = WorkManager.getInstance(context)
            workManager.cancelUniqueWork(WORK_NAME)

            val (hour, minute) = getNotificationTime(context)
            DebugLogger.worker("scheduleDailyWork", "Notification time: ${hour}:${minute.toString().padStart(2, '0')}")

            val now = java.util.Calendar.getInstance()
            val target = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, hour)
                set(java.util.Calendar.MINUTE, minute)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
                if (before(now)) {
                    add(java.util.Calendar.DAY_OF_MONTH, 1)
                }
            }

            val initialDelay = target.timeInMillis - now.timeInMillis
            DebugLogger.worker("scheduleDailyWork", "Initial delay: ${initialDelay / 1000 / 60} minutes")

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<DailyEventWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build()

            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
            DebugLogger.worker("scheduleDailyWork", "Work enqueued successfully")
        }

        fun cancelDailyWork(context: Context) {
            DebugLogger.worker("cancelDailyWork", "Cancelling daily work")
            val workManager = WorkManager.getInstance(context)
            workManager.cancelUniqueWork(WORK_NAME)
            DebugLogger.worker("cancelDailyWork", "Work cancelled")
        }
    }
}
