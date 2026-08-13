package com.jose.diamundial

import android.Manifest
import android.content.Intent
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jose.diamundial.data.EventParser
import com.jose.diamundial.domain.Event
import com.jose.diamundial.domain.Category
import com.jose.diamundial.ui.SettingsManager
import com.jose.diamundial.ui.SettingsScreen
import com.jose.diamundial.ui.DebugLogger
import com.jose.diamundial.ui.LocaleContextWrapper
import com.jose.diamundial.ui.theme.DiaMundialTheme
import com.jose.diamundial.worker.EventWorkerInitializer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun formatDateEs(context: android.content.Context, dateStr: String): String {
    val parts = dateStr.split("-")
    val month = parts[0].toInt()
    val day = parts[1].toInt()
    val months = context.resources.getStringArray(R.array.month_names)
    val locale = context.resources.configuration.locales[0]
    val connector = if (locale.language == "es") "de" else "/"
    return "$day $connector ${months[month - 1]}"
}

fun applyIconSwitch(context: android.content.Context, iconKey: String) {
    val pm = context.packageManager
    DebugLogger.icon("applyIconSwitch", "iconKey='$iconKey'")

    val aliases = listOf(
        "com.jose.diamundial.LauncherES",
        "com.jose.diamundial.LauncherEN"
    )

    aliases.forEach { alias ->
        val aliasComponent = android.content.ComponentName(context, alias)
        val state = if (SettingsManager.ICON_ALIAS_MAP[iconKey] == alias) {
            DebugLogger.icon("applyIconSwitch", "ENABLING alias: $alias")
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } else {
            DebugLogger.icon("applyIconSwitch", "DISABLING alias: $alias")
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }
        pm.setComponentEnabledSetting(
            aliasComponent,
            state,
            PackageManager.DONT_KILL_APP
        )
    }

    val enabledAliases = aliases.filter { alias ->
        val state = pm.getComponentEnabledSetting(android.content.ComponentName(context, alias))
        state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
    }
    DebugLogger.icon("applyIconSwitch", "Enabled aliases after change: $enabledAliases")
}

class MainActivity : ComponentActivity() {
    private var currentAppliedLang: String = ""

    private var currentLocale: Locale? = null

