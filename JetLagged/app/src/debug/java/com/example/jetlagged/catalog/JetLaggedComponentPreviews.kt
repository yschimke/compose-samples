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

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetlagged.BasicInformationalCard
import com.example.jetlagged.HomeScreenCardHeading
import com.example.jetlagged.R
import com.example.jetlagged.TwoLineInfoCard
import com.example.jetlagged.WellnessBubble
import com.example.jetlagged.WellnessCard
import com.example.jetlagged.data.HeartRateOverallData
import com.example.jetlagged.data.WellnessData
import com.example.jetlagged.heartrate.HeartRateCard
import com.example.jetlagged.heartrate.HeartRateGraph
import com.example.jetlagged.sleep.JetLaggedHeader
import com.example.jetlagged.sleep.JetLaggedHeaderTabs
import com.example.jetlagged.sleep.JetLaggedSleepGraphCard
import com.example.jetlagged.sleep.SleepBar
import com.example.jetlagged.sleep.SleepTab
import com.example.jetlagged.sleep.SleepType
import com.example.jetlagged.sleep.TimeGraph
import com.example.jetlagged.sleep.colorForSleepType
import com.example.jetlagged.ui.theme.JetLaggedTheme
import com.example.jetlagged.ui.theme.LegendHeadingStyle
import com.example.jetlagged.ui.theme.SmallHeadingStyle

/**
 * `@Preview`s for JetLagged's charts, cards and chrome.
 *
 * JetLagged is the custom-graphics sample: the sleep bar, the time graph and the heart-rate trace
 * are hand-drawn `Path`s inside `drawWithCache`, laid out by a bespoke `Layout` rather than by any
 * stock component. There is no component library to read the intended output from, so a rendered
 * PNG is the only specification these surfaces have — and yet before this file only the sleep bar
 * itself, the legend and four summary cards were reachable by the renderer.
 *
 * Everything here is driven from the deterministic fixtures in `JetLaggedSleepFixtures.kt` rather
 * than the app's `now()`-based sample data, so the charts render identically from one day to the
 * next and a visual diff means something. The variants are chosen for what can actually break in a
 * charting app: a full week against a single day, a 45-minute night against a 16-hour one, each of
 * the four sleep-score bands, and the light/dark palettes side by side.
 *
 * These live in the `debug` source set, so nothing here reaches a release build.
 */

@Composable
private fun Wrap(dark: Boolean = false, content: @Composable () -> Unit) = JetLaggedTheme(isDarkTheme = dark) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Box(Modifier.padding(8.dp)) { content() }
    }
}

// ---------------------------------------------------------------- sleep bar

@Preview(name = "SleepBar", showBackground = true, widthDp = 380, heightDp = 80)
@Composable
fun SleepBarCatalogPreview() = Wrap {
    SleepBar(sleepData = singleNight, modifier = Modifier.fillMaxWidth())
}

@Preview(name = "SleepBar — 45 minute night", showBackground = true, widthDp = 380, heightDp = 80)
@Composable
fun SleepBarShortNightPreview() = Wrap {
    SleepBar(sleepData = shortNight, modifier = Modifier.fillMaxWidth())
}

@Preview(name = "SleepBar — 16 hour night", showBackground = true, widthDp = 380, heightDp = 80)
@Composable
fun SleepBarLongNightPreview() = Wrap {
    SleepBar(sleepData = longNight, modifier = Modifier.fillMaxWidth())
}

