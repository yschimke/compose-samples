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

package com.example.reply.catalog

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import com.example.reply.ui.theme.PreviewThemeOverride
import com.example.reply.ui.theme.darkScheme
import com.example.reply.ui.theme.highContrastDarkColorScheme
import com.example.reply.ui.theme.highContrastLightColorScheme
import com.example.reply.ui.theme.lightScheme
import com.example.reply.ui.theme.mediumContrastDarkColorScheme
import com.example.reply.ui.theme.mediumContrastLightColorScheme
import com.example.reply.ui.theme.replyTypography
import com.example.reply.ui.theme.shapes
import ee.schimke.composeai.preview.ThemeCatalog

/**
 * `@ThemeCatalog` providers for Reply's colour schemes.
 *
 * Reply ships six schemes — light and dark, each at standard, medium and high contrast — but the
 * app only ever resolves one of them at a time, through [
 * com.example.reply.ui.theme.selectSchemeForContrast], which reads `UiModeManager.contrast` at
 * runtime. That system service isn't available in preview (b/336693596), so a plain `@Preview`
 * *cannot* show the contrast variants at all: they were invisible to review even though they're the
 * accessibility story the sample exists to demonstrate.
 *
 * Declaring each scheme as a `@ThemeCatalog` sidesteps the runtime lookup entirely — the plugin
 * composes each provider's [Wrap] around a canned Material 3 role + type-scale grid, so every
 * scheme gets a specimen sheet showing its live resolved `colorScheme`, `typography` and `shapes`.
 * That makes the contrast ladder reviewable as a matrix (3 contrast levels × 2 modes) rather than
 * something you can only reach by changing a system setting on a device.
 *
 * These wrap [MaterialTheme] directly rather than `ContrastAwareReplyTheme`, precisely to pin the
 * scheme instead of re-entering the contrast selection the catalog is trying to enumerate.
 */
@Composable private fun ReplyScheme(scheme: androidx.compose.material3.ColorScheme, content: @Composable () -> Unit) =
    PreviewThemeOverride(content) { themedContent ->
        MaterialTheme(colorScheme = scheme, typography = replyTypography, shapes = shapes, content = themedContent)
    }

@ThemeCatalog(name = "Reply · Light", group = "Reply")
class ReplyLightThemeCatalog : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) = ReplyScheme(lightScheme, content)
}

@ThemeCatalog(name = "Reply · Dark", group = "Reply")
class ReplyDarkThemeCatalog : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) = ReplyScheme(darkScheme, content)
}

@ThemeCatalog(name = "Light — medium contrast", group = "Reply")
class ReplyLightMediumContrastThemeCatalog : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) = ReplyScheme(mediumContrastLightColorScheme, content)
}

@ThemeCatalog(name = "Dark — medium contrast", group = "Reply")
class ReplyDarkMediumContrastThemeCatalog : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) = ReplyScheme(mediumContrastDarkColorScheme, content)
}

@ThemeCatalog(name = "Light — high contrast", group = "Reply")
class ReplyLightHighContrastThemeCatalog : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) = ReplyScheme(highContrastLightColorScheme, content)
}

@ThemeCatalog(name = "Dark — high contrast", group = "Reply")
class ReplyDarkHighContrastThemeCatalog : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) = ReplyScheme(highContrastDarkColorScheme, content)
}
