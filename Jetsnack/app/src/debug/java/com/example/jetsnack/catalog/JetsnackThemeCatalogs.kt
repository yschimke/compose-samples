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

package com.example.jetsnack.catalog

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import com.example.jetsnack.ui.theme.DarkColorPalette
import com.example.jetsnack.ui.theme.JetsnackColors
import com.example.jetsnack.ui.theme.LightColorPalette
import com.example.jetsnack.ui.theme.PreviewThemeOverride
import com.example.jetsnack.ui.theme.ProvideJetsnackColors
import com.example.jetsnack.ui.theme.Shapes
import com.example.jetsnack.ui.theme.Typography
import ee.schimke.composeai.preview.ThemeCatalog

/**
 * `@ThemeCatalog` providers for Jetsnack's **custom** design system.
 *
 * Jetsnack is the sample that deliberately does *not* use Material 3 colour roles: `JetsnackTheme`
 * carries its own [JetsnackColors] palette (brand, uiBackground, textHelp, seven gradient ramps, …)
 * through a `staticCompositionLocalOf`, and pins `MaterialTheme.colorScheme` to
 * [com.example.jetsnack.ui.theme.debugColors] — every M3 role set to magenta — precisely so that any
 * accidental use of an M3 role screams on screen.
 *
 * That trick is great for the app and useless for a specimen sheet: the plugin renders a
 * `@ThemeCatalog` by composing the provider's [Wrap] around a canned M3 role + type-scale grid, so
 * wrapping plain `JetsnackTheme` would produce two identical sheets of solid magenta. These
 * providers therefore **project** the Jetsnack palette onto the M3 roles the specimen grid reads —
 * brand → primary, uiBackground → surface/background, uiBorder → outline, and so on — while still
 * providing the real [JetsnackColors] via [ProvideJetsnackColors] so any Jetsnack component composed
 * inside the sheet resolves its genuine tokens. The result is a readable swatch sheet of Jetsnack's
 * own light and dark palettes, alongside its real Montserrat/Karla [Typography] and [Shapes].
 *
 * The projection is a presentation device for the catalog only; nothing in the app composes these.
 * For the palette *as the app actually models it* — including the gradient ramps, which have no M3
 * role to project onto — see the `JetsnackColorTokens*Preview` / `JetsnackGradientTokensPreview`
 * sheets in `JetsnackComponentPreviews.kt`.
 */
@Composable
private fun JetsnackPalette(colors: JetsnackColors, content: @Composable () -> Unit) = PreviewThemeOverride(content) { themedContent ->
    ProvideJetsnackColors(colors) {
        MaterialTheme(
            colorScheme = if (colors.isDark) {
                darkColorScheme(
                    primary = colors.brand,
                    onPrimary = colors.textInteractive,
                    secondary = colors.brandSecondary,
                    onSecondary = colors.textInteractive,
                    tertiary = colors.textLink,
                    background = colors.uiBackground,
                    onBackground = colors.textSecondary,
                    surface = colors.uiBackground,
                    onSurface = colors.textSecondary,
                    surfaceVariant = colors.uiFloated,
                    onSurfaceVariant = colors.textHelp,
                    surfaceTint = colors.brand,
                    outline = colors.uiBorder,
                    error = colors.error,
                    onError = colors.textInteractive,
                )
            } else {
                lightColorScheme(
                    primary = colors.brand,
                    onPrimary = colors.textInteractive,
                    secondary = colors.brandSecondary,
                    onSecondary = colors.textInteractive,
                    tertiary = colors.textLink,
                    background = colors.uiBackground,
                    onBackground = colors.textSecondary,
                    surface = colors.uiBackground,
                    onSurface = colors.textSecondary,
                    surfaceVariant = colors.uiFloated,
                    onSurfaceVariant = colors.textHelp,
                    surfaceTint = colors.brand,
                    outline = colors.uiBorder,
                    error = colors.error,
                    onError = colors.textInteractive,
                )
            },
            typography = Typography,
            shapes = Shapes,
            content = themedContent,
        )
    }
}

@ThemeCatalog(name = "Jetsnack · Light", group = "Jetsnack")
class JetsnackLightThemeCatalog : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) = JetsnackPalette(LightColorPalette, content)
}

@ThemeCatalog(name = "Jetsnack · Dark", group = "Jetsnack")
class JetsnackDarkThemeCatalog : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) = JetsnackPalette(DarkColorPalette, content)
}