@Preview(name = "SleepBar — score bands", showBackground = true, widthDp = 380, heightDp = 300)
@Composable
fun SleepBarScoreBandsPreview() = Wrap {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        scoreBandNights.forEach { night ->
            Text("score ${night.sleepScore}", style = LegendHeadingStyle)
            SleepBar(sleepData = night, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Preview(
    name = "SleepBar — dark",
    showBackground = true,
    widthDp = 380,
    heightDp = 80,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun SleepBarDarkPreview() = Wrap(dark = true) {
    SleepBar(sleepData = singleNight, modifier = Modifier.fillMaxWidth())
}

// ---------------------------------------------------------------- sleep stage palette

@Composable
private fun SleepStageSwatches() {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        SleepType.entries.forEach { type ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(colorForSleepType(type)),
                )
                Text(stringResource(type.title), style = LegendHeadingStyle)
            }
        }
    }
}

@Preview(name = "Sleep stage palette", showBackground = true, widthDp = 380, heightDp = 100)
@Composable
fun SleepStagePalettePreview() = Wrap { SleepStageSwatches() }

@Preview(
    name = "Sleep stage palette — dark",
    showBackground = true,
    widthDp = 380,
    heightDp = 100,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun SleepStagePaletteDarkPreview() = Wrap(dark = true) { SleepStageSwatches() }

// ---------------------------------------------------------------- time graph

@Preview(name = "SleepGraphCard — week", showBackground = true, widthDp = 700, heightDp = 460)
@Composable
fun JetLaggedSleepGraphCardPreview() = Wrap {
    JetLaggedSleepGraphCard(sleepState = weekOfSleep)
}

@Preview(name = "SleepGraphCard — single day", showBackground = true, widthDp = 700, heightDp = 300)
@Composable
fun JetLaggedSleepGraphCardSingleDayPreview() = Wrap {
    JetLaggedSleepGraphCard(sleepState = singleDayOfSleep)
}

@Preview(
    name = "SleepGraphCard — dark",
    showBackground = true,
    widthDp = 700,
    heightDp = 460,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun JetLaggedSleepGraphCardDarkPreview() = Wrap(dark = true) {
    JetLaggedSleepGraphCard(sleepState = weekOfSleep)
}

/**
 * The bare custom [TimeGraph] `Layout`, without the card chrome the app wraps it in — this is the
 * measure/place algorithm on its own, which is what actually decides where each bar starts.
 */
@Preview(name = "TimeGraph — bare layout", showBackground = true, widthDp = 760, heightDp = 340)
@Composable
fun TimeGraphPreview() = Wrap {
    val graph = weekOfSleep
    val hours = (graph.earliestStartHour..23) + (0..graph.latestEndHour)
    TimeGraph(
        dayItemsCount = graph.sleepDayData.size,
        hoursHeader = {
            Row(Modifier.padding(bottom = 16.dp)) {
                hours.forEach { hour ->
                    Text(
                        text = "$hour",
                        textAlign = TextAlign.Center,
                        style = SmallHeadingStyle,
                        modifier = Modifier
                            .width(50.dp)
                            .padding(vertical = 4.dp),
                    )
                }
            }
        },
        dayLabel = { index ->
            Text(
                graph.sleepDayData[index].startDate.dayOfWeek.name.take(3),
                style = SmallHeadingStyle,
                modifier = Modifier.padding(start = 8.dp, end = 24.dp),
            )
        },
        bar = { index ->
            val data = graph.sleepDayData[index]
            SleepBar(
                sleepData = data,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .timeGraphBar(
                        start = data.firstSleepStart,
                        end = data.lastSleepEnd,
                        hours = hours,
                    ),
            )
        },
    )
}

// ---------------------------------------------------------------- heart rate

@Preview(name = "HeartRateGraph", showBackground = true, widthDp = 420, heightDp = 120)
@Composable
fun HeartRateGraphPreview() = Wrap {
    HeartRateGraph(listData = HeartRateOverallData().listData)
}

@Preview(
    name = "HeartRateGraph — dark",
    showBackground = true,
    widthDp = 420,
    heightDp = 120,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun HeartRateGraphDarkPreview() = Wrap(dark = true) {
    HeartRateGraph(listData = HeartRateOverallData().listData)
}

@Preview(
    name = "HeartRateCard — dark",
    showBackground = true,
    widthDp = 400,
    heightDp = 300,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun HeartRateCardDarkPreview() = Wrap(dark = true) { HeartRateCard() }

@Preview(name = "HeartRateCard — resting", showBackground = true, widthDp = 400, heightDp = 300)
@Composable
fun HeartRateCardRestingPreview() = Wrap {
    HeartRateCard(heartRateData = HeartRateOverallData(averageBpm = 48))
}

// ---------------------------------------------------------------- cards

@Preview(name = "BasicInformationalCard", showBackground = true, widthDp = 320, heightDp = 160)
@Composable
fun BasicInformationalCardPreview() = Wrap {
    BasicInformationalCard(borderColor = MaterialTheme.colorScheme.primary) {
        Box(Modifier.padding(24.dp)) { HomeScreenCardHeading(text = "Sleep") }
    }
}

@Preview(name = "TwoLineInfoCard", showBackground = true, widthDp = 240, heightDp = 240)
@Composable
fun TwoLineInfoCardPreview() = Wrap {
    TwoLineInfoCard(
        borderColor = JetLaggedTheme.extraColors.bed,
        firstLineText = stringResource(R.string.ave_time_in_bed_heading),
        secondLineText = "8h42min",
        icon = R.drawable.ic_watch,
    )
}

/** Above 400dp the card flips from a stacked column to an icon-beside-text row. */
@Preview(name = "TwoLineInfoCard — wide", showBackground = true, widthDp = 520, heightDp = 240)
@Composable
fun TwoLineInfoCardWidePreview() = Wrap {
    TwoLineInfoCard(
        borderColor = JetLaggedTheme.extraColors.sleep,
        firstLineText = stringResource(R.string.ave_time_sleep_heading),
        secondLineText = "7h42min",
        icon = R.drawable.ic_single_bed,
        modifier = Modifier.size(480.dp, 200.dp),
    )
}

@Preview(name = "WellnessCard — recorded night", showBackground = true, widthDp = 400, heightDp = 260)
@Composable
fun WellnessCardRecordedPreview() = Wrap {
    WellnessCard(wellnessData = WellnessData(snoring = 128, coughing = 42, respiration = 17))
}

@Preview(
    name = "WellnessCard — dark",
    showBackground = true,
    widthDp = 400,
    heightDp = 260,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun WellnessCardDarkPreview() = Wrap(dark = true) {
    WellnessCard(wellnessData = WellnessData(snoring = 128, coughing = 42, respiration = 17))
}

@Preview(name = "WellnessBubble", showBackground = true, widthDp = 140, heightDp = 140)
@Composable
fun WellnessBubblePreview() = Wrap {
    WellnessBubble(titleText = "Snoring", countText = "128", metric = "min")
}

@Preview(name = "CardHeading", showBackground = true, widthDp = 320, heightDp = 60)
@Composable
fun HomeScreenCardHeadingPreview() = Wrap { HomeScreenCardHeading(text = "Wellness") }

// ---------------------------------------------------------------- chrome

@Preview(name = "HeaderTabs — Week", showBackground = true, widthDp = 400, heightDp = 72)
@Composable
fun JetLaggedHeaderTabsPreview() = Wrap { HeaderTabs(SleepTab.Week) }

@Preview(name = "HeaderTabs — Day", showBackground = true, widthDp = 400, heightDp = 72)
@Composable
fun JetLaggedHeaderTabsDayPreview() = Wrap { HeaderTabs(SleepTab.Day) }

@Preview(name = "HeaderTabs — 1Y", showBackground = true, widthDp = 400, heightDp = 72)
@Composable
fun JetLaggedHeaderTabsOneYearPreview() = Wrap { HeaderTabs(SleepTab.OneYear) }

@Composable
private fun HeaderTabs(initial: SleepTab) {
    var selected by remember { mutableStateOf(initial) }
    JetLaggedHeaderTabs(onTabSelected = { selected = it }, selectedTab = selected)
}

@Preview(
    name = "Header — dark",
    showBackground = true,
    widthDp = 400,
    heightDp = 170,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun JetLaggedHeaderDarkPreview() = Wrap(dark = true) { JetLaggedHeader() }
