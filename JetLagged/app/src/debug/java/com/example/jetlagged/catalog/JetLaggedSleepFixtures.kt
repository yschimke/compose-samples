/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetlagged.catalog

import com.example.jetlagged.sleep.SleepDayData
import com.example.jetlagged.sleep.SleepGraphData
import com.example.jetlagged.sleep.SleepPeriod
import com.example.jetlagged.sleep.SleepType
import java.time.LocalDateTime

/**
 * Deterministic sleep fixtures for the catalog previews.
 *
 * The app's own [com.example.jetlagged.data.sleepData] is built from `LocalDateTime.now()`, so the
 * day-of-week labels along the left edge of the time graph change every single day. That is fine in
 * the app — it is meant to read as "the last seven nights" — but it makes a rendered sticker sheet
 * churn: the same unmodified chart produces a different PNG tomorrow, and a visual diff cannot tell
 * that apart from a real regression.
 *
 * These fixtures pin the same shapes to a fixed week (Monday 4 March 2024) so the charts render
 * byte-identically until the drawing code actually changes. They also add the states the app's data
 * never contains — a 45-minute night, a 16-hour night, and each of the four sleep-score bands — so
 * the emoji ladder and the extremes of the bar's width/gradient maths are reviewable as pixels.
 */

/** Monday, so the week reads Mon…Sun in the day labels. */
private val WeekStart: LocalDateTime = LocalDateTime.of(2024, 3, 4, 0, 0)

/** A sleep segment as `"HH:mm"` start, `"HH:mm"` end, and its stage. */
private data class Segment(val from: String, val to: String, val type: SleepType)

private fun seg(from: String, to: String, type: SleepType) = Segment(from, to, type)

private fun String.minuteOfDay(): Int {
    val (hour, minute) = split(":").map(String::toInt)
    return hour * 60 + minute
}

private fun LocalDateTime.at(clock: String): LocalDateTime {
    val (hour, minute) = clock.split(":").map(String::toInt)
    return withHour(hour).withMinute(minute).withSecond(0).withNano(0)
}

/**
 * Builds a [SleepDayData] from wall-clock segments, rolling over to the next day whenever the clock
 * wraps past midnight — the same shape the hand-written sample data spells out longhand.
 */
private fun sleepDay(dayIndex: Long, score: Int, vararg segments: Segment): SleepDayData {
    val dayStart = WeekStart.plusDays(dayIndex)
    var cursorDay = 0L
    var previousEnd = -1
    val periods = segments.map { segment ->
        val from = segment.from.minuteOfDay()
        val to = segment.to.minuteOfDay()
        if (previousEnd >= 0 && from < previousEnd) cursorDay++
        val startTime = dayStart.plusDays(cursorDay).at(segment.from)
        if (to < from) cursorDay++
        val endTime = dayStart.plusDays(cursorDay).at(segment.to)
        previousEnd = to
        SleepPeriod(startTime = startTime, endTime = endTime, type = segment.type)
    }
    return SleepDayData(startDate = dayStart, sleepPeriods = periods, sleepScore = score)
}

private fun typicalNight(dayIndex: Long, score: Int) = sleepDay(
    dayIndex,
    score,
    seg("22:10", "22:35", SleepType.Awake),
    seg("22:35", "23:20", SleepType.Light),
    seg("23:20", "00:40", SleepType.Deep),
    seg("00:40", "02:10", SleepType.REM),
    seg("02:10", "02:25", SleepType.Awake),
    seg("02:25", "04:30", SleepType.Deep),
    seg("04:30", "06:20", SleepType.Light),
)

private fun restlessNight(dayIndex: Long, score: Int) = sleepDay(
    dayIndex,
    score,
    seg("23:05", "23:50", SleepType.Awake),
    seg("23:50", "00:30", SleepType.Light),
    seg("00:30", "01:10", SleepType.REM),
    seg("01:10", "02:30", SleepType.Awake),
    seg("02:30", "03:20", SleepType.Light),
    seg("03:20", "04:10", SleepType.REM),
    seg("04:10", "05:00", SleepType.Awake),
)

private fun deepNight(dayIndex: Long, score: Int) = sleepDay(
    dayIndex,
    score,
    seg("21:40", "22:00", SleepType.Awake),
    seg("22:00", "23:10", SleepType.Light),
    seg("23:10", "02:00", SleepType.Deep),
    seg("02:00", "03:30", SleepType.REM),
    seg("03:30", "05:10", SleepType.Deep),
    seg("05:10", "06:45", SleepType.Light),
)

/** One representative night — the default for any single-bar preview. */
internal val singleNight: SleepDayData = typicalNight(0, 92)

/** 45 minutes in bed: the shortest thing the bar can be asked to draw. */
internal val shortNight: SleepDayData = sleepDay(
    0,
    25,
    seg("01:20", "01:35", SleepType.Awake),
    seg("01:35", "02:05", SleepType.Light),
    seg("02:05", "02:20", SleepType.REM),
)

/** Sixteen hours in bed: the widest, with every stage present. */
internal val longNight: SleepDayData = sleepDay(
    0,
    88,
    seg("19:30", "20:00", SleepType.Awake),
    seg("20:00", "22:00", SleepType.Light),
    seg("22:00", "01:30", SleepType.Deep),
    seg("01:30", "04:00", SleepType.REM),
    seg("04:00", "07:30", SleepType.Deep),
    seg("07:30", "11:30", SleepType.Light),
)

/** The same night at each of the four sleep-score bands, so the emoji ladder is visible at once. */
internal val scoreBandNights: List<SleepDayData> = listOf(
    typicalNight(0, 92),
    typicalNight(1, 65),
    typicalNight(2, 50),
    typicalNight(3, 25),
)

/** A full week, best night first, worst night in the middle. */
internal val weekOfSleep: SleepGraphData = SleepGraphData(
    listOf(
        deepNight(0, 95),
        typicalNight(1, 82),
        restlessNight(2, 45),
        typicalNight(3, 64),
        restlessNight(4, 38),
        deepNight(5, 90),
        typicalNight(6, 71),
    ),
)

/** A single day, the state behind the graph card's "Day" tab. */
internal val singleDayOfSleep: SleepGraphData = SleepGraphData(listOf(singleNight))
