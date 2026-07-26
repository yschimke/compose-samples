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

package com.example.jetnews.catalog

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.jetnews.R
import com.example.jetnews.data.AppContainer
import com.example.jetnews.data.Result
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.interests.TopicSelection
import com.example.jetnews.data.interests.impl.FakeInterestsRepository
import com.example.jetnews.data.posts.PostsRepository
import com.example.jetnews.data.posts.impl.BlockingFakePostsRepository
import com.example.jetnews.data.posts.impl.post3
import com.example.jetnews.model.PostsFeed
import com.example.jetnews.ui.AppDrawer
import com.example.jetnews.ui.JetnewsApp
import com.example.jetnews.ui.components.AppNavRail
import com.example.jetnews.ui.components.JetnewsSnackbarHost
import com.example.jetnews.ui.home.HomeFeedScreen
import com.example.jetnews.ui.home.HomeKey
import com.example.jetnews.ui.home.HomeUiState
import com.example.jetnews.ui.interests.InterestsKey
import com.example.jetnews.ui.interests.TabWithSections
import com.example.jetnews.ui.interests.TabWithTopics
import com.example.jetnews.ui.navigation.NAVIGATION_ITEMS
import com.example.jetnews.ui.post.PostScreen
import com.example.jetnews.ui.post.PostUiState
import com.example.jetnews.ui.theme.JetnewsTheme
import com.example.jetnews.utils.ErrorMessage
import kotlinx.coroutines.runBlocking

/*
 * Screen- and state-level catalog fixtures for JetNews.
 *
 * These live in `src/debug` rather than beside the components they exercise: they are
 * sticker-sheet material, not app code, so they never reach a release build — but the
 * compose-preview plugin renders the debug variant, so they are discovered and captured
 * like any other `@Preview`.
 *
 * The shipped `@Preview`s in `src/main` all show the *happy* path: a populated feed, an
 * article that loaded, an unselected toggle. The states that actually differ visually —
 * loading, empty, error, bookmarked, a selected navigation destination, the expanded
 * two-pane posture — had no preview at all. Each fixture below drives a real JetNews
 * composable with real fixture data; nothing here re-implements a screen.
 */

/** Hardcoded, synchronous data for previews — no coroutine delays, no network. */
private val previewPostsFeed: PostsFeed = runBlocking {
    (BlockingFakePostsRepository().getPostsFeed() as Result.Success).data
}

/**
 * [AppContainer] wired to the *blocking* fakes so the whole-app previews below composite a
 * populated feed on their first frame. `AppContainerImpl` uses `FakePostsRepository`, which
 * sleeps 800ms "pretending we're on a slow network" — under the renderer that is the
 * difference between a screenshot of the app and a screenshot of a spinner.
 */
private class PreviewAppContainer : AppContainer {
    override val postsRepository: PostsRepository = BlockingFakePostsRepository()
    override val interestsRepository: InterestsRepository = FakeInterestsRepository()
}

// ---------------------------------------------------------------------------
// Home feed states
// ---------------------------------------------------------------------------

/** The feed while the first load is in flight: a full-screen indeterminate spinner. */
@Preview("Home feed — loading")
@Preview("Home feed — loading (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewHomeFeedLoading() {
    JetnewsTheme {
        HomeFeedScreen(
            uiState = HomeUiState.NoPosts(
                isLoading = true,
                errorMessages = emptyList(),
                searchInput = "",
            ),
            showTopAppBar = true,
            onToggleFavorite = {},
            onSelectPost = {},
            onRefreshPosts = {},
            onErrorDismiss = {},
            openDrawer = {},
            homeListLazyListState = rememberLazyListState(),
            snackbarHostState = SnackbarHostState(),
            onSearchInputChanged = {},
        )
    }
}

/** Loaded, but the feed came back empty — the manual "tap to load content" affordance. */
@Preview("Home feed — empty")
@Preview("Home feed — empty (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewHomeFeedEmpty() {
    JetnewsTheme {
        HomeFeedScreen(
            uiState = HomeUiState.NoPosts(
                isLoading = false,
                errorMessages = emptyList(),
                searchInput = "",
            ),
            showTopAppBar = true,
            onToggleFavorite = {},
            onSelectPost = {},
            onRefreshPosts = {},
            onErrorDismiss = {},
            openDrawer = {},
            homeListLazyListState = rememberLazyListState(),
            snackbarHostState = SnackbarHostState(),
            onSearchInputChanged = {},
        )
    }
}

