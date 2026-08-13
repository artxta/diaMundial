package com.jose.diamundial.ui

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.util.Log
import java.util.Locale

class LocaleContextWrapper(base: Context) : ContextWrapper(base) {
    companion object {
        private const val TAG = "DiaMundial/Locale"

        fun wrap(context: Context, locale: Locale): Context {
            Log.d(TAG, "wrap: Wrapping context with locale=${locale.language}, display=${locale.displayName}")
            val config = Configuration(context.resources.configuration)
            config.setLocale(locale)
            config.setLayoutDirection(locale)

            val wrappedContext = context.createConfigurationContext(config)
            Log.d(TAG, "wrap: Created configuration context, verifying locale=${wrappedContext.resources.configuration.locales[0].language}")
            return wrappedContext
        }
    }
}
