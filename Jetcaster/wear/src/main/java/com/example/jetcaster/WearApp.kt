package com.example.jetcaster

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavHostState
import com.example.jetcaster.theme.WearAppTheme
import com.example.jetcaster.ui.JetcasterNavController.navigateToLatestEpisode
import com.example.jetcaster.ui.JetcasterNavController.navigateToUpNext
import com.example.jetcaster.ui.JetcasterNavController.navigateToYourPodcast
import com.example.jetcaster.ui.LatestEpisodes
import com.example.jetcaster.ui.home.HomeScreen
import com.example.jetcaster.ui.library.LatestEpisodeViewModel
import com.example.jetcaster.ui.library.LatestEpisodesScreen
import com.example.jetcaster.ui.player.PlayerScreen
import com.example.jetcaster.ui.player.PlayerViewModel
import com.google.android.horologist.audio.ui.VolumeViewModel
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToPlayer
import com.google.android.horologist.media.ui.navigation.MediaNavController.navigateToVolume
import com.google.android.horologist.media.ui.navigation.MediaPlayerScaffold
import com.google.android.horologist.media.ui.snackbar.SnackbarManager
import com.google.android.horologist.media.ui.snackbar.SnackbarViewModel

@Composable
fun WearApp() {

    val navController = rememberSwipeDismissableNavController()
    val navHostState = rememberSwipeDismissableNavHostState()
    val volumeViewModel: VolumeViewModel = viewModel(factory = VolumeViewModel.Factory)

    // TODO remove from MediaPlayerScaffold
    val snackBarManager: SnackbarManager = SnackbarManager()
    val snackbarViewModel: SnackbarViewModel = SnackbarViewModel(snackBarManager)

    WearAppTheme {
        MediaPlayerScaffold(
            playerScreen = {
                PlayerScreen(
                    modifier = Modifier.fillMaxSize(),
                    volumeViewModel = volumeViewModel,
                    onVolumeClick = {
                        navController.navigateToVolume()
                    },
                )
            },
            libraryScreen = {
                HomeScreen(
                    onLatestEpisodeClick = { navController.navigateToLatestEpisode() },
                    onYourPodcastClick = { navController.navigateToYourPodcast() },
                    onUpNextClick = { navController.navigateToUpNext() }
                )
            },
            categoryEntityScreen = { _, _ -> },
            mediaEntityScreen = {},
            playlistsScreen = {},
            settingsScreen = {},

            navHostState = navHostState,
            snackbarViewModel = snackbarViewModel,
            volumeViewModel = volumeViewModel,
            deepLinkPrefix = "",
            navController = navController,
            additionalNavRoutes = {
                composable(
                    route = LatestEpisodes.navRoute,
                ) {
                    LatestEpisodesScreen(
                        playlistName = stringResource(id = R.string.latest_episodes),
                        onShuffleButtonClick = {
                            // navController.navigateToPlayer(it[0].episode.uri)
                        },
                        onPlayButtonClick = {
                            navController.navigateToPlayer()
                        }
                    )
                }
            },

            )
    }
}