/**
 * The feed with three posts bookmarked. The only visual difference from the shipped
 * `PreviewHomeListDrawerScreen` is the trailing icon on each recommended row flipping from
 * outline to filled — which is exactly the kind of state a single "happy path" preview hides.
 */
@Preview("Home feed — bookmarked")
@Preview("Home feed — bookmarked (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewHomeFeedBookmarked() {
    JetnewsTheme {
        HomeFeedScreen(
            uiState = HomeUiState.HasPosts(
                postsFeed = previewPostsFeed,
                favorites = previewPostsFeed.recommendedPosts.map { it.id }.toSet(),
                isLoading = false,
                errorMessages = emptyList(),
                searchInput = "",
            ),
            showTopAppBar = true,
            onToggleFavorite = {},
            onSelectPost = {},
            onRefreshPosts = {},
            onErrorDismiss = {},
            openDrawer = {},
            homeListLazyListState = rememberLazyListState(),
            snackbarHostState = SnackbarHostState(),
            onSearchInputChanged = {},
        )
    }
}

/** A refresh that failed: the feed stays on screen and the error arrives as a snackbar. */
@Preview("Home feed — refresh error")
@Composable
fun PreviewHomeFeedError() {
    JetnewsTheme {
        HomeFeedScreen(
            uiState = HomeUiState.HasPosts(
                postsFeed = previewPostsFeed,
                favorites = emptySet(),
                isLoading = false,
                errorMessages = listOf(ErrorMessage(id = 1L, messageId = R.string.load_error)),
                searchInput = "",
            ),
            showTopAppBar = true,
            onToggleFavorite = {},
            onSelectPost = {},
            onRefreshPosts = {},
            onErrorDismiss = {},
            openDrawer = {},
            homeListLazyListState = rememberLazyListState(),
            snackbarHostState = SnackbarHostState(),
            onSearchInputChanged = {},
        )
    }
}

/** The expanded-width feed with the inline search field the compact posture hides. */
@Preview("Home feed — expanded search", widthDp = 700, heightDp = 900)
@Composable
fun PreviewHomeFeedExpandedSearch() {
    JetnewsTheme {
        HomeFeedScreen(
            uiState = HomeUiState.HasPosts(
                postsFeed = previewPostsFeed,
                favorites = emptySet(),
                isLoading = false,
                errorMessages = emptyList(),
                searchInput = "compose",
            ),
            showTopAppBar = false,
            onToggleFavorite = {},
            onSelectPost = {},
            onRefreshPosts = {},
            onErrorDismiss = {},
            openDrawer = {},
            homeListLazyListState = rememberLazyListState(),
            snackbarHostState = SnackbarHostState(),
            onSearchInputChanged = {},
        )
    }
}

// ---------------------------------------------------------------------------
// Article states
// ---------------------------------------------------------------------------

/** The article with the bookmark set — the bottom bar's toggle in its checked treatment. */
@Preview("Article — bookmarked")
@Preview("Article — bookmarked (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewPostScreenBookmarked() {
    JetnewsTheme {
        val post = runBlocking {
            (BlockingFakePostsRepository().getPost(post3.id) as Result.Success).data
        }
        PostScreen(post = post, isExpandedScreen = false, onBack = {}, isFavorite = true, onToggleFavorite = {})
    }
}

/** The article route before the post resolves. */
@Preview("Article — loading")
@Composable
fun PreviewPostScreenLoading() {
    JetnewsTheme {
        PostScreen(
            uiState = PostUiState(loading = true),
            isExpandedScreen = false,
            onBack = {},
            onToggleFavorite = {},
            onScroll = { _, _ -> },
        )
    }
}

/** A deep link to a post id that no longer exists. */
@Preview("Article — not found")
@Composable
fun PreviewPostScreenNotFound() {
    JetnewsTheme {
        PostScreen(
            uiState = PostUiState(post = null, loading = false),
            isExpandedScreen = false,
            onBack = {},
            onToggleFavorite = {},
            onScroll = { _, _ -> },
        )
    }
}

// ---------------------------------------------------------------------------
// Navigation surfaces — the other destination selected
// ---------------------------------------------------------------------------

