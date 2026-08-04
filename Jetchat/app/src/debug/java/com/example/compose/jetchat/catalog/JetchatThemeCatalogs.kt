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

package com.example.compose.jetchat.catalog

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import com.example.compose.jetchat.theme.JetchatDarkColorScheme
import com.example.compose.jetchat.theme.JetchatLightColorScheme
import com.example.compose.jetchat.theme.JetchatTypography
import ee.schimke.composeai.preview.ThemeCatalog

/*
 * `@ThemeCatalog` providers for Jetchat's colour schemes.
 *
 * `JetchatTheme` picks one of four schemes at composition time: the branded blue/yellow light and
 * dark schemes, or — on API 31+ with `isDynamicColor = true`, which is the default — a scheme
 * derived from the device wallpaper. Only one of them is ever live in a given render, and the
 * dynamic branch depends on a platform lookup, so no ordinary `@Preview` can put the four side by
 * side. Declaring each as a catalog gives every scheme its own Material 3 role + type-scale
 * specimen sheet, resolved exactly as the app would resolve it.
 *
 * These wrap `MaterialTheme` directly rather than re-entering `JetchatTheme`, precisely so the
 * scheme is pinned instead of being re-selected by the `isDarkTheme` / `isDynamicColor` logic the
 * catalog is trying to enumerate. `JetchatTypography` is kept so the type scale on each sheet is
 * the sample's own (Montserrat/Karla), not the Material default.
 *
 * The dynamic sheets fall back to the branded scheme below API 31, which is the same fallback
 * `JetchatTheme` applies.
 */

@Composable
private fun JetchatScheme(scheme: ColorScheme, content: @Composable () -> Unit) =
    MaterialTheme(colorScheme = scheme, typography = JetchatTypography, content = content)

@ThemeCatalog(name = "Jetchat · Light", group = "Jetchat")
class JetchatLightThemeCatalog : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) = JetchatScheme(JetchatLightColorScheme, content)
}

@ThemeCatalog(name = "Jetchat · Dark", group = "Jetchat")
class JetchatDarkThemeCatalog : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) = JetchatScheme(JetchatDarkColorScheme, content)
}

@ThemeCatalog(name = "Dynamic light", group = "Jetchat")
class JetchatDynamicLightThemeCatalog : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) {
        val context = LocalContext.current
        val scheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dynamicLightColorScheme(context)
        } else {
            JetchatLightColorScheme
        }
        JetchatScheme(scheme, content)
    }
}

@ThemeCatalog(name = "Dynamic dark", group = "Jetchat")
class JetchatDynamicDarkThemeCatalog : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) {
        val context = LocalContext.current
        val scheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dynamicDarkColorScheme(context)
        } else {
            JetchatDarkColorScheme
        }
        JetchatScheme(scheme, content)
    }
}
