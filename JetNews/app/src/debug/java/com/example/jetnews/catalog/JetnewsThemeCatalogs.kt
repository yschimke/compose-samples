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

package com.example.jetnews.catalog

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import com.example.jetnews.ui.theme.DarkColors
import com.example.jetnews.ui.theme.JetnewsShapes
import com.example.jetnews.ui.theme.JetnewsTypography
import com.example.jetnews.ui.theme.LightColors
import ee.schimke.composeai.preview.ThemeCatalog

/**
 * `@ThemeCatalog` providers for JetNews' colour schemes.
 *
 * [com.example.jetnews.ui.theme.JetnewsTheme] does not simply pick [LightColors] or [DarkColors]:
 * on API 31+ it prefers `dynamicLightColorScheme` / `dynamicDarkColorScheme`, which read the wallpaper
 * palette from the device. That means the sample's *own* brand schemes — the ones the `Color.kt`
 * tokens define, and the ones every pre-S device actually renders — are unreachable from a plain
 * `@Preview` running on a modern SDK: the theme resolves the dynamic scheme instead and the brand
 * colours never appear on screen.
 *
 * Declaring each scheme as a `@ThemeCatalog` pins it. The plugin composes each provider's [Wrap]
 * around a canned Material 3 role + type-scale grid, so both brand schemes get a specimen sheet
 * showing their live resolved `colorScheme`, `typography` and `shapes` regardless of what the host
 * SDK would have substituted.
 *
 * These wrap [MaterialTheme] directly rather than `JetnewsTheme`, precisely to bypass the dynamic
 * colour branch the catalog is trying to enumerate around.
 */
@Composable
private fun JetnewsScheme(scheme: ColorScheme, content: @Composable () -> Unit) =
    MaterialTheme(colorScheme = scheme, shapes = JetnewsShapes, typography = JetnewsTypography, content = content)

@ThemeCatalog(name = "Light", group = "JetNews")
class JetnewsLightThemeCatalog : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) = JetnewsScheme(LightColors, content)
}

@ThemeCatalog(name = "Dark", group = "JetNews")
class JetnewsDarkThemeCatalog : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) = JetnewsScheme(DarkColors, content)
}