/**
 * The drawer with Interests active. The shipped `PreviewAppDrawer` only ever shows Home
 * selected, so the selected-item container treatment was never on the sticker sheet for
 * anything but the first row.
 */
@Preview("Drawer — Interests selected")
@Preview("Drawer — Interests selected (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppDrawerInterestsSelected() {
    JetnewsTheme {
        AppDrawer(
            drawerState = rememberDrawerState(initialValue = DrawerValue.Open),
            currentTopLevelKey = InterestsKey,
            navigate = {},
            navigationItems = NAVIGATION_ITEMS,
            closeDrawer = {},
        )
    }
}

/** The same selection change on the rail: label shown for the active destination only. */
@Preview("Rail — Interests selected")
@Preview("Rail — Interests selected (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppNavRailInterestsSelected() {
    JetnewsTheme {
        AppNavRail(
            currentTopLevelKey = InterestsKey,
            navigate = {},
            navigationItems = NAVIGATION_ITEMS,
        )
    }
}

// ---------------------------------------------------------------------------
// Interests tabs — subscribed state
// ---------------------------------------------------------------------------

/** Topics tab with two topics subscribed, so the selected chip treatment is visible. */
@Preview("Topics tab — subscribed")
@Preview("Topics tab — subscribed (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewTopicsTabSubscribed() {
    val topics = runBlocking {
        (FakeInterestsRepository().getTopics() as Result.Success).data
    }
    val selected = topics.take(2).mapNotNull { section ->
        section.interests.firstOrNull()?.let { TopicSelection(section.title, it) }
    }.toSet()
    JetnewsTheme {
        Surface {
            TabWithSections(topics, selected) { }
        }
    }
}

/** People tab with the first two people followed. */
@Preview("People tab — followed")
@Composable
fun PreviewPeopleTabFollowed() {
    val people = runBlocking {
        (FakeInterestsRepository().getPeople() as Result.Success).data
    }
    JetnewsTheme {
        Surface {
            TabWithTopics(people, people.take(2).toSet()) { }
        }
    }
}

/** Publications tab with the first two publications followed. */
@Preview("Publications tab — followed")
@Composable
fun PreviewPublicationsTabFollowed() {
    val publications = runBlocking {
        (FakeInterestsRepository().getPublications() as Result.Success).data
    }
    JetnewsTheme {
        Surface {
            TabWithTopics(publications, publications.take(2).toSet()) { }
        }
    }
}

// `JetnewsSnackbarHost` deliberately has no standalone fixture. A snackbar only exists while
// `SnackbarHostState.showSnackbar` is suspended, and driving that from a `LaunchedEffect` renders
// an empty canvas: the host's enter transition never advances far enough on a bare preview clock.
// It is covered instead by `PreviewHomeFeedError` above, where the surrounding screen keeps the
// composition alive long enough for the snackbar to composite over the feed.

// ---------------------------------------------------------------------------
// Whole app — the adaptive posture the component previews cannot show
// ---------------------------------------------------------------------------

/**
 * The whole app at compact width: a single pane with the drawer behind the top app bar.
 *
 * This is the only fixture that exercises `JetnewsApp` itself — navigation state, the
 * adaptive `WindowSizeClass` branch and `ListDetailScene` included — rather than one of the
 * stateless screens underneath it.
 */
@Preview("App — compact (single pane)", widthDp = 412, heightDp = 900)
@Composable
fun PreviewJetnewsAppCompact() {
    JetnewsApp(
        appContainer = remember { PreviewAppContainer() },
        isBackEnabled = true,
        initialBackStack = listOf(HomeKey),
    )
}

/**
 * The same app at expanded width. The drawer collapses into a persistent navigation rail and
 * `ListDetailScene` splits the window into the 334dp feed plus a detail pane showing the
 * "select an article" placeholder — the sample's headline adaptive behaviour, and something
 * no screen-level `@Preview` can produce because it is decided by `JetnewsApp`.
 */
@Preview("App — expanded (list/detail)", widthDp = 1000, heightDp = 800)
@Composable
fun PreviewJetnewsAppExpanded() {
    JetnewsApp(
        appContainer = remember { PreviewAppContainer() },
        isBackEnabled = true,
        initialBackStack = listOf(HomeKey),
    )
}
