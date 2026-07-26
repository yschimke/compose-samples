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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetcaster.core.domain.testing.PreviewCategories
import com.example.jetcaster.core.domain.testing.PreviewEpisodes
import com.example.jetcaster.core.domain.testing.PreviewPodcastEpisodes
import com.example.jetcaster.core.domain.testing.PreviewPodcasts
import com.example.jetcaster.core.model.FilterableCategoriesModel
import com.example.jetcaster.core.model.LibraryInfo
import com.example.jetcaster.core.model.PodcastCategoryFilterResult
import com.example.jetcaster.core.model.PodcastToEpisodeInfo
import com.example.jetcaster.ui.OfflineDialog
import com.example.jetcaster.ui.home.HomeAppBar
import com.example.jetcaster.ui.home.HomeCategory
import com.example.jetcaster.ui.home.HomeScreen
import com.example.jetcaster.ui.home.HomeScreenError
import com.example.jetcaster.ui.home.PillToolbar
import com.example.jetcaster.ui.home.category.CategoryPodcastRow
import com.example.jetcaster.ui.home.category.TopPodcastRowItem
import com.example.jetcaster.ui.home.discover.ChoiceChipContent
import com.example.jetcaster.ui.home.discover.PodcastCategoryTabs
import com.example.jetcaster.ui.player.PlayerButtons
import com.example.jetcaster.ui.player.PlayerSlider
import com.example.jetcaster.ui.player.PodcastInformation
import com.example.jetcaster.ui.player.TopAppBar
import com.example.jetcaster.ui.podcast.PodcastDetailsContent
import com.example.jetcaster.ui.podcast.PodcastDetailsDescription
import com.example.jetcaster.ui.podcast.PodcastDetailsHeaderItem
import com.example.jetcaster.ui.podcast.PodcastDetailsHeaderItemButtons
import com.example.jetcaster.ui.podcast.PodcastDetailsScreen
import com.example.jetcaster.ui.podcast.PodcastDetailsTopAppBar
import com.example.jetcaster.ui.shared.EpisodeListItem
import com.example.jetcaster.ui.shared.Loading
import com.example.jetcaster.ui.theme.JetcasterTheme
import com.example.jetcaster.util.ToggleFollowPodcastIconButton
import java.time.Duration
import kotlinx.collections.immutable.toImmutableList

/**
 * `@Preview`s for Jetcaster's phone (`:mobile`) components and screens.
 *
 * Jetcaster ships ~23 `@Preview`s across four form factors, but the phone tier had only seven, and
 * every one of them showed a single resting state: the podcast card with an *empty* title and image
 * URL, the transport controls only while playing, the app bar only while collapsed, the subscribe
 * button never at all. The states that actually differ visually — followed vs not, subscribed vs
 * not, playing vs paused, an episode row with artwork vs the artwork-less variant the details screen
 * uses, a description short enough to fit vs one that needs "see more" — were unreachable without
 * running the app against a live Room database and an RSS feed.
 *
 * These previews drive each surface directly from the sample data the module already ships
 * ([PreviewPodcasts], [PreviewEpisodes], [PreviewPodcastEpisodes] in `:core:domain-testing`), so no
 * repository, database or network is touched. Where the shipped fixtures are deliberately sparse
 * (empty descriptions, no durations) they are `copy()`-ed into the richer shapes real feeds produce,
 * which is what makes the overflow and truncation states reviewable.
 *
 * They live in the `debug` source set, so nothing here reaches a release build. Composables that
 * were file-`private` were widened to `internal` — the debug and main source sets are one Kotlin
 * compilation, so `internal` is the narrowest visibility that reaches them.
 *
 * Most previews here are dark, because dark is what Jetcaster is designed around, but light mode is
 * now real: [JetcasterTheme] used to resolve the dark scheme unconditionally and now follows the
 * system setting, so the light half of the design system is reachable for the first time. The
 * light-mode section below covers the surfaces where the palette swap actually changes something.
 * The medium- and high-contrast schemes have no code path that selects them, so those remain
 * exposed as `@ThemeCatalog`s — see `JetcasterThemeCatalogs.kt`.
 */

