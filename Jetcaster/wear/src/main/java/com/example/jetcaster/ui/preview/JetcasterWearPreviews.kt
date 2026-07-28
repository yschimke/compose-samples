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

package com.example.jetcaster.ui.preview

import androidx.compose.ui.tooling.preview.Preview

/**
 * Jetcaster's canonical Wear preview surface.
 *
 * Catalog size variants use separate small-round wrapper functions. Keeping one device per
 * function also avoids ambiguous variant axes while compose-ai-tools#2870 is unresolved.
 */
@Preview(
    name = "Large Round",
    group = "Devices - Large Round",
    device = "id:wearos_large_round",
    showSystemUi = true,
    showBackground = true,
    backgroundColor = 0xFF000000,
)
annotation class JetcasterWearLargeRoundPreview

/** Small-round companion used by catalog variant wrapper functions. */
@Preview(
    name = "Small Round",
    group = "Catalog - Small Round",
    device = "id:wearos_small_round",
    showSystemUi = true,
    showBackground = true,
    backgroundColor = 0xFF000000,
)
annotation class JetcasterWearSmallRoundPreview
