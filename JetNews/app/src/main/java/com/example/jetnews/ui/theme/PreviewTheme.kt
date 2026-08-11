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

package com.example.jetnews.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalPreviewThemeOverride = staticCompositionLocalOf { false }

/** Applies the app theme unless an outer preview catalog has already selected one. */
@Composable
internal fun PreviewTheme(content: @Composable () -> Unit, theme: @Composable (@Composable () -> Unit) -> Unit) {
    if (LocalPreviewThemeOverride.current) content() else theme(content)
}

/** Applies a catalog theme and prevents nested app-theme calls from replacing it. */
@Composable
internal fun PreviewThemeOverride(content: @Composable () -> Unit, theme: @Composable (@Composable () -> Unit) -> Unit) {
    theme {
        CompositionLocalProvider(LocalPreviewThemeOverride provides true, content = content)
    }
}