// ---------------------------------------------------------------- sample data

private const val LONG_DESCRIPTION =
    "Android Developers Backstage is a podcast by and for Android developers. Hosted by " +
        "developers from the Android platform team, this show covers topics of interest to " +
        "Android programmers, with in-depth discussions and interviews with engineers on the " +
        "Android team at Google. Subscribe to hear the people who build the platform talk about " +
        "how and why it works the way it does."

private val subscribedPodcast = PreviewPodcasts[0].copy(
    description = LONG_DESCRIPTION,
    isSubscribed = true,
)

private val unsubscribedPodcast = PreviewPodcasts[1].copy(
    description = "A short show note that fits comfortably inside three lines.",
    isSubscribed = false,
)

private val longTitlePodcast = subscribedPodcast.copy(
    title = "Android Developers Backstage: The Extended Director's Cut Of Everything " +
        "That Did Not Fit In The Main Feed",
)

private val episode = PreviewEpisodes[0].copy(duration = Duration.ofMinutes(52))

// A distinct `uri` matters: it is the LazyColumn key, and the shipped fixture list holds exactly one
// episode, so a plain copy() of it collides with itself the moment two rows share a list.
private val longTitleEpisode = PreviewEpisodes[0].copy(
    uri = "fakeUri://episode/2",
    title = "Episode 141: A remarkably long episode title that has to wrap onto a second line " +
        "and then be truncated with an ellipsis",
    duration = null,
)

private val library = LibraryInfo(
    episodes = PreviewPodcastEpisodes +
        PodcastToEpisodeInfo(podcast = unsubscribedPodcast, episode = longTitleEpisode),
)

// The mode is pinned explicitly rather than left to isSystemInDarkTheme(), so a render is never at
// the mercy of the harness's default uiMode. Dark is the default because dark is what Jetcaster is
// designed around; the light variants below exist to exercise the other half of the design system,
// which JetcasterTheme only started resolving once it stopped hardcoding darkScheme.
@Composable
private fun Wrap(dark: Boolean = true, content: @Composable () -> Unit) = JetcasterTheme(darkTheme = dark) {
    Surface { Box(Modifier.padding(8.dp)) { content() } }
}

// ---------------------------------------------------------------- follow / subscribe affordances

@Preview(name = "FollowToggle — not following", showBackground = true)
@Composable
fun JetcasterFollowToggleUnfollowedPreview() = Wrap {
    ToggleFollowPodcastIconButton(isFollowed = false, onClick = {})
}

@Preview(name = "FollowToggle — following", showBackground = true)
@Composable
fun JetcasterFollowToggleFollowedPreview() = Wrap {
    ToggleFollowPodcastIconButton(isFollowed = true, onClick = {})
}

@Preview(name = "SubscribeButtons — not subscribed", showBackground = true, widthDp = 412)
@Composable
fun JetcasterSubscribeButtonsPreview() = Wrap {
    PodcastDetailsHeaderItemButtons(isSubscribed = false, onClick = {}, modifier = Modifier.fillMaxWidth())
}

@Preview(name = "SubscribeButtons — subscribed", showBackground = true, widthDp = 412)
@Composable
fun JetcasterSubscribeButtonsSubscribedPreview() = Wrap {
    PodcastDetailsHeaderItemButtons(isSubscribed = true, onClick = {}, modifier = Modifier.fillMaxWidth())
}

// ---------------------------------------------------------------- discover surfaces

