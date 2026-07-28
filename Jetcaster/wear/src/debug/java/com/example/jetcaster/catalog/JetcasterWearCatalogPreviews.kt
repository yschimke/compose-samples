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

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.jetcaster.ui.components.MediaContentPreview
import com.example.jetcaster.ui.episode.EpisodeScreenLoadedPreview
import com.example.jetcaster.ui.podcast.PodcastDetailsScreenLoadedPreview
import com.example.jetcaster.ui.podcasts.PodcastScreenLoadedPreview

private const val SMALL_ROUND_DEVICE = "id:wearos_small_round"

@Preview(name = "Podcast screen — largest font", device = SMALL_ROUND_DEVICE, fontScale = 1.24f)
@Composable
fun PodcastScreenLargestFontCatalogPreview() = PodcastScreenLoadedPreview()

@Preview(name = "Podcast details — largest font", device = SMALL_ROUND_DEVICE, fontScale = 1.24f)
@Composable
fun PodcastDetailsLargestFontCatalogPreview() = PodcastDetailsScreenLoadedPreview()

@Preview(name = "Episode screen — largest font", device = SMALL_ROUND_DEVICE, fontScale = 1.24f)
@Composable
fun EpisodeScreenLargestFontCatalogPreview() = EpisodeScreenLoadedPreview()

@Preview(name = "Player controls — largest font", device = SMALL_ROUND_DEVICE, fontScale = 1.24f)
@Composable
fun PlayerControlsLargestFontCatalogPreview() = MediaContentPreview()
