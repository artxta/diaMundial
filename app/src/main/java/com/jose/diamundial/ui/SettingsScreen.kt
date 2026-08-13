package com.jose.diamundial.ui

import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jose.diamundial.R
import com.jose.diamundial.data.EventParser
import com.jose.diamundial.domain.Category

class SettingsManager(private val context: Context) {
    companion object {
        const val PREFS_NAME = "dia_mundial_prefs"
        private const val KEY_HAS_IMPORTED_FILE = "has_imported_file"
        private const val KEY_IMPORTED_FILE_NAME = "imported_file_name"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_NOTIFICATION_IMPORTANCE = "notification_importance"
        private const val KEY_LANGUAGE = "app_language"
        private const val KEY_SKIP_WELCOME = "skip_welcome"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_SHOW_SCROLL_HINT = "show_scroll_hint"
        private const val KEY_FAVORITES = "favorite_dates"
        private const val KEY_COUNTRY_FILTER = "country_filter"
        private const val KEY_CATEGORY_FILTER = "category_filter"
        private const val KEY_FONT_SCALE = "font_scale"
        private const val KEY_APP_ICON = "app_icon"
        private const val KEY_DEBUG_ENABLED = "debug_enabled"
        private const val KEY_DEBUG_LOG_LEVEL = "debug_log_level"
        private const val IMPORTED_DIR = "imported_events"
        private const val IMPORTED_FILE = "events_imported.json"

        const val THEME_SYSTEM = 0
        const val THEME_LIGHT = 1
        const val THEME_DARK = 2

        const val FONT_SMALL = 0.85f
        const val FONT_MEDIUM = 1.0f
        const val FONT_LARGE = 1.15f
        const val FONT_EXTRA_LARGE = 1.3f

        const val ICON_ES = "es"
        const val ICON_EN = "en"

        const val LOG_OFF = 0
        const val LOG_BASIC = 1
        const val LOG_VERBOSE = 2

        val ICON_ALIAS_MAP = mapOf(
            ICON_ES to "com.jose.diamundial.LauncherES",
            ICON_EN to "com.jose.diamundial.LauncherEN"
        )

        fun getIconForLanguage(lang: String): String {
            val effectiveLang = if (lang.isEmpty()) {
                java.util.Locale.getDefault().language
            } else {
                lang
            }
            return if (effectiveLang == LANG_SPANISH) ICON_ES else ICON_EN
        }

        fun getLanguageDisplayName(lang: String, context: Context): String {
            val effectiveLang = if (lang.isEmpty()) {
                java.util.Locale.getDefault().language
            } else {
                lang
            }
            return when (effectiveLang) {
                LANG_SPANISH -> context.getString(R.string.lang_spanish)
                LANG_ENGLISH -> context.getString(R.string.lang_english)
                LANG_FRENCH -> context.getString(R.string.lang_french)
                LANG_CHINESE -> context.getString(R.string.lang_chinese)
                LANG_JAPANESE -> context.getString(R.string.lang_japanese)
                LANG_PORTUGUESE -> context.getString(R.string.lang_portuguese)
                LANG_CATALAN -> context.getString(R.string.lang_catalan)
                else -> context.getString(R.string.lang_spanish)
            }
        }

        const val LANG_SYSTEM = ""
        const val LANG_SPANISH = "es"
        const val LANG_ENGLISH = "en"
        const val LANG_FRENCH = "fr"
        const val LANG_CHINESE = "zh"
        const val LANG_JAPANESE = "ja"
        const val LANG_PORTUGUESE = "pt"
        const val LANG_CATALAN = "ca"
    }

    fun getPrefs() = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun hasImportedFile(): Boolean = getPrefs().getBoolean(KEY_HAS_IMPORTED_FILE, false)

    fun getImportedFileName(): String = getPrefs().getString(KEY_IMPORTED_FILE_NAME, "") ?: ""

    fun getThemeMode(): Int = getPrefs().getInt(KEY_THEME_MODE, THEME_SYSTEM)

    fun setThemeMode(mode: Int) {
        getPrefs().edit().putInt(KEY_THEME_MODE, mode).apply()
    }