@Preview(name = "TopPodcastRowItem — not following", showBackground = true, widthDp = 160)
@Composable
fun JetcasterTopPodcastRowItemPreview() = Wrap {
    TopPodcastRowItem(
        podcastTitle = unsubscribedPodcast.title,
        podcastImageUrl = unsubscribedPodcast.imageUrl,
        isFollowed = false,
        onToggleFollowClicked = {},
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview(name = "TopPodcastRowItem — following", showBackground = true, widthDp = 160)
@Composable
fun JetcasterTopPodcastRowItemFollowedPreview() = Wrap {
    TopPodcastRowItem(
        podcastTitle = subscribedPodcast.title,
        podcastImageUrl = subscribedPodcast.imageUrl,
        isFollowed = true,
        onToggleFollowClicked = {},
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview(name = "TopPodcastRowItem — long title", showBackground = true, widthDp = 160)
@Composable
fun JetcasterTopPodcastRowItemLongTitlePreview() = Wrap {
    TopPodcastRowItem(
        podcastTitle = longTitlePodcast.title,
        podcastImageUrl = longTitlePodcast.imageUrl,
        isFollowed = true,
        onToggleFollowClicked = {},
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview(name = "CategoryPodcastRow", showBackground = true, widthDp = 412, heightDp = 200)
@Composable
fun JetcasterCategoryPodcastRowPreview() = Wrap {
    CategoryPodcastRow(
        podcasts = listOf(subscribedPodcast, unsubscribedPodcast, longTitlePodcast),
        onTogglePodcastFollowed = {},
        navigateToPodcastDetails = {},
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview(name = "CategoryChip — unselected", showBackground = true)
@Composable
fun JetcasterCategoryChipPreview() = Wrap {
    ChoiceChipContent(text = "Comedy", selected = false, onClick = {})
}

@Preview(name = "CategoryChip — selected", showBackground = true)
@Composable
fun JetcasterCategoryChipSelectedPreview() = Wrap {
    ChoiceChipContent(text = "Comedy", selected = true, onClick = {})
}

@Preview(name = "CategoryTabs", showBackground = true, widthDp = 412)
@Composable
fun JetcasterCategoryTabsPreview() = Wrap {
    PodcastCategoryTabs(
        filterableCategoriesModel = FilterableCategoriesModel(
            categories = PreviewCategories,
            selectedCategory = PreviewCategories[1],
        ),
        onCategorySelected = {},
        modifier = Modifier.fillMaxWidth(),
    )
}

// ---------------------------------------------------------------- episode rows

@Preview(name = "EpisodeListItem", showBackground = true, widthDp = 412)
@Composable
fun JetcasterEpisodeRowPreview() = Wrap {
    EpisodeListItem(
        episode = episode,
        podcast = subscribedPodcast,
        onClick = {},
        onQueueEpisode = {},
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview(name = "EpisodeListItem — with summary", showBackground = true, widthDp = 412)
@Composable
fun JetcasterEpisodeRowSummaryPreview() = Wrap {
    EpisodeListItem(
        episode = episode,
        podcast = subscribedPodcast,
        onClick = {},
        onQueueEpisode = {},
        showSummary = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview(name = "EpisodeListItem — no artwork", showBackground = true, widthDp = 412)
@Composable
fun JetcasterEpisodeRowNoArtworkPreview() = Wrap {
    EpisodeListItem(
        episode = episode,
        podcast = subscribedPodcast,
        onClick = {},
        onQueueEpisode = {},
        showPodcastImage = false,
        showSummary = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview(name = "EpisodeListItem — long title, no duration", showBackground = true, widthDp = 412)
@Composable
fun JetcasterEpisodeRowLongTitlePreview() = Wrap {
    EpisodeListItem(
        episode = longTitleEpisode,
        podcast = longTitlePodcast,
        onClick = {},
        onQueueEpisode = {},
        modifier = Modifier.fillMaxWidth(),
    )
}

// ---------------------------------------------------------------- podcast details

@Preview(name = "PodcastDetails header — subscribed", showBackground = true, widthDp = 412, heightDp = 640)
@Composable
fun JetcasterPodcastDetailsHeaderPreview() = Wrap {
    PodcastDetailsHeaderItem(
        podcast = subscribedPodcast,
        toggleSubscribe = {},
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview(name = "PodcastDetails header — not subscribed", showBackground = true, widthDp = 412, heightDp = 640)
@Composable
fun JetcasterPodcastDetailsHeaderUnsubscribedPreview() = Wrap {
    PodcastDetailsHeaderItem(
        podcast = unsubscribedPodcast,
        toggleSubscribe = {},
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview(name = "PodcastDetails header — long title", showBackground = true, widthDp = 412, heightDp = 640)
@Composable
fun JetcasterPodcastDetailsHeaderLongTitlePreview() = Wrap {
    PodcastDetailsHeaderItem(
        podcast = longTitlePodcast,
        toggleSubscribe = {},
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview(name = "PodcastDetails description — short", showBackground = true, widthDp = 412)
@Composable
fun JetcasterPodcastDescriptionPreview() = Wrap {
    PodcastDetailsDescription(podcast = unsubscribedPodcast, modifier = Modifier.fillMaxWidth())
}

@Preview(name = "PodcastDetails description — overflowing", showBackground = true, widthDp = 412)
@Composable
fun JetcasterPodcastDescriptionOverflowPreview() = Wrap {
    PodcastDetailsDescription(podcast = subscribedPodcast, modifier = Modifier.fillMaxWidth())
}

@Preview(name = "PodcastDetails app bar", showBackground = true, widthDp = 412)
@Composable
fun JetcasterPodcastDetailsTopAppBarPreview() = Wrap {
    PodcastDetailsTopAppBar(navigateBack = {}, modifier = Modifier.fillMaxWidth())
}

@Preview(name = "PodcastDetails content", showBackground = true, widthDp = 412, heightDp = 900)
@Composable
fun JetcasterPodcastDetailsContentPreview() = Wrap {
    PodcastDetailsContent(
        podcast = subscribedPodcast,
        episodes = listOf(episode, longTitleEpisode),
        removeFromQueue = {},
        toggleSubscribe = {},
        onQueueEpisode = {},
        navigateToPlayer = {},
    )
}

@Preview(name = "PodcastDetails screen — full screen", showBackground = true, widthDp = 412, heightDp = 900)
@Composable
fun JetcasterPodcastDetailsFullScreenPreview() = Wrap {
    PodcastDetailsScreen(
        podcast = subscribedPodcast,
        episodes = listOf(episode, longTitleEpisode),
        toggleSubscribe = {},
        onQueueEpisode = {},
        navigateToPlayer = {},
        navigateBack = {},
        showBackButton = true,
    )
}

@Preview(name = "PodcastDetails screen — supporting pane", showBackground = true, widthDp = 412, heightDp = 900)
@Composable
fun JetcasterPodcastDetailsPanePreview() = Wrap {
    PodcastDetailsScreen(
        podcast = subscribedPodcast,
        episodes = listOf(episode, longTitleEpisode),
        toggleSubscribe = {},
        onQueueEpisode = {},
        navigateToPlayer = {},
        navigateBack = {},
        showBackButton = false,
    )
}

// ---------------------------------------------------------------- player

@Preview(name = "PlayerButtons — playing", showBackground = true, widthDp = 412, heightDp = 300)
@Composable
fun JetcasterPlayerButtonsPlayingPreview() = Wrap {
    PlayerButtons(
        hasNext = true,
        isPlaying = true,
        onPlayPress = {},
        onPausePress = {},
        onAdvanceBy = {},
        onRewindBy = {},
        onNext = {},
        onPrevious = {},
    )
}

@Preview(name = "PlayerButtons — paused", showBackground = true, widthDp = 412, heightDp = 300)
@Composable
fun JetcasterPlayerButtonsPausedPreview() = Wrap {
    PlayerButtons(
        hasNext = true,
        isPlaying = false,
        onPlayPress = {},
        onPausePress = {},
        onAdvanceBy = {},
        onRewindBy = {},
        onNext = {},
        onPrevious = {},
    )
}

@Preview(name = "PlayerButtons — empty queue", showBackground = true, widthDp = 412, heightDp = 300)
@Composable
fun JetcasterPlayerButtonsNoQueuePreview() = Wrap {
    PlayerButtons(
        hasNext = false,
        isPlaying = false,
        onPlayPress = {},
        onPausePress = {},
        onAdvanceBy = {},
        onRewindBy = {},
        onNext = {},
        onPrevious = {},
    )
}

@Preview(name = "PlayerSlider — start", showBackground = true, widthDp = 412)
@Composable
fun JetcasterPlayerSliderPreview() = Wrap {
    PlayerSlider(
        timeElapsed = Duration.ZERO,
        episodeDuration = Duration.ofMinutes(52),
        onSeekingStarted = {},
        onSeekingFinished = {},
    )
}

@Preview(name = "PlayerSlider — part way through", showBackground = true, widthDp = 412)
@Composable
fun JetcasterPlayerSliderElapsedPreview() = Wrap {
    PlayerSlider(
        timeElapsed = Duration.ofMinutes(18),
        episodeDuration = Duration.ofMinutes(52),
        onSeekingStarted = {},
        onSeekingFinished = {},
    )
}

@Preview(name = "Player app bar", showBackground = true, widthDp = 412)
@Composable
fun JetcasterPlayerTopAppBarPreview() = Wrap {
    TopAppBar(onBackPress = {}, onAddToQueue = {})
}

@Preview(name = "PodcastInformation", showBackground = true, widthDp = 412, heightDp = 420)
@Composable
fun JetcasterPodcastInformationPreview() = Wrap {
    PodcastInformation(
        title = episode.title,
        name = subscribedPodcast.title,
        summary = episode.summary,
        modifier = Modifier.fillMaxWidth(),
    )
}

// ---------------------------------------------------------------- chrome and states

@Preview(name = "HomeAppBar — compact", showBackground = true, widthDp = 412)
@Composable
fun JetcasterHomeAppBarPreview() = Wrap {
    HomeAppBar(isExpanded = false, modifier = Modifier.fillMaxWidth())
}

@Preview(name = "HomeAppBar — expanded", showBackground = true, widthDp = 412)
@Composable
fun JetcasterHomeAppBarExpandedPreview() = Wrap {
    HomeAppBar(isExpanded = true, modifier = Modifier.fillMaxWidth())
}

@Preview(name = "PillToolbar — Discover selected", showBackground = true, widthDp = 412)
@Composable
fun JetcasterPillToolbarDiscoverPreview() = Wrap {
    PillToolbar(selectedHomeCategory = HomeCategory.Discover, onHomeAction = {})
}

@Preview(name = "PillToolbar — Library selected", showBackground = true, widthDp = 412)
@Composable
fun JetcasterPillToolbarLibraryPreview() = Wrap {
    PillToolbar(selectedHomeCategory = HomeCategory.Library, onHomeAction = {})
}

@Preview(name = "Loading", showBackground = true, widthDp = 412, heightDp = 300)
@Composable
fun JetcasterLoadingPreview() = Wrap { Loading(modifier = Modifier.fillMaxWidth()) }

@Preview(name = "Home — error", showBackground = true, widthDp = 412, heightDp = 400)
@Composable
fun JetcasterHomeErrorPreview() = Wrap { HomeScreenError(onRetry = {}) }

@Preview(name = "Offline dialog", showBackground = true, widthDp = 412, heightDp = 400)
@Composable
fun JetcasterOfflineDialogPreview() = Wrap { OfflineDialog(onRetry = {}) }

// ---------------------------------------------------------------- light mode
//
// JetcasterTheme used to resolve darkScheme unconditionally, so none of these were reachable. They
// cover the surfaces where swapping the palette actually changes something worth reviewing: filled
// containers, selected-state colouring, and text over artwork.

@Preview(name = "EpisodeListItem — light", showBackground = true, widthDp = 412)
@Composable
fun JetcasterEpisodeRowLightPreview() = Wrap(dark = false) {
    EpisodeListItem(
        episode = episode,
        podcast = subscribedPodcast,
        onClick = {},
        onQueueEpisode = {},
        showSummary = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview(name = "PodcastDetails header — light", showBackground = true, widthDp = 412, heightDp = 640)
@Composable
fun JetcasterPodcastDetailsHeaderLightPreview() = Wrap(dark = false) {
    PodcastDetailsHeaderItem(
        podcast = subscribedPodcast,
        toggleSubscribe = {},
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview(name = "PlayerButtons — light", showBackground = true, widthDp = 412, heightDp = 300)
@Composable
fun JetcasterPlayerButtonsLightPreview() = Wrap(dark = false) {
    PlayerButtons(
        hasNext = true,
        isPlaying = true,
        onPlayPress = {},
        onPausePress = {},
        onAdvanceBy = {},
        onRewindBy = {},
        onNext = {},
        onPrevious = {},
    )
}

@Preview(name = "PillToolbar — light", showBackground = true, widthDp = 412)
@Composable
fun JetcasterPillToolbarLightPreview() = Wrap(dark = false) {
    PillToolbar(selectedHomeCategory = HomeCategory.Library, onHomeAction = {})
}

@Preview(name = "TopPodcastRowItem — light", showBackground = true, widthDp = 160)
@Composable
fun JetcasterTopPodcastRowItemLightPreview() = Wrap(dark = false) {
    TopPodcastRowItem(
        podcastTitle = subscribedPodcast.title,
        podcastImageUrl = subscribedPodcast.imageUrl,
        isFollowed = true,
        onToggleFollowClicked = {},
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview(name = "Home — library, light", showBackground = true, widthDp = 412, heightDp = 900)
@Composable
fun JetcasterHomeLibraryLightPreview() = JetcasterTheme(darkTheme = false) {
    HomeScreen(
        isHomeAppBarExpanded = true,
        isLoading = false,
        featuredPodcasts = listOf(subscribedPodcast, unsubscribedPodcast).toImmutableList(),
        homeCategories = HomeCategory.entries,
        selectedHomeCategory = HomeCategory.Library,
        filterableCategoriesModel = FilterableCategoriesModel(
            categories = PreviewCategories,
            selectedCategory = PreviewCategories.first(),
        ),
        podcastCategoryFilterResult = PodcastCategoryFilterResult(
            topPodcasts = PreviewPodcasts,
            episodes = PreviewPodcastEpisodes,
        ),
        library = library,
        onHomeAction = {},
        navigateToPodcastDetails = {},
        navigateToPlayer = {},
    )
}

// ---------------------------------------------------------------- home screen

@Preview(name = "Home — library", showBackground = true, widthDp = 412, heightDp = 900)
@Composable
fun JetcasterHomeLibraryPreview() = JetcasterTheme(darkTheme = true) {
    HomeScreen(
        isHomeAppBarExpanded = true,
        isLoading = false,
        featuredPodcasts = listOf(subscribedPodcast, unsubscribedPodcast).toImmutableList(),
        homeCategories = HomeCategory.entries,
        selectedHomeCategory = HomeCategory.Library,
        filterableCategoriesModel = FilterableCategoriesModel(
            categories = PreviewCategories,
            selectedCategory = PreviewCategories.first(),
        ),
        podcastCategoryFilterResult = PodcastCategoryFilterResult(
            topPodcasts = PreviewPodcasts,
            episodes = PreviewPodcastEpisodes,
        ),
        library = library,
        onHomeAction = {},
        navigateToPodcastDetails = {},
        navigateToPlayer = {},
    )
}

@Preview(name = "Home — library, refreshing", showBackground = true, widthDp = 412, heightDp = 900)
@Composable
fun JetcasterHomeLoadingPreview() = JetcasterTheme(darkTheme = true) {
    HomeScreen(
        isHomeAppBarExpanded = true,
        isLoading = true,
        featuredPodcasts = listOf(subscribedPodcast, unsubscribedPodcast).toImmutableList(),
        homeCategories = HomeCategory.entries,
        selectedHomeCategory = HomeCategory.Library,
        filterableCategoriesModel = FilterableCategoriesModel(
            categories = PreviewCategories,
            selectedCategory = PreviewCategories.first(),
        ),
        podcastCategoryFilterResult = PodcastCategoryFilterResult(),
        library = LibraryInfo(),
        onHomeAction = {},
        navigateToPodcastDetails = {},
        navigateToPlayer = {},
    )
}
