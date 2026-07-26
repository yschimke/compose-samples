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

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.jetlagged.AverageTimeAsleepCard
import com.example.jetlagged.AverageTimeInBedCard
import com.example.jetlagged.WellnessCard
import com.example.jetlagged.heartrate.HeartRateCard
import com.example.jetlagged.sleep.JetLaggedHeader
import com.example.jetlagged.ui.theme.JetLaggedTheme

/**
 * Zero-argument catalog stand-ins for JetLagged's summary cards and header.
 *
 * Those composables are annotated `@Preview` in place, and Android Studio
 * renders them, because every parameter is defaulted
 * (`modifier: Modifier = Modifier`, `wellnessData: WellnessData = …`). The
 * headless renderer cannot: it resolves a preview through androidx's
 * `getDeclaredComposableMethod(name)`, which looks up the method by name with
 * no parameter types and so never matches the JVM signature Kotlin emits for a
 * defaulted-parameter composable. Every such preview fails inside androidx with
 * `NoSuchMethodException`, and the omission used to surface only at the end of
 * the pipeline as `missing renders for: Cards/Time asleep, …`.
 *
 * The build-free spec validator does not catch this either: it scans source,
 * sees the `@Preview`, and reports the function as present.
 *
 * Wrapping each one in a genuinely zero-argument preview restores it, so the
 * catalog covers the same composables even though the in-place annotations
 * still fail. The main-source `@Preview`s stay: Android Studio renders them,
 * and they are what the sample's actual audience uses. These wrappers live in
 * `src/debug`, so they never reach a release build.
 */

@Preview(name = "Average time asleep", showBackground = true, widthDp = 400)
@Composable
fun AverageTimeAsleepCardCatalogPreview() {
    JetLaggedTheme { AverageTimeAsleepCard() }
}

/** Mirrors the main-source `@Preview(widthDp = 500, name = "larger screen")` on the same card. */
@Preview(name = "Average time asleep — larger screen", showBackground = true, widthDp = 500)
@Composable
fun AverageTimeAsleepCardWideCatalogPreview() {
    JetLaggedTheme { AverageTimeAsleepCard() }
}

@Preview(name = "Average time in bed", showBackground = true, widthDp = 400)
@Composable
fun AverageTimeInBedCardCatalogPreview() {
    JetLaggedTheme { AverageTimeInBedCard() }
}

/** Mirrors the main-source `@Preview(widthDp = 500, name = "larger screen")` on the same card. */
@Preview(name = "Average time in bed — larger screen", showBackground = true, widthDp = 500)
@Composable
fun AverageTimeInBedCardWideCatalogPreview() {
    JetLaggedTheme { AverageTimeInBedCard() }
}

@Preview(name = "Heart rate", showBackground = true, widthDp = 400)
@Composable
fun HeartRateCardCatalogPreview() {
    JetLaggedTheme { HeartRateCard() }
}

@Preview(name = "Wellness", showBackground = true, widthDp = 400)
@Composable
fun WellnessCardCatalogPreview() {
    JetLaggedTheme { WellnessCard() }
}

@Preview(name = "Header", showBackground = true, widthDp = 400)
@Composable
fun JetLaggedHeaderCatalogPreview() {
    JetLaggedTheme { JetLaggedHeader() }
}
