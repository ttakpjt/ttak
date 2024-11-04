package com.ttak.android.features.screentime.viewmodel

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.TimeZone

data class ScreenTimeData(
    val todayUsage: Int,
    val yesterdayUsage: Int,
    val todayUsageDifference: Int,
    val topSixAppsUsage: Map<String, Int>,
    val monthUsage: Int,
    val weekUsage: Int,
    val todayUsageMinutes: Int,
    val dailyUsageList: List<Int>
)

class ScreenTimeViewModel(
    private val context: Context
) : ViewModel() {

    private val _screenTimeData = MutableStateFlow<ScreenTimeData?>(null)
    val screenTimeData: StateFlow<ScreenTimeData?> = _screenTimeData

    init {
        loadScreenTimeData()
    }

    private fun loadScreenTimeData() {
        viewModelScope.launch {
            val todayUsage = getForegroundUsageDurationForToday()
            val yesterdayUsage = getForegroundUsageDurationForYesterday()
            val todayUsageDifference = todayUsage - yesterdayUsage
            val topSixAppsUsage = getTopSixAppsUsageForToday()
            val weekUsage = getUsageDurationForLastWeek()
            val monthUsage = getUsageDurationForCurrentMonth()
            val dailyUsageList = getDailyUsageForCurrentWeek()

            val data = ScreenTimeData(
                todayUsage = todayUsage,
                yesterdayUsage = yesterdayUsage,
                todayUsageDifference = todayUsageDifference,
                topSixAppsUsage = topSixAppsUsage,
                monthUsage = monthUsage,
                weekUsage = weekUsage,
                todayUsageMinutes = todayUsage,
                dailyUsageList = dailyUsageList
            )
            _screenTimeData.value = data
        }
    }

    // 오늘 사용 시간 계산 (분 단위)
    private suspend fun getForegroundUsageDurationForToday(): Int = withContext(Dispatchers.IO) {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("Asia/Seoul")
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val events = usageStatsManager.queryEvents(startTime, endTime)
        var totalForegroundTime = 0L
        var lastEventTime = 0L
        var isForeground = false

        while (events.hasNextEvent()) {
            val event = UsageEvents.Event()
            events.getNextEvent(event)

            when (event.eventType) {
                UsageEvents.Event.MOVE_TO_FOREGROUND -> {
                    lastEventTime = event.timeStamp
                    isForeground = true
                }

                UsageEvents.Event.MOVE_TO_BACKGROUND -> {
                    if (isForeground) {
                        val usageTime = event.timeStamp - lastEventTime
                        totalForegroundTime += usageTime
                        isForeground = false
                    }
                }
            }
        }

        if (isForeground) {
            val usageTime = endTime - lastEventTime
            totalForegroundTime += usageTime
        }

        (totalForegroundTime / 60000).toInt() // 분 단위로 반환
    }

    // 어제 사용 시간 계산 (분 단위)
    private suspend fun getForegroundUsageDurationForYesterday(): Int =
        withContext(Dispatchers.IO) {
            val usageStatsManager =
                context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val calendar = Calendar.getInstance().apply {
                timeZone = TimeZone.getTimeZone("Asia/Seoul")
                add(Calendar.DAY_OF_YEAR, -1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startTime = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val endTime = calendar.timeInMillis

            val events = usageStatsManager.queryEvents(startTime, endTime)
            var totalForegroundTime = 0L
            var lastEventTime = 0L
            var isForeground = false

            while (events.hasNextEvent()) {
                val event = UsageEvents.Event()
                events.getNextEvent(event)

                when (event.eventType) {
                    UsageEvents.Event.MOVE_TO_FOREGROUND -> {
                        lastEventTime = event.timeStamp
                        isForeground = true
                    }

                    UsageEvents.Event.MOVE_TO_BACKGROUND -> {
                        if (isForeground) {
                            val usageTime = event.timeStamp - lastEventTime
                            totalForegroundTime += usageTime
                            isForeground = false
                        }
                    }
                }
            }

            if (isForeground) {
                val usageTime = endTime - lastEventTime
                totalForegroundTime += usageTime
            }

            (totalForegroundTime / 60000).toInt() // 분 단위로 반환
        }

    // 가장 많이 사용한 6개의 앱과 사용 시간 (분 단위)
    private suspend fun getTopSixAppsUsageForToday(): Map<String, Int> =
        withContext(Dispatchers.IO) {
            val usageStatsManager =
                context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val calendar = Calendar.getInstance().apply {
                timeZone = TimeZone.getTimeZone("Asia/Seoul")
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startTime = calendar.timeInMillis
            val endTime = System.currentTimeMillis()

            val events = usageStatsManager.queryEvents(startTime, endTime)
            val appUsageMap = mutableMapOf<String, Long>()
            var lastEventTime = 0L
            var lastPackageName = ""
            var isForeground = false

            while (events.hasNextEvent()) {
                val event = UsageEvents.Event()
                events.getNextEvent(event)

                when (event.eventType) {
                    UsageEvents.Event.MOVE_TO_FOREGROUND -> {
                        lastEventTime = event.timeStamp
                        lastPackageName = event.packageName
                        isForeground = true
                    }

                    UsageEvents.Event.MOVE_TO_BACKGROUND -> {
                        if (isForeground && event.packageName == lastPackageName) {
                            val usageTime = event.timeStamp - lastEventTime
                            appUsageMap[lastPackageName] =
                                appUsageMap.getOrDefault(lastPackageName, 0L) + usageTime
                            isForeground = false
                        }
                    }
                }
            }

            appUsageMap.entries.sortedByDescending { it.value }
                .take(6)
                .associate { it.key to (it.value / 60000).toInt() } // 분 단위로 반환
        }

    // 이번 주 (일요일부터 토요일까지) 요일별 사용 시간을 분 단위로 반환
    private suspend fun getDailyUsageForCurrentWeek(): List<Int> = withContext(Dispatchers.IO) {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("Asia/Seoul")
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY) // 이번 주 일요일로 설정
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val dailyUsage = mutableListOf<Int>()
        repeat(7) {
            val startTime = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val endTime = calendar.timeInMillis

            val events = usageStatsManager.queryEvents(startTime, endTime)
            var totalForegroundTime = 0L
            var lastEventTime = 0L
            var isForeground = false

            while (events.hasNextEvent()) {
                val event = UsageEvents.Event()
                events.getNextEvent(event)

                when (event.eventType) {
                    UsageEvents.Event.MOVE_TO_FOREGROUND -> {
                        lastEventTime = event.timeStamp
                        isForeground = true
                    }
                    UsageEvents.Event.MOVE_TO_BACKGROUND -> {
                        if (isForeground) {
                            val usageTime = event.timeStamp - lastEventTime
                            totalForegroundTime += usageTime
                            isForeground = false
                        }
                    }
                }
            }

            if (isForeground) {
                val usageTime = endTime - lastEventTime
                totalForegroundTime += usageTime
            }

            dailyUsage.add((totalForegroundTime / 60000).toInt()) // 분 단위로 변환
        }
        dailyUsage
    }

    // 지난 주 전체 사용 시간 (전주의 일요일부터 토요일까지)을 분 단위로 반환
    private suspend fun getUsageDurationForLastWeek(): Int = withContext(Dispatchers.IO) {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("Asia/Seoul")
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            add(Calendar.WEEK_OF_YEAR, -1) // 지난 주 일요일로 설정
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val startTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, 7) // 지난 주 토요일까지 범위를 설정
        val endTime = calendar.timeInMillis

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )
        val totalForegroundTime = stats.sumOf { it.totalTimeInForeground }
        (totalForegroundTime / (1000 * 60)).toInt() // 분 단위로 변환
    }

    // 이번 달 사용 시간을 분 단위로 반환 (1일부터 오늘까지)
    private suspend fun getUsageDurationForCurrentMonth(): Int = withContext(Dispatchers.IO) {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("Asia/Seoul")
            set(Calendar.DAY_OF_MONTH, 1) // 이번 달 1일로 설정
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val events = usageStatsManager.queryEvents(startTime, endTime)
        var totalForegroundTime = 0L
        var lastEventTime = 0L
        var isForeground = false

        while (events.hasNextEvent()) {
            val event = UsageEvents.Event()
            events.getNextEvent(event)

            when (event.eventType) {
                UsageEvents.Event.MOVE_TO_FOREGROUND -> {
                    lastEventTime = event.timeStamp
                    isForeground = true
                }
                UsageEvents.Event.MOVE_TO_BACKGROUND -> {
                    if (isForeground) {
                        val usageTime = event.timeStamp - lastEventTime
                        totalForegroundTime += usageTime
                        isForeground = false
                    }
                }
            }
        }

        if (isForeground) {
            val usageTime = endTime - lastEventTime
            totalForegroundTime += usageTime
        }

        (totalForegroundTime / 60000).toInt() // 분 단위로 변환
    }
    }
