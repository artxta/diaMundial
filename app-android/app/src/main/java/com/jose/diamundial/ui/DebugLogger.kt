package com.jose.diamundial.ui

import android.content.Context
import android.util.Log

object DebugLogger {
    private const val TAG = "DiaMundial"

    private var logLevel: Int = SettingsManager.LOG_VERBOSE
    private var initialized = false

    fun init(context: Context) {
        val sm = SettingsManager(context)
        val enabled = sm.isDebugEnabled()
        logLevel = if (enabled) sm.getDebugLogLevel() else SettingsManager.LOG_OFF
        initialized = true
        Log.d(TAG, "DebugLogger initialized: enabled=$enabled, logLevel=$logLevel (${logLevelName()})")
    }

    private fun logLevelName(): String = when (logLevel) {
        SettingsManager.LOG_OFF -> "OFF"
        SettingsManager.LOG_BASIC -> "BASIC"
        SettingsManager.LOG_VERBOSE -> "VERBOSE"
        else -> "UNKNOWN($logLevel)"
    }

    fun basic(message: String) {
        if (logLevel >= SettingsManager.LOG_BASIC) {
            Log.d(TAG, message)
        }
    }

    fun verbose(tag: String, message: String) {
        if (logLevel >= SettingsManager.LOG_VERBOSE) {
            Log.v("$TAG/$tag", message)
        }
    }

    fun warn(tag: String, message: String) {
        Log.w("$TAG/$tag", message)
    }

    fun error(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(TAG, message, throwable)
        } else {
            Log.e(TAG, message)
        }
    }

    fun langChange(step: String, details: String) {
        Log.d("$TAG/Lang", "[$step] $details")
    }

    fun lifecycle(step: String, details: String) {
        Log.d("$TAG/Lifecycle", "[$step] $details")
    }

    fun icon(step: String, details: String) {
        Log.d("$TAG/Icon", "[$step] $details")
    }

    fun worker(step: String, details: String) {
        Log.d("$TAG/Worker", "[$step] $details")
    }

    fun events(step: String, details: String) {
        Log.d("$TAG/Events", "[$step] $details")
    }
}
