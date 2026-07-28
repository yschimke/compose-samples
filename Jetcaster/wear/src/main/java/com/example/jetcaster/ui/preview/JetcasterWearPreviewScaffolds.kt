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

package com.example.jetcaster.ui.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.TimeSource
import androidx.wear.compose.material3.TimeText
import com.example.jetcaster.theme.WearAppTheme

/**
 * A clock frozen at 10:10 for previews.
 *
 * The running app lets [TimeText] read the system clock; a preview must not, or every render of the
 * catalog differs from the last one by whatever the wall-clock happened to be, and the design
 * artifacts churn on nothing.
 */
object FixedPreviewTimeSource : TimeSource {
    @Composable
    override fun currentTime(): String = "10:10"
}

/**
 * The frame for a **screen** preview: Jetcaster's Wear theme plus the `AppScaffold` that
 * [com.example.jetcaster.WearApp] supplies at the top of the running app, with the curved
 * [TimeText] status strip frozen by [FixedPreviewTimeSource].
 *
 * Use this for a screen that composes its own `ScreenScaffold` (the whole-screen entry points:
 * `EpisodeScreen`, `QueueScreen`, `PodcastDetailsScreen`, …). Without the `AppScaffold` a screen
 * preview renders with no status strip at all — not what the screen looks like on a watch, and it
 * over-reports the vertical space the content actually gets.
 */
@Composable
fun JetcasterScreenPreview(content: @Composable () -> Unit) {
    WearAppTheme {
        AppScaffold(timeText = { TimeText(timeSource = FixedPreviewTimeSource) }) { content() }
    }
}

/**
 * The frame for a **list screen** preview whose content is the inner, already-hoisted composable
 * that takes a scroll state and the scaffold's content padding (`PodcastScreenLoaded`,
 * `EpisodeScreenLoaded`, `QueueScreenLoaded`, `LibraryScreen`, `LatestEpisodesScreen`).
 *
 * It supplies the same `AppScaffold` + frozen `TimeText` as [JetcasterScreenPreview] plus the
 * `ScreenScaffold` the production caller would have wrapped the content in, and hands the content
 * the real scroll state and content padding. Previews used to pass `PaddingValues()` and a bare
 * `rememberTransformingLazyColumnState()` here, which dropped both the status strip and the curved
 * top/bottom insets — so the first list item rendered jammed against the top of the round display
 * where the bezel clips it.
 */
@Composable
fun JetcasterListScreenPreview(content: @Composable (columnState: TransformingLazyColumnState, contentPadding: PaddingValues) -> Unit) {
    JetcasterScreenPreview {
        val columnState = rememberTransformingLazyColumnState()
        ScreenScaffold(scrollState = columnState) { contentPadding ->
            content(columnState, contentPadding)
        }
    }
}

/**
 * The frame for a **component** preview: the theme and nothing else, with the component centred on
 * the round face inside the Wear horizontal margin.
 *
 * Deliberately *not* a screen: a lone component hosted in an `AppScaffold`/`ScreenScaffold` lands at
 * the very top of the round display, where the curved edge clips it and the status strip crowds it
 * — the capture then reads as a broken screen rather than as the component. Centring it, with the
 * side margin a Wear list item would get, shows the component whole at the width it really has on
 * the device.
 */
@Composable
fun JetcasterComponentPreview(content: @Composable () -> Unit) {
    WearAppTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.fillMaxWidth(WEAR_COMPONENT_WIDTH_FRACTION)) { content() }
        }
    }
}

/**
 * The fraction of the display width a Wear list item spans — the Material 3 5.2% side margin taken
 * off each edge. Applied to a [JetcasterComponentPreview] so a `fillMaxWidth` component measures at
 * its on-device width instead of running edge to edge into the bezel.
 */
private const val WEAR_COMPONENT_WIDTH_FRACTION = 0.896f
