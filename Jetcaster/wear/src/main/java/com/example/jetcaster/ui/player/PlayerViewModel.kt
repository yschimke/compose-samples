/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetcaster.ui.player

import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.example.jetcaster.core.data.di.Graph
import com.example.jetcaster.core.data.repository.EpisodeStore
import com.example.jetcaster.core.data.repository.PodcastStore
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Duration
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class PlayerUiState(
    val title: String = "",
    val subTitle: String = "",
    val duration: Duration? = null,
    val podcastName: String = "",
    val author: String = "",
    val summary: String = "",
    val podcastImageUrl: String = ""
)

/**
 * ViewModel that handles the business logic and screen state of the Player screen
 */
class PlayerViewModel(
    episodeStore: EpisodeStore,
    podcastStore: PodcastStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val uiState = MutableStateFlow<PlayerUiState?>(null)

    init {
        viewModelScope.launch {
            if (savedStateHandle.get<String>("episodeUri") != null) {
                val episodeUri = Uri.decode(savedStateHandle.get<String>("episodeUri"))
                val episode = episodeStore.episodeWithUri(episodeUri).first()
                val podcast = podcastStore.podcastWithUri(episode.podcastUri).first()
                uiState.value = PlayerUiState(
                    title = episode.title,
                    duration = episode.duration,
                    podcastName = podcast.title,
                    summary = episode.summary ?: "",
                    podcastImageUrl = podcast.imageUrl ?: ""
                )
            } else {
                uiState.value = PlayerUiState(
                    title = "",
                    duration = Duration.ZERO,
                    podcastName = "Nothing to play",
                    summary = "",
                    podcastImageUrl = ""
                )
            }
        }
    }

    /**
     * Factory for PlayerViewModel that takes EpisodeStore and PodcastStore as a dependency
     */
    companion object {
        fun provideFactory(
            episodeStore: EpisodeStore = Graph.episodeStore,
            podcastStore: PodcastStore = Graph.podcastStore,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return PlayerViewModel(episodeStore, podcastStore, handle) as T
                }
            }
    }
}
