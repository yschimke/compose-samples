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

package com.example.jetcaster.catalog

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import com.example.jetcaster.designsystem.theme.JetcasterShapes
import com.example.jetcaster.designsystem.theme.JetcasterTypography
import com.example.jetcaster.ui.theme.PreviewThemeOverride
import com.example.jetcaster.ui.theme.darkScheme
import com.example.jetcaster.ui.theme.highContrastDarkColorScheme
import com.example.jetcaster.ui.theme.highContrastLightColorScheme
import com.example.jetcaster.ui.theme.lightScheme
import com.example.jetcaster.ui.theme.mediumContrastDarkColorScheme
import com.example.jetcaster.ui.theme.mediumContrastLightColorScheme
import ee.schimke.composeai.preview.ThemeCatalog

/*
 * `@ThemeCatalog` providers for Jetcaster's colour schemes.
 *
 * Jetcaster's design system defines six schemes — light and dark, each at standard, medium and high
 * contrast. `JetcasterTheme` used to resolve exactly one of them, `darkScheme`, with no parameter to
 * flip and no system setting that reached the others, so five sixths of the sample's colour work was
 * invisible everywhere: in the app, in the IDE, and in CI.
 *
 * Light and dark are now genuinely selectable — `JetcasterTheme` follows `isSystemInDarkTheme()` —
 * so those two are covered by ordinary `@Preview`s as well. The four contrast variants still have no
 * code path that selects them: Material 3 resolves contrast from `UiModeManager` at runtime, which
 * is unavailable in preview (b/336693596), so they remain reachable only from here.
 *
 * Declaring each as a `@ThemeCatalog` renders it against a canned Material 3 role + type-scale grid,
 * so the full contrast ladder (3 levels × 2 modes) becomes reviewable as a matrix without changing
 * the app's behaviour. Each provider pins its scheme through the same [MaterialExpressiveTheme] the
 * app uses, with the app's real shapes and typography, so what the sheet shows is what the app would
 * render if that scheme were selected.
 */

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun JetcasterScheme(scheme: ColorScheme, content: @Composable () -> Unit) = PreviewThemeOverride(content) { themedContent ->
    MaterialExpressiveTheme(
        colorScheme = scheme,
        motionScheme = MotionScheme.expressive(),
        shapes = JetcasterShapes,
        typography = JetcasterTypography,
        content = themedContent,
    )
}

/** The scheme the app actually ships with — every other catalog here is currently unreachable. */
@ThemeCatalog(name = "Jetcaster · Dark", group = "Jetcaster")
class JetcasterDarkThemeCatalog : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) = JetcasterScheme(darkScheme, content)
}

@ThemeCatalog(name = "Jetcaster · Light", group = "Jetcaster")
class JetcasterLightThemeCatalog : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) = JetcasterScheme(lightScheme, content)
}

@ThemeCatalog(name = "Dark — medium contrast", group = "Jetcaster")
class JetcasterDarkMediumContrastThemeCatalog : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) = JetcasterScheme(mediumContrastDarkColorScheme, content)
}

@ThemeCatalog(name = "Dark — high contrast", group = "Jetcaster")
class JetcasterDarkHighContrastThemeCatalog : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) = JetcasterScheme(highContrastDarkColorScheme, content)
}

@ThemeCatalog(name = "Light — medium contrast", group = "Jetcaster")
class JetcasterLightMediumContrastThemeCatalog : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) = JetcasterScheme(mediumContrastLightColorScheme, content)
}

@ThemeCatalog(name = "Light — high contrast", group = "Jetcaster")
class JetcasterLightHighContrastThemeCatalog : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) = JetcasterScheme(highContrastLightColorScheme, content)
}
