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

package com.example.jetcaster.ui.tooling

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.example.jetcaster.ui.LocalAnimatedVisibilityScope
import com.example.jetcaster.ui.LocalSharedTransitionScope

/*
 * Only `JetcasterApp` provides `LocalSharedTransitionScope` / `LocalAnimatedVisibilityScope`, and
 * the screens that opt into shared-element transitions read them with a `?: throw
 * IllegalStateException("No SharedElementScope found")`. A `@Preview` renders a screen outside the
 * `SharedTransitionLayout` + `NavHost`, so both locals are null and the preview dies before it draws
 * anything. [SharedTransitionPreview] supplies the same two scopes the app does, so those screens can
 * be previewed.
 *
 * The `AnimatedVisibility` starts out visible, so its transition is already settled on the first
 * frame and the render is not at the mercy of animation timing.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionPreview(content: @Composable () -> Unit) {
    SharedTransitionLayout {
        AnimatedVisibility(visible = true) {
            CompositionLocalProvider(
                LocalSharedTransitionScope provides this@SharedTransitionLayout,
                LocalAnimatedVisibilityScope provides this,
            ) {
                content()
            }
        }
    }
}
