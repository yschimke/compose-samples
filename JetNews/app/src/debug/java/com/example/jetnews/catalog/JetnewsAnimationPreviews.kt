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

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetnews.data.posts.impl.post3
import com.example.jetnews.ui.home.PostCardPopular
import com.example.jetnews.ui.home.PostCardSimple
import com.example.jetnews.ui.interests.SelectTopicButton
import com.example.jetnews.ui.theme.JetnewsTheme
import com.example.jetnews.ui.utils.BookmarkButton
import ee.schimke.composeai.preview.AnimatedPreview
import kotlinx.coroutines.delay

/**
 * Motion previews for the JetNews sticker sheet.
 *
 * These live in `src/debug` on purpose: they are catalog fixtures, not app code,
 * so they never reach a release build — but the compose-preview plugin renders
 * the debug variant, so they are discovered and captured like any other
 * `@Preview`.
 *
 * `@AnimatedPreview` records a GIF instead of a single PNG, which is the only
 * way a static sticker sheet can show what a state change actually looks like.
 * Each fixture drives a *real* JetNews component through a real state change;
 * nothing here re-implements the animation.
 */

/**
 * Zero-argument catalog stand-in for `PostCardPopular`.
 *
 * The shipped previews for this card take their post via `@PreviewParameter`.
 * Those render fine, but the renderer emits **no data products** for a preview
 * with a `@PreviewParameter` argument — no semantics, layout, fonts or
 * figma-svg — so the design-catalog completeness gate refuses to publish the
 * component ("no semantics for: PostCard/Popular"). Calling the same composable
 * with a literal fixture restores the full data-product set, so the card stays
 * on the sticker sheet.
 */
@Preview(name = "Popular post card", showBackground = true, widthDp = 280)
@Preview(
    name = "Popular post card (dark)",
    showBackground = true,
    widthDp = 280,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun PopularPostCardCatalogPreview() {
    JetnewsTheme {
        Surface {
            PostCardPopular(post = post3, navigateToPost = {})
        }
    }
}

/** Flips [state] every [everyMs] so a preview can record the transition. */
@Composable
private fun rememberToggling(everyMs: Long): Boolean {
    var state by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(everyMs)
            state = !state
        }
    }
    return state
}

/**
 * The bookmark toggle's checked/unchecked transition — an `IconToggleButton`
 * swapping its icon, captured as motion rather than as two unrelated stills.
 */
@Preview(name = "Bookmark toggle", showBackground = true, widthDp = 96, heightDp = 96)
@AnimatedPreview(durationMs = 2000, frameIntervalMs = 100, showCurves = false)
@Composable
fun BookmarkToggleAnimatedPreview() {
    val bookmarked = rememberToggling(everyMs = 500)
    JetnewsTheme {
        Surface {
            Box(Modifier.padding(24.dp)) {
                BookmarkButton(isBookmarked = bookmarked, onClick = {})
            }
        }
    }
}

/**
 * The interests subscribe affordance crossfading between its unselected and
 * selected treatments — the colour/border swap JetNews uses to confirm a topic
 * subscription.
 */
@Preview(name = "Subscribe toggle", showBackground = true, widthDp = 120, heightDp = 120)
@AnimatedPreview(durationMs = 2400, frameIntervalMs = 120, showCurves = false)
@Composable
fun SelectTopicToggleAnimatedPreview() {
    val selected = rememberToggling(everyMs = 600)
    JetnewsTheme {
        Surface {
            AnimatedContent(
                targetState = selected,
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                },
                label = "subscribe",
            ) { isSelected ->
                SelectTopicButton(modifier = Modifier.padding(32.dp), selected = isSelected)
            }
        }
    }
}

/**
 * A feed row bookmarking itself. This is the whole-row version of the toggle
 * above — it proves the row's layout does not shift when the trailing icon
 * changes, which a pair of static PNGs cannot show.
 */
// heightDp is pinned deliberately: without it the animated capture silently
// emits PNG bytes under a `.gif` filename (compose-ai-tools renderer, 0.17.17).
@Preview(name = "Feed row bookmarking", showBackground = true, widthDp = 412, heightDp = 140)
@AnimatedPreview(durationMs = 2000, frameIntervalMs = 100, showCurves = false)
@Composable
fun FeedRowBookmarkAnimatedPreview() {
    val favorite = rememberToggling(everyMs = 500)
    JetnewsTheme {
        Surface {
            PostCardSimple(
                post = post3,
                navigateToPost = {},
                isFavorite = favorite,
                onToggleFavorite = {},
            )
        }
    }
}
