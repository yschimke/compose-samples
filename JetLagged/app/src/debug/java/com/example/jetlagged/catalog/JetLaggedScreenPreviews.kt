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
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.example.jetlagged.JetLaggedScreen
import com.example.jetlagged.data.JetLaggedHomeScreenViewModel
import com.example.jetlagged.ui.theme.JetLaggedTheme

/*
 * Screen-level `@Preview`s for JetLagged's single home screen.
 *
 * `JetLaggedScreen` is annotated `@MultiDevicePreview` in main source, but every one of its
 * parameters is defaulted, so the renderer skips it the same way it skips the summary cards — the
 * four device variants come back as `NoSuchMethodException` sidecars rather than PNGs. These
 * zero-argument wrappers are the same workaround `JetLaggedCatalogPreviews.kt` already applies to
 * the cards, extended to the screen so the catalog has a Screens section at all.
 *
 * They also pin the view model explicitly rather than letting `viewModel()` resolve it, because the
 * renderer composes into a bare host with no `ViewModelStoreOwner` worth relying on. The state is
 * the app's own `JetLaggedHomeScreenState`, so the composition here is exactly what `MainActivity`
 * puts on screen — the whole point of a screen-level sticker is that it is not a re-assembly of the
 * parts.
 *
 * Note that the app's `sleepData` is built from `LocalDateTime.now()`, so the day-of-week labels
 * down the left edge of the graph rotate daily. The component previews avoid that with the fixed
 * fixtures in `JetLaggedSleepFixtures.kt`; the screen cannot, because `JetLaggedHomeScreenViewModel`
 * builds its own state and takes no seam for injecting one. Judge these on layout and palette, and
 * the fixture-backed `SleepGraphCard` previews on chart geometry.
 *
 * These live in the `debug` source set, so nothing here reaches a release build.
 */

@Composable
private fun Screen(windowSizeClass: WindowWidthSizeClass, dark: Boolean = false) = JetLaggedTheme(isDarkTheme = dark) {
    JetLaggedScreen(
        windowSizeClass = windowSizeClass,
        viewModel = remember { JetLaggedHomeScreenViewModel() },
    )
}

@Preview(name = "Home — compact", showBackground = true, widthDp = 412, heightDp = 1100)
@Composable
fun JetLaggedScreenCompactPreview() = Screen(WindowWidthSizeClass.Compact)

/** Above compact the cards reflow into `FlowColumn`s beside the graph rather than stacking. */
@Preview(name = "Home — medium", showBackground = true, widthDp = 700, heightDp = 900)
@Composable
fun JetLaggedScreenMediumPreview() = Screen(WindowWidthSizeClass.Medium)

@Preview(name = "Home — expanded", showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
fun JetLaggedScreenExpandedPreview() = Screen(WindowWidthSizeClass.Expanded)

@Preview(
    name = "Home — dark",
    showBackground = true,
    widthDp = 412,
    heightDp = 1100,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun JetLaggedScreenDarkPreview() = Screen(WindowWidthSizeClass.Compact, dark = true)
