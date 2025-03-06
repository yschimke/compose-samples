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

package com.example.jetcaster

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavHostState
import com.example.jetcaster.theme.WearAppTheme
import com.example.jetcaster.ui.Episode
import com.example.jetcaster.ui.LatestEpisodes
import com.example.jetcaster.ui.PodcastDetails
import com.example.jetcaster.ui.UpNext
import com.example.jetcaster.ui.YourPodcasts
import com.example.jetcaster.ui.episode.EpisodeScreen
import com.example.jetcaster.ui.latest_episodes.LatestEpisodesScreen
import com.example.jetcaster.ui.library.LibraryScreen
import com.example.jetcaster.ui.player.PlayerScreen
import com.example.jetcaster.ui.podcast.PodcastDetailsScreen
import com.example.jetcaster.ui.podcasts.PodcastsScreen
import com.example.jetcaster.ui.queue.QueueScreen
import com.google.android.horologist.audio.ui.VolumeScreen
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.nav.composable
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToPlayer
import com.google.android.horologist.media.ui.navigation.NavigationScreen
import com.google.android.horologist.media.ui.screens.playerlibrarypager.PlayerLibraryPagerScreen

@Composable
fun WearApp() {

    val navController = rememberSwipeDismissableNavController()
    val navHostState = rememberSwipeDismissableNavHostState()
    val volumeViewModel: VolumeViewModel = viewModel(factory = VolumeViewModel.Factory)

    WearAppTheme {
        AppScaffold {
            com.google.android.horologist.compose.nav.SwipeDismissableNavHost(
                startDestination = NavigationScreen.Player(),
                navController = navController,
                modifier = Modifier.background(Color.Transparent),
                state = navHostState,
            ) {
                composable<NavigationScreen.Player> {
                    val volumeState by volumeViewModel.volumeUiState.collectAsStateWithLifecycle()
                    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })

                    PlayerLibraryPagerScreen(
                        pagerState = pagerState,
                        volumeUiState = { volumeState },
                        displayVolumeIndicatorEvents = volumeViewModel.displayIndicatorEvents,
                        playerScreen = {
                            PlayerScreen(
                                modifier = Modifier.fillMaxSize(),
                                volumeViewModel = volumeViewModel,
                                onVolumeClick = {
                                    navController.navigate(NavigationScreen.Volume)
                                }
                            )
                        },
                        libraryScreen = {
                            LibraryScreen(
                                onLatestEpisodeClick = { navController.navigate(LatestEpisodes) },
                                onYourPodcastClick = { navController.navigate(YourPodcasts) },
                                onUpNextClick = { navController.navigate(UpNext) },
                            )
                        },
                        backStack = it,
                    )
                }

                composable<NavigationScreen.Volume> {
                    ScreenScaffold(timeText = {}) {
                        VolumeScreen(volumeViewModel = volumeViewModel)
                    }
                }

                composable<LatestEpisodes> {
                    LatestEpisodesScreen(
                        onPlayButtonClick = {
                            navController.navigateToPlayer()
                        },
                        onDismiss = { navController.popBackStack() }
                    )
                }
                composable<YourPodcasts> {
                    PodcastsScreen(
                        onPodcastsItemClick = { navController.navigate(PodcastDetails(it.uri)) },
                        onDismiss = { navController.popBackStack() }
                    )
                }
                composable<PodcastDetails> {
                    PodcastDetailsScreen(
                        onPlayButtonClick = {
                            navController.navigateToPlayer()
                        },
                        onEpisodeItemClick = { navController.navigate(Episode(it.uri)) },
                        onDismiss = { navController.popBackStack() }
                    )
                }
                composable<UpNext> {
                    QueueScreen(
                        onPlayButtonClick = {
                            navController.navigateToPlayer()
                        },
                        onEpisodeItemClick = { navController.navigate(NavigationScreen.Player) },
                        onDismiss = {
                            navController.popBackStack()
                            navController.navigate(YourPodcasts)
                        }
                    )
                }
                composable<Episode> {
                    EpisodeScreen(
                        onPlayButtonClick = {
                            navController.navigateToPlayer()
                        },
                        onDismiss = {
                            navController.popBackStack()
                            navController.navigate(YourPodcasts)
                        }
                    )
                }
            }
        }
    }
}
