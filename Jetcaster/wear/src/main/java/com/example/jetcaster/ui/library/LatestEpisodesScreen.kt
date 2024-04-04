/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.jetcaster.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.ChipDefaults
import com.example.jetcaster.R
import com.example.jetcaster.core.data.database.model.EpisodeToPodcast
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberColumnState
import com.google.android.horologist.compose.material.Button
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.images.base.util.rememberVectorPainter
import com.google.android.horologist.images.coil.CoilPaintable
import com.google.android.horologist.media.ui.screens.entity.DefaultEntityScreenHeader
import com.google.android.horologist.media.ui.screens.entity.EntityScreen

@Composable
fun LatestEpisodesScreen(
    playlistName: String,
    onShuffleButtonClick: (List<EpisodeToPodcast>) -> Unit,
    onPlayButtonClick: (List<EpisodeToPodcast>) -> Unit,
    modifier: Modifier = Modifier,
    latestEpisodeViewModel: LatestEpisodeViewModel = viewModel(),
) {
    val viewState by latestEpisodeViewModel.state.collectAsStateWithLifecycle()

    LatestEpisodeScreen(
        modifier = modifier,
        playlistName = playlistName,
        viewState = viewState,
        onShuffleButtonClick = onShuffleButtonClick,
        onPlayButtonClick = onPlayButtonClick,
    )
}

@Composable
fun LatestEpisodeScreen(
    playlistName: String,
    viewState: LatestEpisodeViewState,
    onShuffleButtonClick: (List<EpisodeToPodcast>) -> Unit,
    onPlayButtonClick: (List<EpisodeToPodcast>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val columnState = rememberColumnState()

    ScreenScaffold(
        scrollState = columnState,
        modifier = modifier
    ) {
        EntityScreen(
            modifier = modifier,
            columnState = columnState,
            headerContent = { DefaultEntityScreenHeader(title = playlistName) },
            content = {
                items(count = viewState.libraryEpisodes.size) { index ->
                    MediaContent(
                        episode = viewState.libraryEpisodes[index],
                        downloadItemArtworkPlaceholder = rememberVectorPainter(
                            image = Icons.Default.MusicNote,
                            tintColor = Color.Blue,
                        )
                    )
                }
            },
            buttonsContent = {
                ButtonsContent(
                    viewState = viewState,
                    onShuffleButtonClick = onShuffleButtonClick,
                    onPlayButtonClick = onPlayButtonClick,
                )
            },
        )
    }
}

@Composable
fun MediaContent(
    episode: EpisodeToPodcast,
    downloadItemArtworkPlaceholder: Painter?
) {
    val mediaTitle = episode.episode.title

    val secondaryLabel = episode.episode.author

    Chip(
        label = mediaTitle,
        onClick = { /*play*/ },
        secondaryLabel = secondaryLabel,
        icon = CoilPaintable(episode.podcast.imageUrl, downloadItemArtworkPlaceholder),
        largeIcon = true,
        colors = ChipDefaults.secondaryChipColors(),
    )
}

@Composable
fun ButtonsContent(
    viewState: LatestEpisodeViewState,
    onShuffleButtonClick: (List<EpisodeToPodcast>) -> Unit,
    onPlayButtonClick: (List<EpisodeToPodcast>) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
    ) {
        Button(
            imageVector = ImageVector.vectorResource(R.drawable.speed),
            contentDescription = stringResource(id = R.string.speed_button_content_description),
            onClick = { onShuffleButtonClick(viewState.libraryEpisodes) },
            modifier = Modifier
                .weight(weight = 0.3F, fill = false),
        )

        Button(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = stringResource(id = R.string.button_play_content_description),
            onClick = { onPlayButtonClick(viewState.libraryEpisodes) },
            modifier = Modifier
                .weight(weight = 0.3F, fill = false),
        )
    }
}
