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

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AlertDialogContent
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.rememberPlaceholderState
import com.example.jetcaster.R
import com.example.jetcaster.core.domain.testing.PreviewPodcasts
import com.example.jetcaster.ui.components.MediaContentPreview
import com.example.jetcaster.ui.episode.EpisodeScreenLoadedPreview
import com.example.jetcaster.ui.episode.EpisodeScreenLoadingPreview
import com.example.jetcaster.ui.latest_episodes.LatestEpisodeScreen
import com.example.jetcaster.ui.latest_episodes.LatestEpisodeScreenLoadedPreview
import com.example.jetcaster.ui.latest_episodes.LatestEpisodeScreenState
import com.example.jetcaster.ui.library.LibraryScreen
import com.example.jetcaster.ui.library.LibraryScreenPreview
import com.example.jetcaster.ui.library.NoSubscribedPodcastScreen
import com.example.jetcaster.ui.library.PodcastContentPreview
import com.example.jetcaster.ui.podcast.PodcastDetailsScreen
import com.example.jetcaster.ui.podcast.PodcastDetailsScreenLoadedPreview
import com.example.jetcaster.ui.podcast.PodcastDetailsScreenState
import com.example.jetcaster.ui.podcasts.PodcastScreenLoadedPreview
import com.example.jetcaster.ui.podcasts.PodcastsScreen
import com.example.jetcaster.ui.podcasts.PodcastsScreenState
import com.example.jetcaster.ui.preview.JetcasterWearLargeRoundPreview
import com.example.jetcaster.ui.preview.JetcasterWearSmallRoundPreview
import com.example.jetcaster.ui.queue.QueueScreen
import com.example.jetcaster.ui.queue.QueueScreenLoadedPreview
import com.example.jetcaster.ui.queue.QueueScreenState

private const val SMALL_ROUND_DEVICE = "id:wearos_small_round"

@JetcasterWearSmallRoundPreview
@Composable
fun PodcastScreenSmallCatalogPreview() = PodcastScreenLoadedPreview()

@JetcasterWearLargeRoundPreview
@Composable
fun PodcastScreenLoadingCatalogPreview() = PodcastsScreen(
    podcastsScreenState = PodcastsScreenState.Loading,
    placeholderState = rememberPlaceholderState(isVisible = true),
    onPodcastsItemClick = {},
    onDismiss = {},
)

@JetcasterWearLargeRoundPreview
@Composable
fun PodcastScreenEmptyCatalogPreview() = WearEmptyDialogCatalogPreview(titleRes = R.string.podcasts_no_podcasts)

@JetcasterWearSmallRoundPreview
@Composable
fun PodcastDetailsSmallCatalogPreview() = PodcastDetailsScreenLoadedPreview()

@JetcasterWearLargeRoundPreview
@Composable
fun PodcastDetailsLoadingCatalogPreview() = PodcastDetailsScreen(
    uiState = PodcastDetailsScreenState.Loading,
    placeholderState = rememberPlaceholderState(isVisible = true),
    onPlayButtonClick = {},
    onEpisodeItemClick = {},
    onPlayEpisode = {},
    onDismiss = {},
)

@JetcasterWearLargeRoundPreview
@Composable
fun PodcastDetailsEmptyCatalogPreview() {
    WearEmptyDialogCatalogPreview(
        titleRes = R.string.podcasts_no_episode_podcasts,
    )
}

@JetcasterWearSmallRoundPreview
@Composable
fun LibraryScreenSmallCatalogPreview() = LibraryScreenPreview()

@JetcasterWearLargeRoundPreview
@Composable
fun LibraryScreenLoadingCatalogPreview() = LibraryScreen(
    columnState = rememberTransformingLazyColumnState(),
    contentPadding = PaddingValues(),
    onLatestEpisodeClick = {},
    onYourPodcastClick = {},
    onUpNextClick = {},
    placeholderState = rememberPlaceholderState(isVisible = true),
    queue = emptyList(),
)

@JetcasterWearLargeRoundPreview
@Composable
fun LibraryScreenNoSubscriptionsCatalogPreview() = NoSubscribedPodcastScreen(
    columnState = rememberTransformingLazyColumnState(),
    contentPadding = PaddingValues(),
    topPodcasts = PreviewPodcasts,
    onTogglePodcastFollowed = {},
)

@JetcasterWearSmallRoundPreview
@Composable
fun LatestEpisodeSmallCatalogPreview() = LatestEpisodeScreenLoadedPreview()

@JetcasterWearLargeRoundPreview
@Composable
fun LatestEpisodeLoadingCatalogPreview() = LatestEpisodeScreen(
    uiState = LatestEpisodeScreenState.Loading,
    placeholderState = rememberPlaceholderState(isVisible = true),
    onPlayButtonClick = {},
    onDismiss = {},
    onPlayEpisodes = {},
    onPlayEpisode = {},
)

@JetcasterWearLargeRoundPreview
@Composable
fun LatestEpisodeEmptyCatalogPreview() {
    WearEmptyDialogCatalogPreview(
        titleRes = R.string.podcasts_no_episode_podcasts,
    )
}

@JetcasterWearSmallRoundPreview
@Composable
fun EpisodeScreenSmallCatalogPreview() = EpisodeScreenLoadedPreview()

@JetcasterWearLargeRoundPreview
@Composable
fun EpisodeScreenEmptyCatalogPreview() = WearEmptyDialogCatalogPreview(titleRes = R.string.episode_info_not_available)

@JetcasterWearSmallRoundPreview
@Composable
fun QueueScreenSmallCatalogPreview() = QueueScreenLoadedPreview()

@JetcasterWearLargeRoundPreview
@Composable
fun QueueScreenLoadingCatalogPreview() = QueueScreen(
    uiState = QueueScreenState.Loading,
    placeholderState = rememberPlaceholderState(isVisible = true),
    onPlayButtonClick = {},
    onPlayEpisodes = {},
    onEpisodeItemClick = {},
    onDeleteQueueEpisodes = {},
    onDismiss = {},
)

@JetcasterWearLargeRoundPreview
@Composable
fun QueueScreenEmptyCatalogPreview() {
    WearEmptyDialogCatalogPreview(
        titleRes = R.string.display_nothing_in_queue,
        textRes = R.string.no_episodes_from_queue,
    )
}

@JetcasterWearSmallRoundPreview
@Composable
fun PodcastContentSmallCatalogPreview() = PodcastContentPreview()

@JetcasterWearSmallRoundPreview
@Composable
fun PlayerControlsSmallCatalogPreview() = MediaContentPreview()

@JetcasterWearSmallRoundPreview
@Composable
fun EpisodeLoadingSmallCatalogPreview() = EpisodeScreenLoadingPreview()

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

@Composable
private fun WearEmptyDialogCatalogPreview(@StringRes titleRes: Int, @StringRes textRes: Int? = null) {
    if (textRes == null) {
        AlertDialogContent(title = { Text(stringResource(titleRes)) })
    } else {
        AlertDialogContent(
            title = { Text(stringResource(titleRes)) },
            text = { Text(stringResource(textRes)) },
        )
    }
}