    override fun attachBaseContext(newBase: Context?) {
        Log.d("DiaMundial/Lifecycle", "attachBaseContext: Starting locale initialization")
        val prefs = newBase?.getSharedPreferences("dia_mundial_prefs", Context.MODE_PRIVATE)
        val savedLang = prefs?.getString("app_language", "") ?: ""
        Log.d("DiaMundial/Lifecycle", "attachBaseContext: savedLang='$savedLang'")

        val contextToUse = if (savedLang.isNotEmpty()) {
            val locale = Locale.forLanguageTag(savedLang)
            Log.d("DiaMundial/Lifecycle", "attachBaseContext: Wrapping context with locale=${locale.language}")
            currentLocale = locale
            currentAppliedLang = savedLang
            LocaleContextWrapper.wrap(newBase!!, locale)
        } else {
            Log.d("DiaMundial/Lifecycle", "attachBaseContext: No saved language, using system default")
            currentAppliedLang = ""
            currentLocale = null
            newBase!!
        }
        super.attachBaseContext(contextToUse)
        Log.d("DiaMundial/Lifecycle", "attachBaseContext: DONE, currentAppliedLang='$currentAppliedLang'")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("DiaMundial/Lifecycle", "onCreate: START")
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("dia_mundial_prefs", Context.MODE_PRIVATE)
        val savedLang = prefs.getString("app_language", "") ?: ""
        val currentLocale = resources.configuration.locales[0]
        Log.d("DiaMundial/Lifecycle", "onCreate: savedLang='$savedLang', configLocale=${currentLocale.language}, appliedLang='$currentAppliedLang'")

        enableEdgeToEdge()
        EventWorkerInitializer.scheduleDailyWork(this)

        com.jose.diamundial.update.UpdateChecker.checkForUpdate(this)

        DebugLogger.init(applicationContext)
        val settingsManager = SettingsManager(this)
        val initialTheme = settingsManager.getThemeMode()
        DebugLogger.basic("onCreate: locale=${resources.configuration.locales[0].language}, theme=$initialTheme, debug=${settingsManager.isDebugEnabled()}")

        val currentIcon = settingsManager.getAppIcon()
        applyIconSwitch(this, currentIcon)

        val savedFontScale = settingsManager.getFontScale()
        @Suppress("DEPRECATION")
        resources.configuration.fontScale = savedFontScale

        setContent {
            var themeMode by remember { mutableIntStateOf(initialTheme) }

            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { _ -> }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val hasPermission = checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                    if (!hasPermission) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            val onLanguageChanged: () -> Unit = {
                val lang = settingsManager.getLanguage()
                val newIcon = SettingsManager.getIconForLanguage(lang)
                Log.d("DiaMundial/Lang", "=== onLanguageChanged FIRED ===")
                Log.d("DiaMundial/Lang", "Language from prefs: '${lang}' (empty=system default)")
                Log.d("DiaMundial/Lang", "Icon to apply: '${newIcon}'")
                DebugLogger.basic("onLanguageChanged: lang='$lang', icon='$newIcon'")
                applyIconSwitch(this@MainActivity, newIcon)
                Log.d("DiaMundial/Lang", "Icon switch applied, recreating activity")
                recreate()
            }

            val onFontScaleChanged: (Float) -> Unit = { _ ->
                recreate()
            }

            DiaMundialTheme(themeMode = themeMode) {
                MainNavigation(
                    onThemeChanged = { themeMode = it },
                    onLanguageChanged = onLanguageChanged,
                    onFontScaleChanged = onFontScaleChanged
                )
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}

enum class Screen { HOME, FAVORITES, SETTINGS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(onThemeChanged: (Int) -> Unit = {}, onLanguageChanged: () -> Unit = {}, onFontScaleChanged: (Float) -> Unit = {}) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var eventsVersion by remember { mutableIntStateOf(0) }

    val parser = remember { EventParser(context) }
    val events = remember { parser.parseEvents() }
    val today = remember {
        SimpleDateFormat("MM-dd", Locale.getDefault()).format(Date())
    }
    val todayEvent = remember(events) { events.find { it.date == today } }

    var showWelcomeDialog by remember { mutableStateOf(settingsManager.shouldShowWelcome()) }
    var skipWelcomeChecked by remember { mutableStateOf(false) }

    BackHandler(enabled = currentScreen == Screen.SETTINGS) {
        currentScreen = Screen.HOME
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text(stringResource(R.string.home)) },
                    selected = currentScreen == Screen.HOME,
                    onClick = { currentScreen = Screen.HOME }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text(stringResource(R.string.favorites)) },
                    selected = currentScreen == Screen.FAVORITES,
                    onClick = { currentScreen = Screen.FAVORITES }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text(stringResource(R.string.settings)) },
                    selected = currentScreen == Screen.SETTINGS,
                    onClick = { currentScreen = Screen.SETTINGS }
                )
            }
        }
    ) { innerPadding ->
        when (currentScreen) {
            Screen.HOME -> HomeScreen(
                modifier = Modifier.padding(innerPadding),
                eventsVersion = eventsVersion,
                onEventsChanged = { eventsVersion++ }
            )
            Screen.FAVORITES -> FavoritesScreen(
                modifier = Modifier.padding(innerPadding),
                eventsVersion = eventsVersion,
                onEventsChanged = { eventsVersion++ }
            )
            Screen.SETTINGS -> SettingsScreen(
                modifier = Modifier.padding(innerPadding),
                onImportComplete = { eventsVersion++ },
                onThemeChanged = onThemeChanged,
                onLanguageChanged = onLanguageChanged,
                onFontScaleChanged = onFontScaleChanged
            )
        }
    }

    if (showWelcomeDialog) {
        AlertDialog(
            onDismissRequest = {
                if (skipWelcomeChecked) {
                    settingsManager.setSkipWelcome(true)
                }
                showWelcomeDialog = false
            },
            title = {
                Text(
                    text = stringResource(R.string.welcome_title),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    if (todayEvent != null) {
                        Text(
                            text = stringResource(R.string.welcome_message_today, todayEvent.title),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.welcome_message_none),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                    Text(
                        text = stringResource(R.string.welcome_description),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { skipWelcomeChecked = !skipWelcomeChecked },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = skipWelcomeChecked,
                            onCheckedChange = { skipWelcomeChecked = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.welcome_skip),
                            fontSize = 13.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (skipWelcomeChecked) {
                        settingsManager.setSkipWelcome(true)
                    }
                    showWelcomeDialog = false
                }) {
                    Text(stringResource(R.string.accept))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier, eventsVersion: Int = 0, onEventsChanged: () -> Unit = {}) {
    val context = LocalContext.current
    val parser = remember(eventsVersion) { EventParser(context) }
    val allEvents = remember(eventsVersion) { parser.parseEvents() }

    val settingsManager = remember { SettingsManager(context) }
    val selectedCategoryFilters = remember { settingsManager.getCategoryFilter() }
    val events = remember(allEvents, selectedCategoryFilters) {
        if (selectedCategoryFilters.isEmpty()) allEvents
        else allEvents.filter { event ->
            Category.classify(event).key in selectedCategoryFilters
        }
    }

    val today = remember {
        SimpleDateFormat("MM-dd", Locale.getDefault()).format(Date())
    }
    val todayEvents = remember(events) { events.filter { it.date == today } }

    val (savedHour, savedMinute) = remember { EventWorkerInitializer.getNotificationTime(context) }
    var hour by remember { mutableIntStateOf(savedHour) }
    var minute by remember { mutableIntStateOf(savedMinute) }
    var showTimePicker by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(SettingsManager(context).areNotificationsEnabled()) }

    val timePickerState = rememberTimePickerState(
        initialHour = hour,
        initialMinute = minute,
        is24Hour = true
    )

    val allCountriesText = stringResource(R.string.all_countries)
    var selectedCountryFilters by remember { mutableStateOf(settingsManager.getCountryFilter()) }
    val availableCountries = remember(events) {
        events.map { it.country }.distinct().sorted()
    }
    var showCountryFilterDialog by remember { mutableStateOf(false) }

    var showScrollHint by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    LaunchedEffect(scrollState.value) {
        if (scrollState.value > 50) {
            showScrollHint = false
        } else if (scrollState.value == 0) {
            showScrollHint = true
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.today_world_day),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (todayEvents.isNotEmpty()) {
                if (todayEvents.size == 1) {
                    EventCard(event = todayEvents[0], context = context, onFavoriteChanged = onEventsChanged)
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(R.string.multiple_events_today, todayEvents.size),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            todayEvents.forEach { event ->
                                var isFav by remember(event.date) { mutableStateOf(settingsManager.isFavorite(event.date)) }
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 6.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = event.title,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(
                                                onClick = {
                                                    settingsManager.toggleFavorite(event.date)
                                                    isFav = !isFav
                                                    onEventsChanged()
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                    contentDescription = stringResource(R.string.add_favorite),
                                                    tint = if (isFav) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        Text(
                                            text = event.description,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2
                                        )
                                        Text(
                                            text = stringResource(R.string.country_label_format, event.country),
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.no_world_day_today),
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }
            }

            CalendarSection(
                events = events,
                availableCountries = availableCountries,
                selectedCountryFilters = selectedCountryFilters,
                onCountryFiltersChanged = { selectedCountryFilters = it },
                onShowFilterDialog = { showCountryFilterDialog = true },
                onEventsChanged = onEventsChanged
            )

            Spacer(modifier = Modifier.height(16.dp))

            NotificationSettings(
                hour = hour,
                minute = minute,
                notificationsEnabled = notificationsEnabled,
                onNotificationsEnabledChanged = { notificationsEnabled = it },
                onShowTimePicker = { showTimePicker = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.total_days_format, events.size),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (showScrollHint) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                ScrollHintIndicator(
                    onDismiss = {
                        showScrollHint = false
                    }
                )
            }
        }
    }

    if (showCountryFilterDialog) {
        val tempSelected = remember { mutableStateOf(selectedCountryFilters) }
        AlertDialog(
            onDismissRequest = { showCountryFilterDialog = false },
            title = { Text(stringResource(R.string.filter_by_country), fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                tempSelected.value = if (tempSelected.value == availableCountries.toSet()) emptySet() else availableCountries.toSet()
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = tempSelected.value == availableCountries.toSet() && availableCountries.isNotEmpty(),
                            onCheckedChange = {
                                tempSelected.value = if (it) availableCountries.toSet() else emptySet()
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.all_countries),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    HorizontalDivider()
                    availableCountries.forEach { country ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    tempSelected.value = if (country in tempSelected.value) tempSelected.value - country else tempSelected.value + country
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = country in tempSelected.value,
                                onCheckedChange = { checked ->
                                    tempSelected.value = if (checked) tempSelected.value + country else tempSelected.value - country
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = country)
                        }
                    }
                    HorizontalDivider()
                    val customDaysText = stringResource(R.string.custom_days)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                tempSelected.value = if (customDaysText in tempSelected.value) tempSelected.value - customDaysText else tempSelected.value + customDaysText
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = customDaysText in tempSelected.value,
                            onCheckedChange = { checked ->
                                tempSelected.value = if (checked) tempSelected.value + customDaysText else tempSelected.value - customDaysText
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = customDaysText,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = stringResource(R.string.custom_days_desc),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedCountryFilters = tempSelected.value
                    settingsManager.setCountryFilter(selectedCountryFilters)
                    showCountryFilterDialog = false
                }) {
                    Text(stringResource(R.string.accept))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCountryFilterDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text(stringResource(R.string.select_time)) },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    hour = timePickerState.hour
                    minute = timePickerState.minute
                    EventWorkerInitializer.setNotificationTime(context, hour, minute)
                    showTimePicker = false
                }) {
                    Text(stringResource(R.string.accept))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun EventCard(event: Event, context: android.content.Context, onFavoriteChanged: () -> Unit = {}) {
    val settingsManager = remember { SettingsManager(context) }
    var isFavorite by remember(event.date) { mutableStateOf(settingsManager.isFavorite(event.date)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        settingsManager.toggleFavorite(event.date)
                        isFavorite = !isFavorite
                        onFavoriteChanged()
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(R.string.add_favorite),
                        tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = formatDateEs(context, event.date),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = event.description,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = stringResource(R.string.country_label_format, event.country),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, event.title)
                        type = "text/plain"
                        setPackage("com.whatsapp")
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    try {
                        context.startActivity(shareIntent)
                    } catch (_: Exception) {
                        val fallback = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, event.title)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(fallback, context.getString(R.string.share_with)))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.share_whatsapp))
            }
        }
    }
}

@Composable
fun NotificationSettings(
    hour: Int,
    minute: Int,
    notificationsEnabled: Boolean,
    onNotificationsEnabledChanged: (Boolean) -> Unit,
    onShowTimePicker: () -> Unit
) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }

    Text(
        text = stringResource(R.string.notifications),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onShowTimePicker() }
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.notification_time_format, hour, minute),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onShowTimePicker() }
                    )
                    Text(
                        text = stringResource(R.string.tap_to_change),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                    Text(
                        text = if (notificationsEnabled) stringResource(R.string.notifications_enabled) else stringResource(R.string.notifications_disabled),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = {
                        onNotificationsEnabledChanged(it)
                        settingsManager.setNotificationsEnabled(it)
                        if (!it) {
                            EventWorkerInitializer.cancelDailyWork(context)
                        } else {
                            EventWorkerInitializer.scheduleDailyWork(context)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (notificationsEnabled)
                    stringResource(R.string.notification_daily_enabled)
                else
                    stringResource(R.string.notification_daily_disabled),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarSection(
    events: List<Event>,
    availableCountries: List<String> = listOf("Global", "España"),
    selectedCountryFilters: Set<String> = emptySet(),
    onCountryFiltersChanged: (Set<String>) -> Unit = {},
    onShowFilterDialog: () -> Unit = {},
    onEventsChanged: () -> Unit = {}
) {
    val context = LocalContext.current
    val monthNames = stringArrayResource(R.array.month_names)
    var selectedMonth by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var selectedYear by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var selectedDayEvent by remember { mutableStateOf<Event?>(null) }
    var selectedDayEvents by remember { mutableStateOf<List<Event>?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var editTitle by remember { mutableStateOf("") }
    var editDescription by remember { mutableStateOf("") }
    var selectedEventIsModified by remember { mutableStateOf(false) }

    var showCreateDialog by remember { mutableStateOf(false) }
    var createDay by remember { mutableIntStateOf(0) }
    var createMonth by remember { mutableIntStateOf(0) }
    var createYear by remember { mutableIntStateOf(0) }
    var createTitle by remember { mutableStateOf("") }
    var createDescription by remember { mutableStateOf("") }
    var createCountry by remember { mutableStateOf("") }

    val defaultCountryText = stringResource(R.string.default_country)

    val dayParser = remember { EventParser(context) }
    val settingsManager = remember { SettingsManager(context) }

    val customDaysText = stringResource(R.string.custom_days)
    val filteredEvents = remember(events, selectedCountryFilters) {
        if (selectedCountryFilters.isEmpty()) events
        else {
            val userDates = dayParser.getUserCreatedOrModifiedDates()
            events.filter { event ->
                val matchesCountry = event.country in selectedCountryFilters
                val matchesCustom = customDaysText in selectedCountryFilters && event.date in userDates
                matchesCountry || matchesCustom
            }
        }
    }

    val todayCal = remember { Calendar.getInstance() }
    val isCurrentMonth = remember(selectedMonth, selectedYear) {
        todayCal.get(Calendar.MONTH) == selectedMonth && todayCal.get(Calendar.YEAR) == selectedYear
    }
    val todayDay = remember { todayCal.get(Calendar.DAY_OF_MONTH) }
    val currentTodayMonth = remember { todayCal.get(Calendar.MONTH) }
    val currentTodayYear = remember { todayCal.get(Calendar.YEAR) }

    val eventsByMonthDay = remember(filteredEvents, selectedMonth, selectedYear) {
        val cal = Calendar.getInstance()
        filteredEvents.groupBy {
            cal.set(selectedYear, selectedMonth, 1)
            val parts = it.date.split("-")
            "%02d-%02d".format(parts[0].toInt(), parts[1].toInt())
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = stringResource(R.string.calendar),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val filterText = if (selectedCountryFilters.isEmpty()) stringResource(R.string.all_countries)
                else selectedCountryFilters.joinToString(", ")

            Box(modifier = Modifier
                .fillMaxWidth()
                .clickable { onShowFilterDialog() }
                .padding(bottom = 8.dp)
            ) {
                OutlinedTextField(
                    value = filterText,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text(stringResource(R.string.filter_by_country)) },
                    trailingIcon = {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (selectedMonth == Calendar.JANUARY) {
                        selectedMonth = Calendar.DECEMBER
                        selectedYear--
                    } else {
                        selectedMonth--
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = stringResource(R.string.prev_month))
                }

                Text(
                    text = "${monthNames[selectedMonth]} $selectedYear",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isCurrentMonth) {
                        OutlinedButton(
                            onClick = {
                                selectedMonth = currentTodayMonth
                                selectedYear = currentTodayYear
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(stringResource(R.string.go_to_today), fontSize = 12.sp)
                        }
                    }

                    IconButton(onClick = {
                        if (selectedMonth == Calendar.DECEMBER) {
                            selectedMonth = Calendar.JANUARY
                            selectedYear++
                        } else {
                            selectedMonth++
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = stringResource(R.string.next_month))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val dayNames = listOf(
                stringResource(R.string.day_mon),
                stringResource(R.string.day_tue),
                stringResource(R.string.day_wed),
                stringResource(R.string.day_thu),
                stringResource(R.string.day_fri),
                stringResource(R.string.day_sat),
                stringResource(R.string.day_sun)
            )
            val headerFontSize = if (LocalConfiguration.current.screenWidthDp.dp > 600.dp) 14.sp else 12.sp
            Row(modifier = Modifier.fillMaxWidth()) {
                dayNames.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = headerFontSize,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            val cal = remember(selectedYear, selectedMonth) {
                Calendar.getInstance().apply { set(selectedYear, selectedMonth, 1) }
            }
            val firstDayOfWeek = remember(selectedYear, selectedMonth) { (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7 }
            val daysInMonth = remember(selectedYear, selectedMonth) { cal.getActualMaximum(Calendar.DAY_OF_MONTH) }

            val totalCells = firstDayOfWeek + daysInMonth
            val rows = (totalCells + 6) / 7

            Column {
                for (row in 0 until rows) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0 until 7) {
                            val index = row * 7 + col
                            if (index < firstDayOfWeek || index >= totalCells) {
                                Spacer(modifier = Modifier.weight(1f).height(40.dp))
                            } else {
                                val day = index - firstDayOfWeek + 1
                                val monthKey = "%02d-%02d".format(selectedMonth + 1, day)
                                val event = eventsByMonthDay[monthKey]
                                val isToday = isCurrentMonth && todayDay == day
                                val isFav = settingsManager.isFavorite(monthKey)

                                Box(modifier = Modifier.weight(1f)) {
                                    DayCell(
                                        day = day,
                                        hasEvent = event != null,
                                        isToday = isToday,
                                        isFavorite = isFav,
                                        onClick = {
                                            if (event != null) {
                                                if (event.size == 1) {
                                                    selectedDayEvent = event[0]
                                                    isEditing = false
                                                    editTitle = event[0].title
                                                    editDescription = event[0].description
                                                    selectedEventIsModified = dayParser.isEventModified(event[0].date)
                                                } else {
                                                    selectedDayEvents = event
                                                }
                                            }
                                        },
                                        onLongClick = {
                                            createDay = day
                                            createMonth = selectedMonth + 1
                                            createYear = selectedYear
                                            createTitle = ""
                                            createDescription = ""
                                            createCountry = defaultCountryText
                                            showCreateDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedDayEvent != null) {
        var isFav by remember(selectedDayEvent) { mutableStateOf(settingsManager.isFavorite(selectedDayEvent!!.date)) }
        AlertDialog(
            onDismissRequest = {
                selectedDayEvent = null
                isEditing = false
            },
            title = {
                if (isEditing) Text(stringResource(R.string.edit_celebration), fontWeight = FontWeight.Bold)
                else Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedDayEvent!!.title,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        modifier = Modifier
                            .clickable {
                                settingsManager.toggleFavorite(selectedDayEvent!!.date)
                                isFav = !isFav
                                onEventsChanged()
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isFav) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isFav) stringResource(R.string.remove_favorite) else stringResource(R.string.add_favorite),
                            fontSize = 12.sp,
                            color = if (isFav) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    if (isEditing) {
                        OutlinedTextField(
                            value = editTitle,
                            onValueChange = { editTitle = it },
                            label = { Text(stringResource(R.string.name_label)) },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = editDescription,
                            onValueChange = { editDescription = it },
                            label = { Text(stringResource(R.string.description_label)) },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            minLines = 2
                        )
                    } else {
                        Text(
                            text = formatDateEs(context, selectedDayEvent!!.date),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        if (selectedEventIsModified) {
                            Text(
                                text = stringResource(R.string.modified),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        Text(
                            selectedDayEvent!!.description,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            stringResource(R.string.country_label_format, selectedDayEvent!!.country),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                if (isEditing) {
                    Row {
                        TextButton(onClick = {
                            if (editTitle.isNotBlank()) {
                                val parser = EventParser(context)
                                val currentEvents = parser.parseEvents().toMutableList()
                                val index = currentEvents.indexOfFirst { it.date == selectedDayEvent!!.date }
                                if (index >= 0) {
                                    currentEvents[index] = currentEvents[index].copy(
                                        title = editTitle,
                                        description = editDescription
                                    )
                                }
                                parser.saveEvents(currentEvents)
                                selectedDayEvent = currentEvents[index]
                                isEditing = false
                                onEventsChanged()
                                Toast.makeText(context, context.getString(R.string.celebration_saved), Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Text(stringResource(R.string.save))
                        }
                        TextButton(onClick = {
                            isEditing = false
                            editTitle = selectedDayEvent!!.title
                            editDescription = selectedDayEvent!!.description
                        }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var menuExpanded by remember { mutableStateOf(false) }
                        Box {
                            TextButton(onClick = { menuExpanded = true }) {
                                Text(stringResource(R.string.options))
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.edit)) },
                                    onClick = {
                                        menuExpanded = false
                                        isEditing = true
                                    }
                                )
                                if (selectedEventIsModified) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.restore)) },
                                        onClick = {
                                            menuExpanded = false
                                            val parser = EventParser(context)
                                            parser.restoreEvent(selectedDayEvent!!.date)
                                            val defaultEvent = parser.getDefaultEvent(selectedDayEvent!!.date)
                                            if (defaultEvent != null) {
                                                selectedDayEvent = defaultEvent
                                                selectedEventIsModified = false
                                            } else {
                                                selectedDayEvent = null
                                            }
                                            onEventsChanged()
                                            Toast.makeText(context, context.getString(R.string.celebration_restored), Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = {
                                        val isFav = settingsManager.isFavorite(selectedDayEvent!!.date)
                                        Text(if (isFav) stringResource(R.string.remove_favorite) else stringResource(R.string.add_favorite))
                                    },
                                    leadingIcon = {
                                        val isFav = settingsManager.isFavorite(selectedDayEvent!!.date)
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = null,
                                            tint = if (isFav) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        settingsManager.toggleFavorite(selectedDayEvent!!.date)
                                        onEventsChanged()
                                        val msg = if (settingsManager.isFavorite(selectedDayEvent!!.date))
                                            context.getString(R.string.added_to_favorites)
                                        else
                                            context.getString(R.string.removed_from_favorites)
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                        TextButton(onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, selectedDayEvent!!.title)
                                type = "text/plain"
                                setPackage("com.whatsapp")
                            }
                            try {
                                context.startActivity(Intent.createChooser(sendIntent, null))
                            } catch (_: Exception) {
                                val fallback = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, selectedDayEvent!!.title)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(fallback, context.getString(R.string.share_with)))
                            }
                        }) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(stringResource(R.string.share))
                        }
                    }
                }
            },
            dismissButton = {
                if (!isEditing) {
                    TextButton(onClick = {
                        selectedDayEvent = null
                        isEditing = false
                    }) {
                        Text(stringResource(R.string.close))
                    }
                }
            }
        )
    }

    if (selectedDayEvents != null) {
        AlertDialog(
            onDismissRequest = { selectedDayEvents = null },
            title = {
                Text(
                    text = stringResource(R.string.multiple_events_title, selectedDayEvents!!.size),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = formatDateEs(context, selectedDayEvents!![0].date),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    selectedDayEvents!!.forEach { event ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable {
                                    selectedDayEvent = event
                                    selectedDayEvents = null
                                    isEditing = false
                                    editTitle = event.title
                                    editDescription = event.description
                                    selectedEventIsModified = dayParser.isEventModified(event.date)
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = event.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                    text = event.description,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                                Text(
                                    text = stringResource(R.string.country_label_format, event.country),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedDayEvents = null }) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text(stringResource(R.string.add_celebration), fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = stringResource(R.string.date_format_day, createDay, createMonth, createYear),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = createTitle,
                        onValueChange = { createTitle = it },
                        label = { Text(stringResource(R.string.name_label)) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = createDescription,
                        onValueChange = { createDescription = it },
                        label = { Text(stringResource(R.string.description_label)) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        minLines = 2
                    )
                    var countryExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = countryExpanded,
                        onExpandedChange = { countryExpanded = !countryExpanded }
                    ) {
                        OutlinedTextField(
                            value = createCountry,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.country)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = countryExpanded,
                            onDismissRequest = { countryExpanded = false }
                        ) {
                            val allCountries = remember(events) {
                                events.map { it.country }.distinct().sorted()
                            }
                            allCountries.forEach { country ->
                                DropdownMenuItem(
                                    text = { Text(country) },
                                    onClick = {
                                        createCountry = country
                                        countryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (createTitle.isNotBlank()) {
                        val dateKey = "%02d-%02d".format(createMonth, createDay)
                        val newEvent = Event(
                            date = dateKey,
                            title = createTitle,
                            description = createDescription,
                            country = createCountry
                        )
                        val parser = EventParser(context)
                        val currentEvents = parser.parseEvents().toMutableList()
                        val existingIndex = currentEvents.indexOfFirst { it.date == dateKey }
                        if (existingIndex >= 0) {
                            currentEvents[existingIndex] = newEvent
                        } else {
                            currentEvents.add(newEvent)
                        }
                        parser.saveEvents(currentEvents)
                        showCreateDialog = false
                        onEventsChanged()
                        Toast.makeText(context, context.getString(R.string.celebration_created), Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text(stringResource(R.string.create))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    eventsVersion: Int = 0,
    onEventsChanged: () -> Unit = {}
) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    val parser = remember(eventsVersion) { EventParser(context) }
    val allEvents = remember(eventsVersion) { parser.parseEvents() }
    val selectedCategoryFilters = remember { settingsManager.getCategoryFilter() }
    val events = remember(allEvents, selectedCategoryFilters) {
        if (selectedCategoryFilters.isEmpty()) allEvents
        else allEvents.filter { event ->
            Category.classify(event).key in selectedCategoryFilters
        }
    }
    val favorites = remember(eventsVersion) { settingsManager.getFavorites() }

    val favoriteEvents = remember(events, favorites) {
        events.filter { it.date in favorites }.sortedBy { it.date }
    }

    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.favorites),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (favoriteEvents.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.no_favorites),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.no_favorites_hint),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(favoriteEvents) { event ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable { selectedEvent = event },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = event.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = formatDateEs(context, event.date),
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                Text(
                                    text = event.description,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Text(
                                    text = stringResource(R.string.country_label_format, event.country),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedEvent != null) {
        val isFav = settingsManager.isFavorite(selectedEvent!!.date)
        AlertDialog(
            onDismissRequest = { selectedEvent = null },
            title = { Text(selectedEvent!!.title, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = formatDateEs(context, selectedEvent!!.date),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = selectedEvent!!.description,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = stringResource(R.string.country_label_format, selectedEvent!!.country),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = {
                        settingsManager.toggleFavorite(selectedEvent!!.date)
                        onEventsChanged()
                        selectedEvent = null
                    }) {
                        Icon(
                            imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isFav) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(if (isFav) stringResource(R.string.remove_favorite) else stringResource(R.string.add_favorite))
                    }
                    TextButton(onClick = { selectedEvent = null }) {
                        Text(stringResource(R.string.close))
                    }
                }
            }
        )
    }
}

@Composable
fun DayCell(day: Int, hasEvent: Boolean, isToday: Boolean = false, isFavorite: Boolean = false, onClick: () -> Unit, onLongClick: () -> Unit = {}) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cellPadding = if (screenWidth > 600.dp) 3.dp else 2.dp
    val dayFontSize = if (screenWidth > 600.dp) 18.sp else 14.sp
    val dotSize = if (screenWidth > 600.dp) 5.dp else 4.dp
    val heartSize = if (screenWidth > 600.dp) 10.dp else 8.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(cellPadding)
            .clip(CircleShape)
            .then(
                if (hasEvent) Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                else Modifier
            )
            .then(
                if (isToday) Modifier.background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                else Modifier
            )
            .then(
                if (isToday) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                else Modifier
            )
            .combinedClickable(
                enabled = true,
                onClick = {
                    if (hasEvent) onClick()
                },
                onLongClick = onLongClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$day",
                fontSize = dayFontSize,
                color = when {
                    hasEvent -> MaterialTheme.colorScheme.onPrimaryContainer
                    isToday -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface
                },
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
            )
            if (hasEvent) {
                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
            if (isFavorite) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(heartSize),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun ScrollHintIndicator(onDismiss: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "scroll_hint")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(5000)
        onDismiss()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onDismiss() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.scroll_hint),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.scroll_hint_desc),
                tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = alpha),
                modifier = Modifier
                    .offset(y = offsetY.dp)
                    .size(28.dp)
            )
        }
    }
}