    fun getLanguage(): String {
        val lang = getPrefs().getString(KEY_LANGUAGE, LANG_SYSTEM) ?: LANG_SYSTEM
        DebugLogger.verbose("SettingsManager", "getLanguage: '$lang'")
        return lang
    }

    fun setLanguage(lang: String) {
        DebugLogger.langChange("setLanguage", "Saving language: '$lang' to SharedPreferences")
        getPrefs().edit().putString(KEY_LANGUAGE, lang).apply()
        val saved = getPrefs().getString(KEY_LANGUAGE, LANG_SYSTEM) ?: LANG_SYSTEM
        DebugLogger.langChange("setLanguage", "Verified saved language: '$saved'")
    }

    fun shouldShowWelcome(): Boolean = !getPrefs().getBoolean(KEY_SKIP_WELCOME, false)

    fun setSkipWelcome(skip: Boolean) {
        getPrefs().edit().putBoolean(KEY_SKIP_WELCOME, skip).apply()
    }

    fun areNotificationsEnabled(): Boolean = getPrefs().getBoolean(KEY_NOTIFICATIONS_ENABLED, true)

    fun setNotificationsEnabled(enabled: Boolean) {
        getPrefs().edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    fun shouldShowScrollHint(): Boolean = getPrefs().getBoolean(KEY_SHOW_SCROLL_HINT, true)

    fun resetScrollHint() {
        getPrefs().edit().putBoolean(KEY_SHOW_SCROLL_HINT, true).apply()
    }

    fun setScrollHintShown() {
        getPrefs().edit().putBoolean(KEY_SHOW_SCROLL_HINT, false).apply()
    }

    fun getNotificationImportance(): Int =
        getPrefs().getInt(KEY_NOTIFICATION_IMPORTANCE, NotificationManager.IMPORTANCE_DEFAULT)

    fun setNotificationImportance(importance: Int) {
        getPrefs().edit().putInt(KEY_NOTIFICATION_IMPORTANCE, importance).apply()
    }

    fun getImportedFileUri(): Uri? {
        if (!hasImportedFile()) return null
        return try {
            val dir = context.filesDir.resolve(IMPORTED_DIR)
            val file = dir.resolve(IMPORTED_FILE)
            if (file.exists()) Uri.fromFile(file) else null
        } catch (_: Exception) {
            null
        }
    }

    fun saveImportedFile(uri: Uri, fileName: String): Boolean {
        return try {
            val dir = context.filesDir.resolve(IMPORTED_DIR)
            dir.mkdirs()
            val destFile = dir.resolve(IMPORTED_FILE)

            context.contentResolver.openInputStream(uri)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val testList = EventParser.readFromFile(destFile.absolutePath)
            if (testList != null) {
                getPrefs().edit()
                    .putBoolean(KEY_HAS_IMPORTED_FILE, true)
                    .putString(KEY_IMPORTED_FILE_NAME, fileName)
                    .apply()
                true
            } else {
                destFile.delete()
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    fun deleteImportedFile(): Boolean {
        return try {
            val dir = context.filesDir.resolve(IMPORTED_DIR)
            val file = dir.resolve(IMPORTED_FILE)
            file.delete()
            getPrefs().edit()
                .putBoolean(KEY_HAS_IMPORTED_FILE, false)
                .putString(KEY_IMPORTED_FILE_NAME, "")
                .apply()
            true
        } catch (_: Exception) {
            false
        }
    }

    fun getFavorites(): Set<String> {
        return getPrefs().getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    fun isFavorite(date: String): Boolean {
        return date in getFavorites()
    }

    fun toggleFavorite(date: String) {
        val current = getFavorites().toMutableSet()
        if (date in current) current.remove(date) else current.add(date)
        getPrefs().edit().putStringSet(KEY_FAVORITES, current).apply()
    }

    fun getCountryFilter(): Set<String> {
        val saved = getPrefs().getStringSet(KEY_COUNTRY_FILTER, null)
        return saved ?: emptySet()
    }

    fun setCountryFilter(countries: Set<String>) {
        getPrefs().edit().putStringSet(KEY_COUNTRY_FILTER, countries).apply()
    }

    fun getCategoryFilter(): Set<String> {
        val saved = getPrefs().getStringSet(KEY_CATEGORY_FILTER, null)
        return saved ?: emptySet()
    }

    fun setCategoryFilter(categories: Set<String>) {
        getPrefs().edit().putStringSet(KEY_CATEGORY_FILTER, categories).apply()
    }

    fun getFontScale(): Float {
        return getPrefs().getFloat(KEY_FONT_SCALE, FONT_MEDIUM)
    }

    fun setFontScale(scale: Float) {
        getPrefs().edit().putFloat(KEY_FONT_SCALE, scale).apply()
    }

    fun getAppIcon(): String {
        return getPrefs().getString(KEY_APP_ICON, ICON_ES) ?: ICON_ES
    }

    fun setAppIcon(icon: String) {
        DebugLogger.icon("setAppIcon", "Saving icon: '$icon' to SharedPreferences")
        getPrefs().edit().putString(KEY_APP_ICON, icon).apply()
    }

    fun isDebugEnabled(): Boolean = getPrefs().getBoolean(KEY_DEBUG_ENABLED, false)

    fun setDebugEnabled(enabled: Boolean) {
        getPrefs().edit().putBoolean(KEY_DEBUG_ENABLED, enabled).apply()
    }

    fun getDebugLogLevel(): Int = getPrefs().getInt(KEY_DEBUG_LOG_LEVEL, LOG_VERBOSE)

    fun setDebugLogLevel(level: Int) {
        getPrefs().edit().putInt(KEY_DEBUG_LOG_LEVEL, level).apply()
    }
}

data class ThemeOption(val labelRes: Int, val mode: Int)
data class NotificationOption(val labelRes: Int, val importance: Int, val descriptionRes: Int)
data class LanguageOption(val labelRes: Int, val lang: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onImportComplete: () -> Unit = {},
    onThemeChanged: (Int) -> Unit = {},
    onLanguageChanged: () -> Unit = {},
    onFontScaleChanged: (Float) -> Unit = {}
) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }

    var hasImported by remember { mutableStateOf(settingsManager.hasImportedFile()) }
    var importedFileName by remember { mutableStateOf(settingsManager.getImportedFileName()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }

    val themeOptions = listOf(
        ThemeOption(R.string.theme_system, SettingsManager.THEME_SYSTEM),
        ThemeOption(R.string.theme_light, SettingsManager.THEME_LIGHT),
        ThemeOption(R.string.theme_dark, SettingsManager.THEME_DARK)
    )

    val languageOptions = listOf(
        LanguageOption(R.string.lang_system, SettingsManager.LANG_SYSTEM),
        LanguageOption(R.string.lang_spanish, SettingsManager.LANG_SPANISH),
        LanguageOption(R.string.lang_english, SettingsManager.LANG_ENGLISH),
        LanguageOption(R.string.lang_french, SettingsManager.LANG_FRENCH),
        LanguageOption(R.string.lang_chinese, SettingsManager.LANG_CHINESE),
        LanguageOption(R.string.lang_japanese, SettingsManager.LANG_JAPANESE),
        LanguageOption(R.string.lang_portuguese, SettingsManager.LANG_PORTUGUESE),
        LanguageOption(R.string.lang_catalan, SettingsManager.LANG_CATALAN)
    )

    val notificationOptions = listOf(
        NotificationOption(R.string.notif_none, NotificationManager.IMPORTANCE_MIN, R.string.notif_none_desc),
        NotificationOption(R.string.notif_silent, NotificationManager.IMPORTANCE_LOW, R.string.notif_silent_desc),
        NotificationOption(R.string.notif_sound, NotificationManager.IMPORTANCE_DEFAULT, R.string.notif_sound_desc)
    )

    var selectedThemeIndex by remember {
        mutableIntStateOf(
            themeOptions.indexOfFirst { it.mode == settingsManager.getThemeMode() }.coerceAtLeast(0)
        )
    }
    var selectedLanguageIndex by remember {
        mutableIntStateOf(
            languageOptions.indexOfFirst { it.lang == settingsManager.getLanguage() }.coerceAtLeast(0)
        )
    }
    var selectedNotificationIndex by remember {
        mutableIntStateOf(
            notificationOptions.indexOfFirst { it.importance == settingsManager.getNotificationImportance() }.coerceAtLeast(1)
        )
    }

    val fontScaleOptions = listOf(
        SettingsManager.FONT_SMALL to R.string.font_small,
        SettingsManager.FONT_MEDIUM to R.string.font_medium,
        SettingsManager.FONT_LARGE to R.string.font_large,
        SettingsManager.FONT_EXTRA_LARGE to R.string.font_extra_large
    )
    var selectedFontScale by remember { mutableFloatStateOf(settingsManager.getFontScale()) }

    val allCategoryKeys = Category.entries.map { it.key }.toSet()
    var selectedCategoryFilters by remember { mutableStateOf(settingsManager.getCategoryFilter()) }
    val categoryOptions = listOf(
        Category.HEALTH to R.string.category_health,
        Category.EDUCATION to R.string.category_education,
        Category.ENVIRONMENT to R.string.category_environment,
        Category.CULTURE to R.string.category_culture,
        Category.SCIENCE_TECH to R.string.category_science_tech
    )

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "events.json"
            val success = settingsManager.saveImportedFile(uri, fileName)
            if (success) {
                hasImported = true
                importedFileName = settingsManager.getImportedFileName()
                resultMessage = context.getString(R.string.import_success)
            } else {
                resultMessage = context.getString(R.string.import_error)
            }
            showResultDialog = true
            onImportComplete()
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val parser = EventParser(context)
                val events = parser.parseEvents()
                val gson = GsonBuilder().setPrettyPrinting().create()
                val json = gson.toJson(events)
                context.contentResolver.openOutputStream(uri)?.use { output ->
                    output.write(json.toByteArray())
                }
                Toast.makeText(context, context.getString(R.string.export_success), Toast.LENGTH_SHORT).show()
            } catch (_: Exception) {
                Toast.makeText(context, context.getString(R.string.export_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // --- Apariencia ---
        Text(
            text = stringResource(R.string.appearance),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.app_theme),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                themeOptions.forEachIndexed { index, option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                selectedThemeIndex = index
                                settingsManager.setThemeMode(option.mode)
                                onThemeChanged(option.mode)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedThemeIndex == index,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(option.labelRes),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // --- Tamaño de fuente ---
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.font_size),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                fontScaleOptions.forEach { (scale, labelRes) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                selectedFontScale = scale
                                settingsManager.setFontScale(scale)
                                onFontScaleChanged(scale)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedFontScale == scale,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(labelRes),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Aa",
                            fontSize = (14.sp * scale),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // --- Idioma ---
        Text(
            text = stringResource(R.string.language),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.language_title),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                languageOptions.forEachIndexed { index, option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                if (index != selectedLanguageIndex) {
                                    selectedLanguageIndex = index
                                    settingsManager.setLanguage(option.lang)
                                    settingsManager.setAppIcon(SettingsManager.ICON_EN)
                                    onLanguageChanged()
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedLanguageIndex == index,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(option.labelRes),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // --- Notificaciones ---
        Text(
            text = stringResource(R.string.notifications),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.notifications_mode),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = stringResource(R.string.notifications_mode_desc),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                notificationOptions.forEachIndexed { index, option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                selectedNotificationIndex = index
                                settingsManager.setNotificationImportance(option.importance)
                                NotificationHelper(context).createNotificationChannel()
                            },
                        verticalAlignment = Alignment.Top
                    ) {
                        RadioButton(
                            selected = selectedNotificationIndex == index,
                            onClick = null,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = stringResource(option.labelRes),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = stringResource(option.descriptionRes),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // --- Categorías ---
        Text(
            text = stringResource(R.string.categories),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.filter_by_category),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = stringResource(R.string.notifications_mode_desc),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedCategoryFilters = if (selectedCategoryFilters == allCategoryKeys || selectedCategoryFilters.isEmpty()) {
                                emptySet()
                            } else {
                                allCategoryKeys
                            }
                            settingsManager.setCategoryFilter(selectedCategoryFilters)
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedCategoryFilters.isEmpty() || selectedCategoryFilters == allCategoryKeys,
                        onCheckedChange = { checked ->
                            selectedCategoryFilters = if (checked) emptySet() else allCategoryKeys
                            settingsManager.setCategoryFilter(selectedCategoryFilters)
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.all_categories),
                        fontWeight = FontWeight.Medium
                    )
                }

                HorizontalDivider()

                categoryOptions.forEach { (category, labelRes) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedCategoryFilters = if (category.key in selectedCategoryFilters) {
                                    selectedCategoryFilters - category.key
                                } else {
                                    selectedCategoryFilters + category.key
                                }
                                settingsManager.setCategoryFilter(selectedCategoryFilters)
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = category.key in selectedCategoryFilters,
                            onCheckedChange = { checked ->
                                selectedCategoryFilters = if (checked) {
                                    selectedCategoryFilters + category.key
                                } else {
                                    selectedCategoryFilters - category.key
                                }
                                settingsManager.setCategoryFilter(selectedCategoryFilters)
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(labelRes),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // --- Archivo de Días Mundiales ---
        Text(
            text = stringResource(R.string.data),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.world_day_file),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = stringResource(R.string.filter_by_category),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (hasImported) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.active_file),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = importedFileName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { filePickerLauncher.launch(arrayOf("application/json")) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.change))
                        }

                        OutlinedButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(stringResource(R.string.restore))
                        }
                    }
                } else {
                    Button(
                        onClick = { filePickerLauncher.launch(arrayOf("application/json")) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.import_json))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.export_desc),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedButton(
                    onClick = { exportLauncher.launch("dias_mundiales.json") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.export_json))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.format_hint),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // --- Información del desarrollador ---
        var devTapCount by remember { mutableIntStateOf(0) }
        var showDebugSection by remember { mutableStateOf(settingsManager.isDebugEnabled()) }

        Text(
            text = stringResource(R.string.developer_info),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        devTapCount++
                        if (devTapCount == 3) {
                            Toast.makeText(context, context.getString(R.string.debug_hint), Toast.LENGTH_SHORT).show()
                        } else if (devTapCount >= 5) {
                            devTapCount = 0
                            showDebugSection = !showDebugSection
                            settingsManager.setDebugEnabled(showDebugSection)
                            val msg = if (showDebugSection) context.getString(R.string.debug_enabled) else context.getString(R.string.debug_disabled)
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
            ) {
                Text(
                    text = stringResource(R.string.developer_name),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = stringResource(R.string.app_version),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "https://github.com/artxta/diaMundial",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse("https://github.com/artxta/diaMundial")
                        )
                        context.startActivity(intent)
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.play_store_on),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "https://play.google.com/store/apps/details?id=com.jose.diamundial",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse("https://play.google.com/store/apps/details?id=com.jose.diamundial")
                        )
                        context.startActivity(intent)
                    }
                )
            }
        }

        // --- Depuración (Easter egg) ---
        if (showDebugSection) {
            Text(
                text = stringResource(R.string.debug),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.debug_log_level),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Text(
                        text = stringResource(R.string.debug_log_level_desc),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val logLevelOptions = listOf(
                        SettingsManager.LOG_OFF to R.string.debug_log_off,
                        SettingsManager.LOG_BASIC to R.string.debug_log_basic,
                        SettingsManager.LOG_VERBOSE to R.string.debug_log_verbose
                    )
                    var selectedLogLevel by remember {
                        mutableIntStateOf(settingsManager.getDebugLogLevel())
                    }

                    logLevelOptions.forEach { (level, labelRes) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    selectedLogLevel = level
                                    settingsManager.setDebugLogLevel(level)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedLogLevel == level,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(labelRes),
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            showDebugSection = false
                            settingsManager.setDebugEnabled(false)
                            devTapCount = 0
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.debug_hide))
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.restore_defaults)) },
            text = { Text(stringResource(R.string.restore_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    settingsManager.deleteImportedFile()
                    hasImported = false
                    importedFileName = ""
                    showDeleteDialog = false
                    onImportComplete()
                }) {
                    Text(stringResource(R.string.restore))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            title = { Text(stringResource(R.string.result)) },
            text = { Text(resultMessage) },
            confirmButton = {
                TextButton(onClick = { showResultDialog = false }) {
                    Text(stringResource(R.string.accept))
                }
            }
        )
    }
}
