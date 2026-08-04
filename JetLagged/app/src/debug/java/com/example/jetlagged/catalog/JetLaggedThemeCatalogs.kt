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
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import com.example.jetlagged.ui.theme.JetLaggedTheme
import ee.schimke.composeai.preview.ThemeCatalog

/*
 * `@ThemeCatalog` providers for JetLagged's two palettes.
 *
 * JetLagged does not use stock Material 3 roles for the things it actually draws. Its light palette
 * is a yellow/lilac/mint/coral set and its dark palette is a red/plum/forest set, and most of those
 * colours live in `JetLaggedExtraColors` — a `staticCompositionLocalOf` bolted alongside the
 * `ColorScheme` — rather than in `MaterialTheme.colorScheme`. `JetLaggedTheme` is the only place the
 * two halves are wired together, so wrapping it (rather than `MaterialTheme` with a hand-picked
 * scheme) is what makes a specimen sheet reflect what the app really resolves.
 *
 * The app picks between them with `isSystemInDarkTheme()`, which a plain `@Preview` can only reach
 * through `uiMode` on each individual preview. Declaring the two palettes as theme catalogs renders
 * the resolved `colorScheme`, `typography` and `shapes` for each one as its own sheet, so the pair
 * is reviewable side by side instead of one preview at a time.
 *
 * `shapes` matters here too: `JetLaggedTheme` overrides `large` to `CircleShape`, which is why the
 * summary cards are pill-shaped rather than rounded rectangles.
 *
 * These live in the `debug` source set, so nothing here reaches a release build.
 */

@ThemeCatalog(name = "JetLagged · Light", group = "JetLagged")
class JetLaggedLightThemeCatalog : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) = JetLaggedTheme(isDarkTheme = false, content = content)
}

@ThemeCatalog(name = "JetLagged · Dark", group = "JetLagged")
class JetLaggedDarkThemeCatalog : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) = JetLaggedTheme(isDarkTheme = true, content = content)
}
